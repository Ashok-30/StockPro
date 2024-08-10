package com.stockpro.repository;

import com.stockpro.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByStoreIdAndRoleNot(Long storeId, String role);
   
}
