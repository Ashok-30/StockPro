package com.stockpro.serviceimpl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stockpro.model.Order;
import com.stockpro.repository.OrderRepository;
import com.stockpro.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
 
 @Autowired
 private OrderRepository orderRepository;

 @Override
 public Order createOrder(Order order) {
     // Add any business logic here if necessary
     return orderRepository.save(order);
 }
}
