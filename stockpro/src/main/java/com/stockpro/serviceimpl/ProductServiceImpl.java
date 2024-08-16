package com.stockpro.serviceimpl;

import com.stockpro.model.Product;
import com.stockpro.model.ProductSaleRequest;
import com.stockpro.model.Purchase;
import com.stockpro.repository.ProductRepository;
import com.stockpro.service.EmailService;
import com.stockpro.service.ProductService;
import com.stockpro.service.PurchaseService;
import com.stockpro.service.UserService;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
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
    @Autowired
    private PurchaseService purchaseService;
   

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
            	Purchase purchase = new Purchase();
                purchase.setName(product.getName());
                purchase.setPrice(product.getPrice());
                purchase.setStoreId(product.getStoreId());
                purchase.setSupplier(product.getSupplier());
                purchase.setQuantity(product.getQuantity());
                purchaseService.addPurchase(purchase);
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
    public ResponseEntity<List<Product>> getProductsBelowMinimum(Long storeId) {
        List<Product> products = productRepository.findProductsBelowMinimum(storeId);
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
                    	Purchase purchase = new Purchase();
                        purchase.setName(product.getName());
                        purchase.setPrice(product.getPrice());
                        purchase.setStoreId(product.getStoreId());
                        purchase.setSupplier(product.getSupplier());
                        purchase.setQuantity(product.getQuantity());
                        purchaseService.addPurchase(purchase);
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
    @Override
    public ResponseEntity<String> uploadAndAddProducts(InputStream file, Long storeId) {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (currentRow.getRowNum() != 0) { // Skip header row
                    try {
                        Product product = new Product();
                        product.setName(currentRow.getCell(0).getStringCellValue());
                        product.setDescription(currentRow.getCell(1).getStringCellValue());
                        product.setCategory(currentRow.getCell(2).getStringCellValue());
                        product.setQuantity((int) currentRow.getCell(3).getNumericCellValue());
                        product.setMinimumQuantity((int) currentRow.getCell(4).getNumericCellValue());
                        product.setPrice(BigDecimal.valueOf(currentRow.getCell(5).getNumericCellValue()));
                        product.setSupplier(currentRow.getCell(6).getStringCellValue());
                        product.setAttribute(currentRow.getCell(7).getStringCellValue());
                        product.setBrand(currentRow.getCell(8).getStringCellValue());
                        product.setStoreId(storeId);

                        ResponseEntity<String> response = addProduct(product, storeId);
                        if (!response.getStatusCode().is2xxSuccessful()) {
                            return new ResponseEntity<>("Failed to add/update some products", HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } catch (Exception e) {
                        return new ResponseEntity<>("Error processing row " + currentRow.getRowNum() + ": " + e.getMessage(), HttpStatus.BAD_REQUEST);
                    }
                }
            }
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to read Excel file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("All products added/updated successfully", HttpStatus.OK);
    }
    
    
}
