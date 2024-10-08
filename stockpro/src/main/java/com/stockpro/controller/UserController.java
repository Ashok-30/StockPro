package com.stockpro.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockpro.model.User;
import com.stockpro.service.EmailService;
import com.stockpro.service.FileStorageService;
import com.stockpro.service.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/auth")

public class UserController {
	 private static final Logger logger = LoggerFactory.getLogger(UserController.class);



    @Autowired
    private EmailService emailService;


    @Autowired
    private UserService userService;
    
    @Autowired
    private FileStorageService fileStorageService;
   

    private Map<String, String> otpStorage = new HashMap<>();

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody Map<String, String> requestMap) {
        return userService.signUp(requestMap);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> requestMap) {
        return userService.login(requestMap);
    }

    @PostMapping("/verify-credentials")
    public ResponseEntity<String> verifyCredentials(@RequestBody Map<String, String> requestMap) {
        String email = requestMap.get("email");
        String password = requestMap.get("password");
        return userService.verifyCredentials(email, password);
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOTP(@RequestBody Map<String, String> requestMap) {
        String email = requestMap.get("email");
        String otp = emailService.generateOTP();
        emailService.sendOTPEmail(email, otp);
        otpStorage.put(email, otp);
        return new ResponseEntity<>("OTP sent to your email", HttpStatus.OK);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestBody Map<String, String> requestMap) {
        String email = requestMap.get("email");
        String otp = requestMap.get("otp");
        if (otp.equals(otpStorage.get(email))) {
            return new ResponseEntity<>("OTP verified successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return userService.getDashboard(username);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        return userService.logout(token);
    }

    @PostMapping("/add-user")
    public ResponseEntity<String> addUser(@RequestBody Map<String, String> requestMap, @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails.getUsername();
        return userService.addUser(requestMap, adminEmail);
    }
    @GetMapping("/users/by-store")
    public ResponseEntity<List<User>> getUsersByStoreId(@AuthenticationPrincipal UserDetails userDetails) {
        User adminUser = userService.findByEmail(userDetails.getUsername());
        if (adminUser != null) {
            Long storeId = adminUser.getStore().getId();
            return userService.getUsersByStoreIdAndRoleNotAdmin(storeId);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long userId,
            @RequestParam("user") String userStr,
            @RequestParam("file") MultipartFile file) {
        User userDetails = null;
		try {
			userDetails = new ObjectMapper().readValue(userStr, User.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        logger.info("Updating user with ID: {}", userId);
        User updatedUser = userService.updateUserWithPhoto(userId, userDetails, file);
        if (updatedUser != null) {
            logger.info("User updated successfully: {}", updatedUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            logger.warn("User not found with ID: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource resource = fileStorageService.loadFileAsResource(filename);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @PutMapping("/admin/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        logger.info("Updating user with ID: {}", userId);
        User updatedUser = userService.updateUser(userId, userDetails);
        if (updatedUser != null) {
            logger.info("User updated successfully: {}", updatedUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            logger.warn("User not found with ID: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        boolean isDeleted = userService.deleteUser(userId);
        if (isDeleted) {
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/users/profile")
    public ResponseEntity<User> getAuthenticatedUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return userService.getUserByEmail(userDetails.getUsername());
    }
    @GetMapping("/admin-email")
    public ResponseEntity<String> getAdminEmail(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        if (user != null) {
            return userService.getAdminEmailByStoreId(user.getStore().getId());
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


//In UserController.java
@GetMapping("/users/count")
public ResponseEntity<Integer> getUserCount(@AuthenticationPrincipal UserDetails userDetails) {
 User user = userService.findByEmail(userDetails.getUsername());
 if (user != null) {
     Long storeId = user.getStore().getId();
     return userService.countUsersByStoreId(storeId);
 } else {
     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
 }
}

@GetMapping("/users/growth")
public ResponseEntity<List<Map<String, Object>>> getUserGrowth(@AuthenticationPrincipal UserDetails userDetails) {
    User user = userService.findByEmail(userDetails.getUsername());
    if (user != null ) {
        Long storeId = user.getStore().getId();
        return userService.analyzeUserGrowthByStoreId(storeId);
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}


}