package com.stockpro.serviceimpl;

import com.stockpro.model.Store;
import com.stockpro.model.User;
import com.stockpro.repository.StoreRepository;
import com.stockpro.repository.UserRepository;
import com.stockpro.service.UserService;
import com.stockpro.utils.JwtUtil;
import com.stockpro.utils.StockUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userRepository.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    Store store = new Store();
                    store.setName(requestMap.get("storeName"));
                    store.setAddress(requestMap.get("storeAddress"));
                    store = storeRepository.save(store);

                    userRepository.save(getUserFromMap(requestMap, "ADMIN", store));
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
    public ResponseEntity<String> addUser(Map<String, String> requestMap, String adminEmail) {
        try {
            if (validateAddUserMap(requestMap)) {
                User adminUser = userRepository.findByEmail(adminEmail);
                if (adminUser != null && adminUser.getRole().equals("ADMIN")) {
                    User user = userRepository.findByEmail(requestMap.get("email"));
                    if (Objects.isNull(user)) {
                        userRepository.save(getUserFromMap(requestMap, requestMap.get("role"), adminUser.getStore()));
                        return StockUtils.getResponseEntity("User added successfully", HttpStatus.OK);
                    } else {
                        return StockUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return StockUtils.getResponseEntity("Not Authorised to create user", HttpStatus.UNAUTHORIZED);
                }
            } else {
                return StockUtils.getResponseEntity("Invalid Data", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return StockUtils.getResponseEntity("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private User getUserFromMap(Map<String, String> requestMap, String role, Store store) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));
        user.setRole(role);
        user.setStore(store);
        return user;
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
               requestMap.containsKey("contactNumber") &&
               requestMap.containsKey("email") &&
               requestMap.containsKey("password") &&
               requestMap.containsKey("storeName") &&
               requestMap.containsKey("storeAddress");
    }

    private boolean validateAddUserMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
               requestMap.containsKey("contactNumber") &&
               requestMap.containsKey("email") &&
               requestMap.containsKey("password") &&
               requestMap.containsKey("role");
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            if (requestMap.containsKey("email") && requestMap.containsKey("password")) {
                User user = userRepository.findByEmail(requestMap.get("email"));
                if (Objects.nonNull(user) && passwordEncoder.matches(requestMap.get("password"), user.getPassword())) {
                    String token = jwtUtil.generateToken(user.getEmail());
                    return ResponseEntity.ok(token);
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

    @Override
    public ResponseEntity<Map<String, String>> getDashboard(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            Map<String, String> userDetails = new HashMap<>();
            userDetails.put("name", user.getName());
            userDetails.put("contactNumber", user.getContactNumber());
            userDetails.put("email", user.getEmail());
            userDetails.put("role", user.getRole());
            userDetails.put("storeId", user.getStore().getId().toString());
            return ResponseEntity.ok(userDetails);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> logout(String token) {
        return ResponseEntity.ok("Logged out successfully");
    }
    @Override
    public ResponseEntity<List<User>> getUsersByStoreIdAndRoleNotAdmin(Long storeId) {
        List<User> users = userRepository.findByStoreIdAndRoleNot(storeId, "ADMIN");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public User updateUser(Long userId, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            user.setContactNumber(userDetails.getContactNumber());
            return userRepository.save(user);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean deleteUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
            return true;
        } else {
            return false;
        }
    }
    @Override
    public ResponseEntity<User> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @Override
    public ResponseEntity<String> getAdminEmailByStoreId(Long storeId) {
        List<User> users = userRepository.findByStoreIdAndRole(storeId, "ADMIN");
        if (users != null && !users.isEmpty()) {
            return ResponseEntity.ok(users.get(0).getEmail());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
}
