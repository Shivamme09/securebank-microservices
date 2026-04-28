package com.securebank.user.service.impl;

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
import com.securebank.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor  // Lombok: constructor injection (best practice over @Autowired)
@Slf4j                    // Lombok: gives you log.info(), log.error() etc.
@Transactional(readOnly = true)  // Default all methods to read-only for performance
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional  // Override to writable for state-changing methods
    public UserResponse registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check duplicates before creating
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", request.getEmail());
        }

        if (request.getPhoneNumber() != null &&
                userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ResourceAlreadyExistsException("User", "phoneNumber", request.getPhoneNumber());
        }

        // Map DTO → Entity
        User user = userMapper.toEntity(request);

        // Set fields not handled by mapper (SRP — mapper only maps, service applies logic)
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setRole(UserRole.CUSTOMER);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userMapper.updateEntityFromRequest(request, user);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        log.info("Deleting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }
}