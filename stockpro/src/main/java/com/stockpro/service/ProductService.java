package com.stockpro.service;

import com.stockpro.model.Product;
import com.stockpro.model.ProductSaleRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
	ResponseEntity<Page<Product>> getAllProducts(Long storeId, Pageable pageable);
    ResponseEntity<Product> getProductById(Long id);
    ResponseEntity<String> addProduct(Product product, Long storeId); 
    ResponseEntity<String> updateProduct(Long id, Product productDetails);
    ResponseEntity<String> deleteProduct(Long id);
    ResponseEntity<List<Product>> getProductsBelowMinimum();
    ResponseEntity<String> sellProducts(List<ProductSaleRequest> saleRequests);
    ResponseEntity<List<Product>> searchProductsByName(String name);
}
