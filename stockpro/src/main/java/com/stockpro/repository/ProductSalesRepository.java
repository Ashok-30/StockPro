package com.stockpro.repository;

import com.stockpro.model.ProductSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductSalesRepository extends JpaRepository<ProductSales, Long> {

    @Query("SELECT ps FROM ProductSales ps WHERE ps.productId = :productId AND ps.storeId = :storeId AND ps.orderDate BETWEEN :startDate AND :endDate ORDER BY ps.orderDate ASC")
    List<ProductSales> findSalesByProductIdAndStoreIdAndDateRange(Long productId, Long storeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT ps FROM ProductSales ps WHERE ps.storeId = :storeId AND ps.orderDate BETWEEN :startDate AND :endDate ORDER BY ps.orderDate ASC")
    List<ProductSales> findSalesByStoreIdAndDateRange(Long storeId, LocalDate startDate, LocalDate endDate);
}
