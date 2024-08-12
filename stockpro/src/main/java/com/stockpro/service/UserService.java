package com.stockpro.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.stockpro.model.User;

import java.util.List;
import java.util.Map;



public interface UserService {
    ResponseEntity<String> signUp(Map<String, String> requestMap);
    ResponseEntity<String> login(Map<String, String> requestMap);
    ResponseEntity<String> verifyCredentials(String email, String password);
    ResponseEntity<Map<String, String>> getDashboard(String email);
    ResponseEntity<String> logout(String token);
    
    ResponseEntity<String> addUser(Map<String, String> requestMap, String adminEmail);
    ResponseEntity<User> getUserByEmail(String email);
    ResponseEntity<List<User>> getUsersByStoreIdAndRoleNotAdmin(Long storeId);
    User findByEmail(String email);
    User updateUser(Long userId, User userDetails);
    boolean deleteUser(Long userId);
    ResponseEntity<String>getAdminEmailByStoreId(Long storeId);
    User updateUserWithPhoto(Long userId, User userDetails, MultipartFile photo);
}