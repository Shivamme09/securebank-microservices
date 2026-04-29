package com.securebank.user.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.securebank.user.domain.User;
import com.securebank.user.domain.UserRole;
import com.securebank.user.domain.UserStatus;
import com.securebank.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest // Loads only JPA layer — fast, no web/security context
@DisplayName("UserRepository Tests")
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  private User testUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll(); // Clean state before each test

    testUser =
        User.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@securebank.com")
            .password("encodedPassword")
            .phoneNumber("+447911123456")
            .status(UserStatus.PENDING_VERIFICATION)
            .role(UserRole.CUSTOMER)
            .build();

    userRepository.save(testUser);
  }

  @Test
  @DisplayName("Should find user by email")
  void shouldFindUserByEmail() {
    Optional<User> found = userRepository.findByEmail("john.doe@securebank.com");

    assertThat(found).isPresent();
    assertThat(found.get().getFirstName()).isEqualTo("John");
  }

  @Test
  @DisplayName("Should return empty when email not found")
  void shouldReturnEmptyWhenEmailNotFound() {
    Optional<User> found = userRepository.findByEmail("nobody@securebank.com");

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should return true when email exists")
  void shouldReturnTrueWhenEmailExists() {
    boolean exists = userRepository.existsByEmail("john.doe@securebank.com");

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false when email does not exist")
  void shouldReturnFalseWhenEmailNotExists() {
    boolean exists = userRepository.existsByEmail("nobody@securebank.com");

    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should return true when phone number exists")
  void shouldReturnTrueWhenPhoneExists() {
    boolean exists = userRepository.existsByPhoneNumber("+447911123456");

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should find users by status")
  void shouldFindUsersByStatus() {
    var users = userRepository.findByStatus(UserStatus.PENDING_VERIFICATION);

    assertThat(users).hasSize(1);
    assertThat(users.get(0).getEmail()).isEqualTo("john.doe@securebank.com");
  }
}
