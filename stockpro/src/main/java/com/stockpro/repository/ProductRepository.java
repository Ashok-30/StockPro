package com.stockpro.repository;

import com.stockpro.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByQuantityLessThanEqual(int quantity);
    Page<Product> findByStoreId(Long storeId, Pageable pageable);
    Optional<Product> findByNameAndStoreId(String name, Long storeId);
}
