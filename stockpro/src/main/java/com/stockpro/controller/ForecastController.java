package com.stockpro.controller;

import com.stockpro.model.ProductSales;
import com.stockpro.service.ForecastService;
import com.stockpro.service.ProductSalesService;
import com.stockpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forecast")
public class ForecastController {

    @Autowired
    private ProductSalesService productSalesService;

    @Autowired
    private ForecastService forecastService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Integer>> getSalesForecast(
        @RequestParam Long productId,
        @RequestParam String startDate,
        @RequestParam String endDate,
        @AuthenticationPrincipal UserDetails userDetails) {

        Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<ProductSales> salesData = productSalesService.getSalesData(productId, storeId, start, end);

        List<Map<String, Object>> salesDataForFlask = salesData.stream().map(sale -> {
            Map<String, Object> map = new HashMap<>();
            map.put("order_date", sale.getOrderDate().toString());
            map.put("product_id", sale.getProductId());
            map.put("quantity", sale.getQuantity());
            return map;
        }).collect(Collectors.toList());

        System.out.println("Sales Data for Flask: " + salesDataForFlask);

        // Check what forecastService returns
        Map<String, Integer> forecast = forecastService.getSalesForecast(salesDataForFlask);
        System.out.println("Forecast Data: " + forecast);

        return ResponseEntity.ok(forecast);
    }

}
