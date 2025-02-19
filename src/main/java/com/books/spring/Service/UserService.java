package com.books.spring.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.books.spring.Model.UserModel;
import com.books.spring.Repository.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Transactional(readOnly = true)
  public List<UserModel> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<UserModel> getUserById(UUID id) {
    return userRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public UserModel getUserByUsername(String userName) {
    return userRepository.findByUserName(userName);
  }

  @Transactional(readOnly = true)
  public UserModel getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional
  public UserModel createUser(UserModel user) {
    if (userRepository.existsByUserName(user.getUserName())) {
      throw new RuntimeException("Username already exists");
    }
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new RuntimeException("Email already exists");
    }
    return userRepository.save(user);
  }

  @Transactional
  public UserModel updateUser(UUID id, UserModel userDetails) {
    UserModel user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Check if new username is already taken by another user
    if (!user.getUserName().equals(userDetails.getUserName()) &&
        userRepository.existsByUserName(userDetails.getUserName())) {
      throw new RuntimeException("Username already exists");
    }

    // Check if new email is already taken by another user
    if (!user.getEmail().equals(userDetails.getEmail()) &&
        userRepository.existsByEmail(userDetails.getEmail())) {
      throw new RuntimeException("Email already exists");
    }

    user.setName(userDetails.getName());
    user.setUserName(userDetails.getUserName());
    user.setEmail(userDetails.getEmail());
    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
      user.setPassword(userDetails.getPassword());
    }

    return userRepository.save(user);
  }

  @Transactional
  public void deleteUser(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new RuntimeException("User not found");
    }
    userRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public boolean isUserNameTaken(String userName) {
    return userRepository.existsByUserName(userName);
  }

  @Transactional(readOnly = true)
  public boolean isEmailTaken(String email) {
    return userRepository.existsByEmail(email);
  }
}