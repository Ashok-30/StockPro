package com.stockpro.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class StockUtils {
    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>(responseMessage, httpStatus);
    }
}
