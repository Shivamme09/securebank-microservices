package com.securebank.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

  @Size(min = 2, max = 100)
  private String firstName;

  @Size(min = 2, max = 100)
  private String lastName;

  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
  private String phoneNumber;
}
