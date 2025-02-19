package com.books.spring.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collections;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.books.spring.Config.JwtService;
import com.books.spring.Model.UserModel;
import com.books.spring.Service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserModel user) {
    try {
      // Encode password before saving
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      UserModel newUser = userService.createUser(user);

      // Generate token for the new user
      String token = jwtService.generateToken(new UserDetails() {
        @Override
        public String getUsername() {
          return newUser.getUserName();
        }

        @Override
        public boolean isAccountNonExpired() {
          return true;
        }

        @Override
        public boolean isAccountNonLocked() {
          return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
          return true;
        }

        @Override
        public boolean isEnabled() {
          return true;
        }

        @Override
        public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
          return Collections.emptyList();
        }

        @Override
        public String getPassword() {
          return newUser.getPassword();
        }
      });

      Map<String, Object> response = new HashMap<>();
      response.put("token", token);
      response.put("user", newUser);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    try {
      String username = credentials.get("username");
      String password = credentials.get("password");

      // Authenticate the user
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));

      // If authentication is successful, generate token
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String token = jwtService.generateToken(userDetails);

      // Get user details
      UserModel user = userService.getUserByUsername(userDetails.getUsername());
      if (user == null) {
        user = userService.getUserByEmail(userDetails.getUsername());
      }

      Map<String, Object> response = new HashMap<>();
      response.put("token", token);
      response.put("user", user);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(401).body(Map.of("error", "Authentication failed"));
    }
  }
}