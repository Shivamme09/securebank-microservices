package com.securebank.user.dto.response;

import com.securebank.user.domain.UserRole;
import com.securebank.user.domain.UserStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserStatus status;
    private UserRole role;
    private LocalDateTime createdAt;
    // NOTE: password is intentionally excluded — never expose it in responses
}