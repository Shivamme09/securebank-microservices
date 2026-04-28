package com.securebank.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

import com.securebank.common.exception.ResourceAlreadyExistsException;
import com.securebank.common.exception.ResourceNotFoundException;
import com.securebank.user.domain.User;
import com.securebank.user.domain.UserRole;
import com.securebank.user.domain.UserStatus;
import com.securebank.user.dto.request.UpdateUserRequest;
import com.securebank.user.dto.request.UserRegistrationRequest;
import com.securebank.user.dto.response.UserResponse;
import com.securebank.user.mapper.UserMapper;
import com.securebank.user.repository.UserRepository;
import com.securebank.user.service.impl.UserServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)   // Enables Mockito annotations — no Spring context loaded
@DisplayName("UserService Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks                       // Creates UserServiceImpl and injects all @Mocks
    private UserServiceImpl userService;

    // Test data — built once, reused across tests
    private UserRegistrationRequest registrationRequest;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstName("John");
        registrationRequest.setLastName("Doe");
        registrationRequest.setEmail("john.doe@securebank.com");
        registrationRequest.setPassword("SecurePass@123");
        registrationRequest.setPhoneNumber("+447911123456");

        user = User.builder()
                .id("test-uuid-123")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@securebank.com")
                .password("encodedPassword")
                .phoneNumber("+447911123456")
                .status(UserStatus.PENDING_VERIFICATION)
                .role(UserRole.CUSTOMER)
                .build();

        userResponse = new UserResponse();
        userResponse.setId("test-uuid-123");
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setEmail("john.doe@securebank.com");
        userResponse.setStatus(UserStatus.PENDING_VERIFICATION);
        userResponse.setRole(UserRole.CUSTOMER);
    }

    // ─── Nested classes group related tests — clean and readable ───

    @Nested
    @DisplayName("Register User Tests")
    class RegisterUserTests {

        @Test
        @DisplayName("Should register user successfully when valid request provided")
        void shouldRegisterUserSuccessfully() {
            // GIVEN — set up mock behaviour (BDD style)
            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(userRepository.existsByPhoneNumber(anyString())).willReturn(false);
            given(userMapper.toEntity(any(UserRegistrationRequest.class))).willReturn(user);
            given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
            given(userRepository.save(any(User.class))).willReturn(user);
            given(userMapper.toResponse(any(User.class))).willReturn(userResponse);

            // WHEN — call the method under test
            UserResponse result = userService.registerUser(registrationRequest);

            // THEN — assert the outcome
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("john.doe@securebank.com");
            assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
            assertThat(result.getRole()).isEqualTo(UserRole.CUSTOMER);

            // Verify interactions
            then(userRepository).should().save(any(User.class));
            then(passwordEncoder).should().encode("SecurePass@123");
        }

        @Test
        @DisplayName("Should throw ResourceAlreadyExistsException when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // GIVEN
            given(userRepository.existsByEmail(registrationRequest.getEmail()))
                    .willReturn(true);  // Email already taken

            // WHEN & THEN — assertThatThrownBy is AssertJ's clean exception testing
            assertThatThrownBy(() -> userService.registerUser(registrationRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("User")
                    .hasMessageContaining("email");

            // Verify save was NEVER called — important negative assertion
            then(userRepository).should(never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw ResourceAlreadyExistsException when phone number already exists")
        void shouldThrowExceptionWhenPhoneNumberAlreadyExists() {
            // GIVEN
            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(userRepository.existsByPhoneNumber(registrationRequest.getPhoneNumber()))
                    .willReturn(true);

            // WHEN & THEN
            assertThatThrownBy(() -> userService.registerUser(registrationRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("phoneNumber");

            then(userRepository).should(never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should return user when valid ID provided")
        void shouldReturnUserWhenValidIdProvided() {
            // GIVEN
            given(userRepository.findById("test-uuid-123")).willReturn(Optional.of(user));
            given(userMapper.toResponse(user)).willReturn(userResponse);

            // WHEN
            UserResponse result = userService.getUserById("test-uuid-123");

            // THEN
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("test-uuid-123");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user ID does not exist")
        void shouldThrowExceptionWhenUserNotFound() {
            // GIVEN
            given(userRepository.findById(anyString())).willReturn(Optional.empty());

            // WHEN & THEN
            assertThatThrownBy(() -> userService.getUserById("non-existent-id"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User")
                    .hasMessageContaining("non-existent-id");
        }

        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() {
            // GIVEN
            given(userRepository.findAll()).willReturn(List.of(user));
            given(userMapper.toResponse(user)).willReturn(userResponse);

            // WHEN
            List<UserResponse> results = userService.getAllUsers();

            // THEN
            assertThat(results).isNotNull()
                    .hasSize(1)
                    .first()
                    .extracting(UserResponse::getEmail)
                    .isEqualTo("john.doe@securebank.com");
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // GIVEN
            UpdateUserRequest updateRequest = new UpdateUserRequest();
            updateRequest.setFirstName("Jane");

            given(userRepository.findById("test-uuid-123")).willReturn(Optional.of(user));
            given(userRepository.save(any(User.class))).willReturn(user);
            given(userMapper.toResponse(any(User.class))).willReturn(userResponse);

            // WHEN
            UserResponse result = userService.updateUser("test-uuid-123", updateRequest);

            // THEN
            assertThat(result).isNotNull();
            then(userMapper).should().updateEntityFromRequest(updateRequest, user);
            then(userRepository).should().save(user);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent user")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            // GIVEN
            given(userRepository.findById(anyString())).willReturn(Optional.empty());

            // WHEN & THEN
            assertThatThrownBy(() -> userService.updateUser("bad-id", new UpdateUserRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // GIVEN
            given(userRepository.findById("test-uuid-123")).willReturn(Optional.of(user));
            willDoNothing().given(userRepository).delete(user);

            // WHEN
            userService.deleteUser("test-uuid-123");

            // THEN
            then(userRepository).should().delete(user);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            // GIVEN
            given(userRepository.findById(anyString())).willReturn(Optional.empty());

            // WHEN & THEN
            assertThatThrownBy(() -> userService.deleteUser("bad-id"))
                    .isInstanceOf(ResourceNotFoundException.class);

            then(userRepository).should(never()).delete(any(User.class));
        }
    }
}