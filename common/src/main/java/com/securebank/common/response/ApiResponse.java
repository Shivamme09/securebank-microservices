package com.securebank.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Don't show null fields in JSON
public class ApiResponse<T> {

  private final boolean success;
  private final String message;
  private final T data;
  private final LocalDateTime timestamp;

  // Static factory methods — clean and readable at call site
  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static <T> ApiResponse<T> success(String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static <T> ApiResponse<T> error(String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
