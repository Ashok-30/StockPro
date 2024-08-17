package com.stockpro.controller;

import com.stockpro.model.ProductSales;
import com.stockpro.service.ProductSalesService;
import com.stockpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class ProductSalesController {

    @Autowired
    private ProductSalesService productSalesService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<ProductSales>> getSalesData(
            @RequestParam(required = false) Long productId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Get storeId from the logged-in user's details
        Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<ProductSales> salesData = productSalesService.getSalesData(productId, storeId, start, end);
        return ResponseEntity.ok(salesData);
    }
}
