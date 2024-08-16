package com.stockpro.controller;




import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.stockpro.model.Order;

import com.stockpro.service.OrderService;
import com.stockpro.service.UserService;

@RestController
@RequestMapping("/orders")
public class OrderController {
 
	@Autowired
    private OrderService orderService;


	@Autowired
    private UserService userService; 

	@PostMapping
    public ResponseEntity<Order> addOrder(@RequestBody Order order, @AuthenticationPrincipal UserDetails userDetails) {
        Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId(); // Get storeId from userDetails
        Order newOrder = orderService.createOrder(order, storeId);
        return ResponseEntity.ok(newOrder);
    }
    @GetMapping("/sales-data")
    public ResponseEntity<Map<String, Object>> getDailySalesData(@AuthenticationPrincipal UserDetails userDetails) {
        Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();
        Map<String, Object> salesData = orderService.getDailySalesData(storeId);
        return ResponseEntity.ok(salesData);
    }
    @GetMapping("/top-selling-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(@AuthenticationPrincipal UserDetails userDetails) {
        Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();
        List<Map<String, Object>> topSellingProducts = orderService.getTopSellingProducts(storeId);
        if (topSellingProducts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(topSellingProducts);
    }
    
   }
