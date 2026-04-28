package com.securebank.user.service;

import com.securebank.user.dto.request.UpdateUserRequest;
import com.securebank.user.dto.request.UserRegistrationRequest;
import com.securebank.user.dto.response.UserResponse;
import java.util.List;

// DIP: Controller depends on this interface, not the implementation
public interface UserService {

  UserResponse registerUser(UserRegistrationRequest request);

  UserResponse getUserById(String id);

  UserResponse getUserByEmail(String email);

  List<UserResponse> getAllUsers();

  UserResponse updateUser(String id, UpdateUserRequest request);

  void deleteUser(String id);
}
