package com.securebank.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securebank.common.exception.ResourceNotFoundException;
import com.securebank.user.config.SecurityConfig;
import com.securebank.user.domain.UserRole;
import com.securebank.user.domain.UserStatus;
import com.securebank.user.dto.request.UserRegistrationRequest;
import com.securebank.user.dto.response.UserResponse;
import com.securebank.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class) // Loads only web layer — fast
@Import(SecurityConfig.class) // Import your security config
@DisplayName("UserController Tests")
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper; // For converting objects to JSON

  @MockBean // Spring-managed mock — replaces real bean
  private UserService userService;

  private UserRegistrationRequest validRequest;
  private UserResponse userResponse;

  @BeforeEach
  void setUp() {
    validRequest = new UserRegistrationRequest();
    validRequest.setFirstName("John");
    validRequest.setLastName("Doe");
    validRequest.setEmail("john.doe@securebank.com");
    validRequest.setPassword("SecurePass@123");
    validRequest.setPhoneNumber("+447911123456");

    userResponse = new UserResponse();
    userResponse.setId("test-uuid-123");
    userResponse.setFirstName("John");
    userResponse.setLastName("Doe");
    userResponse.setEmail("john.doe@securebank.com");
    userResponse.setStatus(UserStatus.PENDING_VERIFICATION);
    userResponse.setRole(UserRole.CUSTOMER);
  }

  @Test
  @DisplayName("POST /register - Should return 201 when valid request")
  void shouldReturn201WhenValidRegistrationRequest() throws Exception {
    // GIVEN
    given(userService.registerUser(any(UserRegistrationRequest.class))).willReturn(userResponse);

    // WHEN & THEN
    mockMvc
        .perform(
            post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.email").value("john.doe@securebank.com"))
        .andExpect(jsonPath("$.data.password").doesNotExist()); // Never expose password
  }

  @Test
  @DisplayName("POST /register - Should return 400 when email is invalid")
  void shouldReturn400WhenEmailIsInvalid() throws Exception {
    // GIVEN — invalid email
    validRequest.setEmail("not-an-email");

    // WHEN & THEN
    mockMvc
        .perform(
            post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.validationErrors.email").exists());
  }

  @Test
  @DisplayName("POST /register - Should return 400 when required fields are missing")
  void shouldReturn400WhenRequiredFieldsMissing() throws Exception {
    // GIVEN — empty request
    mockMvc
        .perform(
            post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.validationErrors").exists());
  }

  @Test
  @DisplayName("GET /{id} - Should return 404 when user not found")
  @WithMockUser
  void shouldReturn404WhenUserNotFound() throws Exception {
    // GIVEN
    given(userService.getUserById("bad-id"))
        .willThrow(new ResourceNotFoundException("User", "id", "bad-id"));

    // WHEN & THEN
    mockMvc
        .perform(get("/api/v1/users/bad-id"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").doesNotExist())
        .andExpect(jsonPath("$.message").exists());
  }
}
