package com.stockpro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockpro.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
