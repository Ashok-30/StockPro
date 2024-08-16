package com.stockpro.repository;



import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockpro.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query
    ("SELECT SUM(o.totalAmount) FROM Order o WHERE o.storeId = :storeId AND FUNCTION('DATE', o.orderDateTime) = :date")
    Double findTotalSalesByDateAndStoreId(@Param("storeId") Long storeId, @Param("date") LocalDate date);
    
    
    @Query(value = "SELECT p.name AS productName, p.price AS productPrice, SUM(o.total_amount) AS totalSales, " +
            "(SUM(o.total_amount) / (SELECT SUM(total_amount) FROM orders WHERE store_id = ?1) * 100) AS percentageOfTotalSales " +
            "FROM orders o " +
            "JOIN product p ON FIND_IN_SET(p.id, o.product_ids) > 0 " +
            "WHERE o.store_id = ?1 " +
            "GROUP BY p.id, p.name, p.price " +
            "ORDER BY totalSales DESC " , nativeQuery = true)
List<Object[]> findTopSellingProductsByStoreId(Long storeId);
   
}
