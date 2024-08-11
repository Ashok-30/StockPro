package com.stockpro.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; // Import LocalDateTime
import com.stockpro.model.Order;
import com.stockpro.repository.OrderRepository;
import com.stockpro.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
 
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {
        order.setOrderDateTime(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
