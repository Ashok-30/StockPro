package com.stockpro.repository;

import com.stockpro.model.User;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByStoreIdAndRoleNot(Long storeId, String role);
    List<User> findByStoreIdAndRole(Long storeId, String role);

 // In UserRepository.java
    int countByStoreId(Long storeId);
    @Query("SELECT new map(MONTH(u.createdAt) as month, YEAR(u.createdAt) as year, COUNT(u) as count) " +
            "FROM User u " +
            "WHERE u.store.id = :storeId " +
            "GROUP BY YEAR(u.createdAt), MONTH(u.createdAt) " +
            "ORDER BY YEAR(u.createdAt) DESC, MONTH(u.createdAt) DESC")
     List<Map<String, Object>> countUsersByMonthAndYear(Long storeId);



}
