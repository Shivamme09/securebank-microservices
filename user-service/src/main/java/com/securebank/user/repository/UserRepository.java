package com.securebank.user.repository;

import com.securebank.user.domain.User;
import com.securebank.user.domain.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByPhoneNumber(String phoneNumber);

  List<User> findByStatus(UserStatus status);
}
