package com.securebank.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data // Lombok: generates getters, setters, equals, hashCode, toString
public class UserRegistrationRequest {

  @NotBlank(message = "First name is required")
  @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Please provide a valid email address")
  private String email;

  @NotBlank(message = "Password is required")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message = "Password must be 8+ chars with uppercase, lowercase, number and special character")
  private String password;

  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
  private String phoneNumber;
}
