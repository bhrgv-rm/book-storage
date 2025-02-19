package com.books.spring.Repository;

import com.books.spring.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {
  boolean existsByUserName(String userName);

  boolean existsByEmail(String email);

  UserModel findByUserName(String userName);

  UserModel findByEmail(String email);

}