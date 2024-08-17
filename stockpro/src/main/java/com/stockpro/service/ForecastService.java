package com.stockpro.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ForecastService {

    private final String FLASK_API_URL = "http://localhost:5001/forecast";

    public Map<String, Integer> getSalesForecast(List<Map<String, Object>> salesData) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sales", salesData);

        // Log the request body to verify the data being sent
        System.out.println("Request Body: " + requestBody);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                FLASK_API_URL,
                HttpMethod.POST,
                request,
                Map.class
        );

        return response.getBody();
    }
}
