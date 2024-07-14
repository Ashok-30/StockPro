package com.stockpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stockpro.service.EmailService;
import com.stockpro.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

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
}
