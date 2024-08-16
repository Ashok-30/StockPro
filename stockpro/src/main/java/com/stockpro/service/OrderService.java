package com.stockpro.service;

import java.util.List;
import java.util.Map;

import com.stockpro.model.Order;

public interface OrderService {
    Order createOrder(Order order, Long storeId);
    Map<String, Object> getDailySalesData(Long storeId);
    List<Map<String, Object>> getTopSellingProducts(Long storeId); // This method returns the top 5 selling products
}
