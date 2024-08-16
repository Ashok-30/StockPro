package com.stockpro.repository;

import com.stockpro.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByStoreId(Long storeId, Pageable pageable);
    Optional<Product> findByNameAndStoreId(String name, Long storeId);
    List<Product> findByNameContainingIgnoreCase(String name);
    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND p.quantity < p.minimumQuantity")
    List<Product> findProductsBelowMinimum(@Param("storeId") Long storeId);
}
