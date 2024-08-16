package com.stockpro.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime; // Import LocalDateTime
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.stockpro.model.Order;
import com.stockpro.model.ProductSales;
import com.stockpro.repository.OrderRepository;
import com.stockpro.repository.ProductSalesRepository;
import com.stockpro.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
 
   

    @Autowired
    private OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private ProductSalesRepository productSalesRepository;


    @Override
    public Order createOrder(Order order, Long storeId) {
        order.setOrderDateTime(LocalDateTime.now());
        order.setStoreId(storeId); // Set storeId received from the controller

        Order savedOrder = orderRepository.save(order);

        // Normalize product_ids and save to ProductSales table
        String[] productIds = order.getProductIds().split(",");
        for (String productId : productIds) {
            Long prodId = Long.parseLong(productId);

            ProductSales productSales = new ProductSales();
            productSales.setProductId(prodId);
            productSales.setStoreId(storeId);
            productSales.setOrderDate(order.getOrderDateTime().toLocalDate());
            productSales.setQuantity(1); // Assuming each product ID corresponds to one quantity

            productSalesRepository.save(productSales);
        }

        return savedOrder;
    }
    @Override
    public Map<String, Object> getDailySalesData(Long storeId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Double todaySales = orderRepository.findTotalSalesByDateAndStoreId(storeId, today);
        Double yesterdaySales = orderRepository.findTotalSalesByDateAndStoreId(storeId, yesterday);

        logger.info("Today's Sales: {}", todaySales);
        logger.info("Yesterday's Sales: {}", yesterdaySales);
        
        Map<String, Object> salesData = new HashMap<>();
        salesData.put("totalSalesToday", todaySales != null ? todaySales : 0);
        salesData.put("percentageDifference", 0);
        
        

        if (yesterdaySales != null && yesterdaySales != 0 && todaySales != null) {
            double percentageDifference = ((todaySales - yesterdaySales) / yesterdaySales) * 100;
            salesData.put("percentageDifference", percentageDifference);
            String trend = percentageDifference >= 0 ? "Increased" : "Decreased";
            salesData.put("trend", trend);
            
        }

        return salesData;
    }
    @Override
    public List<Map<String, Object>> getTopSellingProducts(Long storeId) {
        List<Object[]> results = orderRepository.findTopSellingProductsByStoreId(storeId);
        if (results.isEmpty()) {
            logger.info("No products found for storeId: {}", storeId);
        } else {
            logger.info("Products found: {}", results.size());
        }
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("productName", result[0]);  // Product name
            map.put("productPrice", result[1]); // Product price (individual, no calculations)
            map.put("totalSales", result[2]);   // Total sales for this product
            map.put("percentageOfTotalSales", result[3]); // Percentage of total sales
            return map;
        }).collect(Collectors.toList());
    }

}
