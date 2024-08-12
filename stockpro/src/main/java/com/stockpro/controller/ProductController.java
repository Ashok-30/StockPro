package com.stockpro.controller;

import com.stockpro.model.Product;
import com.stockpro.model.ProductSaleRequest;
import com.stockpro.service.ProductService;
import com.stockpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllProducts(@AuthenticationPrincipal UserDetails userDetails, @PageableDefault(size = 10) Pageable pageable) {
        Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();
        return productService.getAllProducts(storeId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody Product product, @AuthenticationPrincipal UserDetails userDetails) {
        Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();
        return productService.addProduct(product, storeId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/below-minimum")
    public ResponseEntity<List<Product>> getProductsBelowMinimum() {
        return productService.getProductsBelowMinimum();
    }
    
    @PutMapping("/sell-products")
    public ResponseEntity<String> sellProducts(@RequestBody List<ProductSaleRequest> saleRequests) {
        return productService.sellProducts(saleRequests);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
        return productService.searchProductsByName(name);
    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) {
        if (!file.isEmpty()) {
            try {
                Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();
                return productService.uploadAndAddProducts(file.getInputStream(), storeId);
            } catch (Exception e) {
                return new ResponseEntity<>("Failed to process file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Please upload a non-empty Excel file.", HttpStatus.BAD_REQUEST);
        }
    }

}
