package com.securebank.user.mapper;

import com.securebank.user.domain.User;
import com.securebank.user.dto.request.UpdateUserRequest;
import com.securebank.user.dto.request.UserRegistrationRequest;
import com.securebank.user.dto.response.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")  // Makes it a Spring Bean — inject with @Autowired
public interface UserMapper {

    // Map registration request → User entity
    // Password & status handled separately in service layer (not mapped here)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRegistrationRequest request);

    // Map User entity → UserResponse DTO
    UserResponse toResponse(User user);

    // Update existing entity from request — only non-null fields (PATCH behaviour)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);
}