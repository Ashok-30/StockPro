package com.stockpro.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stockpro.model.User;
import com.stockpro.repository.UserRepository;
import com.stockpro.service.UserService;
import com.stockpro.utils.StockUtils;

import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userRepository.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userRepository.save(getUserFromMap(requestMap));
                    return StockUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
                } else {
                    return StockUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return StockUtils.getResponseEntity("Invalid Data", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return StockUtils.getResponseEntity("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            if (requestMap.containsKey("email") && requestMap.containsKey("password")) {
                User user = userRepository.findByEmail(requestMap.get("email"));
                if (Objects.nonNull(user) && passwordEncoder.matches(requestMap.get("password"), user.getPassword())) {
                    return StockUtils.getResponseEntity("Login successful", HttpStatus.OK);
                }
                return StockUtils.getResponseEntity("Invalid email or password", HttpStatus.UNAUTHORIZED);
            }
            return StockUtils.getResponseEntity("Invalid data", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return StockUtils.getResponseEntity("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> verifyCredentials(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return StockUtils.getResponseEntity("Credentials verified", HttpStatus.OK);
        } else {
            return StockUtils.getResponseEntity("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
               requestMap.containsKey("contactNumber") &&
               requestMap.containsKey("email") &&
               requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));
        user.setRole("USER");
        return user;
    }
}