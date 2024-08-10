package com.stockpro.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stockpro.model.Order;
import com.stockpro.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
 
 @Autowired
 private OrderService orderService;

 @PostMapping
 public ResponseEntity<Order> addOrder(@RequestBody Order order) {
     Order newOrder = orderService.createOrder(order);
     return ResponseEntity.ok(newOrder);
 }
}
