package com.books.spring.Model;

import jakarta.persistence.*;
import javax.validation.constraints.Email;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserModel {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String name;

  @Column(unique = true)
  private String userName;

  @Email
  @Column(unique = true)
  private String email;

  private String password;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    createdAt = updatedAt = LocalDateTime.now();
    if (id == null) {
      id = UUID.randomUUID();
    }
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public java.time.LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.time.LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public java.time.LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
