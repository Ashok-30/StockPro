package com.stockpro.serviceimpl;

import com.stockpro.model.Product;
import com.stockpro.model.ProductSaleRequest;
import com.stockpro.repository.ProductRepository;
import com.stockpro.service.EmailService;
import com.stockpro.service.ProductService;
import com.stockpro.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
   

    @Override
    public ResponseEntity<Page<Product>> getAllProducts(Long storeId, Pageable pageable) {
        Page<Product> products = productRepository.findByStoreId(storeId, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Product> getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<String> addProduct(Product product, Long storeId) {
        try {
            // Check if the product already exists
            Optional<Product> existingProduct = productRepository.findByNameAndStoreId(product.getName(), storeId);
            if (existingProduct.isPresent()) {
                // If product exists, update all fields and increment the quantity
                Product updatedProduct = existingProduct.get();
                updatedProduct.setDescription(product.getDescription());
                updatedProduct.setPrice(product.getPrice());
                updatedProduct.setCategory(product.getCategory());
                updatedProduct.setMinimumQuantity(product.getMinimumQuantity());
                updatedProduct.setSupplier(product.getSupplier());
                updatedProduct.setAttribute(product.getAttribute());
                updatedProduct.setBrand(product.getBrand());
                // Update quantity specifically
                updatedProduct.setQuantity(updatedProduct.getQuantity() + product.getQuantity());
                productRepository.save(updatedProduct);
                return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
            } else {
                // If product does not exist, save as new product
                product.setStoreId(storeId);
                productRepository.save(product);
                return new ResponseEntity<>("Product added successfully", HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error in product operation: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public ResponseEntity<String> updateProduct(Long id, Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setCategory(productDetails.getCategory());
            product.setQuantity(productDetails.getQuantity());
            product.setMinimumQuantity(productDetails.getMinimumQuantity());
            product.setPrice(productDetails.getPrice());
            product.setSupplier(productDetails.getSupplier());
            product.setAttribute(productDetails.getAttribute());
            product.setBrand(productDetails.getBrand());
            
            productRepository.save(product);
            if (product.getQuantity() <= product.getMinimumQuantity()) {
                ResponseEntity<String> adminEmailResponse = userService.getAdminEmailByStoreId(product.getStoreId());
                if (adminEmailResponse.getStatusCode() == HttpStatus.OK && adminEmailResponse.getBody() != null) {
                    emailService.sendAdminNotification(adminEmailResponse.getBody(), product.getName());
                }
            }
           
           
            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<Product>> getProductsBelowMinimum() {
        List<Product> products = productRepository.findByQuantityLessThanEqual(5); 
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
   
    @Override
    @Transactional
    public ResponseEntity<String> sellProducts(List<ProductSaleRequest> saleRequests) {
        for (ProductSaleRequest request : saleRequests) {
            Optional<Product> optionalProduct = productRepository.findById(request.getProductId());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                int newQuantity = product.getQuantity() - request.getQuantity();

                if (newQuantity >= 0) {
                    product.setQuantity(newQuantity);
                    productRepository.save(product);

                    if (newQuantity <= product.getMinimumQuantity()) {
                        ResponseEntity<String> adminEmailResponse = userService.getAdminEmailByStoreId(product.getStoreId());
                        if (adminEmailResponse.getStatusCode() == HttpStatus.OK && adminEmailResponse.getBody() != null) {
                            emailService.sendAdminNotification(adminEmailResponse.getBody(), product.getName());
                        }
                    }
                } else {
                    return new ResponseEntity<>("Insufficient stock for product ID: " + request.getProductId(), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Product not found with ID: " + request.getProductId(), HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("Products sold successfully", HttpStatus.OK);
    }


    @Override
    public ResponseEntity<List<Product>> searchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    
}
