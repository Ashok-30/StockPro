package com.stockpro.service;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface UserService {
    ResponseEntity<String> signUp(Map<String, String> requestMap);
    ResponseEntity<String> login(Map<String, String> requestMap);
    ResponseEntity<String> verifyCredentials(String email, String password);
    ResponseEntity<Map<String, String>> getDashboard(String email);
    ResponseEntity<String> logout(String token);
}
