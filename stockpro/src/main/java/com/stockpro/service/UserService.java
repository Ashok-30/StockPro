package com.stockpro.service;

import org.springframework.http.ResponseEntity;
import com.stockpro.model.User;

import java.util.Map;
import java.util.List;

public interface UserService {
    ResponseEntity<String> signUp(Map<String, String> requestMap);
    ResponseEntity<String> login(Map<String, String> requestMap);
    ResponseEntity<String> verifyCredentials(String email, String password);
    ResponseEntity<Map<String, String>> getDashboard(String email);
    ResponseEntity<String> logout(String token);

    // New methods for user management
    ResponseEntity<String> addUser(Map<String, String> requestMap, String adminEmail);
    ResponseEntity<List<User>> getAllUsers();
}
