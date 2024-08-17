package com.stockpro.serviceimpl;

import com.stockpro.model.ProductSales;
import com.stockpro.repository.ProductSalesRepository;
import com.stockpro.service.ProductSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductSalesServiceImpl implements ProductSalesService {

    @Autowired
    private ProductSalesRepository productSalesRepository;

    @Override
    public List<ProductSales> getSalesData(Long productId, Long storeId, LocalDate startDate, LocalDate endDate) {
        List<ProductSales> salesData;
        if (productId != null) {
            salesData = productSalesRepository.findSalesByProductIdAndStoreIdAndDateRange(productId, storeId, startDate, endDate);
        } else {
            salesData = productSalesRepository.findSalesByStoreIdAndDateRange(storeId, startDate, endDate);
        }
        
        // Log the retrieved sales data
        System.out.println("Sales Data Retrieved: " + salesData);
        
        return salesData;
    }

    
    // Add more method implementations here if needed
}
