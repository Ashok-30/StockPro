package com.stockpro.service;

import com.stockpro.model.ProductSales;

import java.time.LocalDate;
import java.util.List;

public interface ProductSalesService {
    
    List<ProductSales> getSalesData(Long productId, Long storeId, LocalDate startDate, LocalDate endDate);
    
    // Add more method declarations here if needed
}
