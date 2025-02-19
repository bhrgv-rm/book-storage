package com.books.spring.Controller;

import com.books.spring.Model.UserModel;
import com.books.spring.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  private UserService userService;

  @GetMapping("/all")
  public ResponseEntity<List<UserModel>> getAllUsers() {
    if (userService.getAllUsers() != null) {
      return ResponseEntity.status(200).body(userService.getAllUsers());
    }
    return ResponseEntity.status(404).build();
  }

  @GetMapping("/id/{id}")
  public ResponseEntity<UserModel> getUserById(@PathVariable UUID id) {
    Optional<UserModel> user = userService.getUserById(id);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
  }

  @GetMapping("/username/{username}")
  public ResponseEntity<UserModel> getUserByUserName(@PathVariable String username) {
    if (userService.getUserByUsername(username) != null) {
      return ResponseEntity.status(200).body(userService.getUserByUsername(username));
    }
    return ResponseEntity.status(404).build();
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserModel> getUserByEmail(@PathVariable String email) {
    if (userService.getUserByEmail(email) != null) {
      return ResponseEntity.status(200).body(userService.getUserByEmail(email));
    }
    return ResponseEntity.status(404).build();
  }

  @PostMapping("/add")
  public ResponseEntity<?> postUser(@RequestBody UserModel user) {
    try {
      UserModel createdUser = userService.createUser(user);
      return ResponseEntity.status(201).body(createdUser);
    } catch (RuntimeException e) {
      return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("Unexpected error", e.getMessage()));
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    if (userService.getUserById(id) != null) {
      userService.deleteUser(id);
      return ResponseEntity.status(204).build();
    }
    return ResponseEntity.status(404).build();
  }
}