package com.securebank.user.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = "email"),
      @UniqueConstraint(columnNames = "phone_number")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Builder pattern (Design Pattern #1 ✅)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID) // UUID better than Long for microservices
  @Column(updatable = false, nullable = false)
  private String id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "phone_number", unique = true)
  private String phoneNumber;

  @Enumerated(EnumType.STRING) // Store as "ACTIVE" not 0/1 — readable in DB
  @Column(nullable = false)
  private UserStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @CreationTimestamp // Auto-set on insert
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp // Auto-set on update
  private LocalDateTime updatedAt;
}
