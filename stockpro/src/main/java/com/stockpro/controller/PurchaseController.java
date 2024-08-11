package com.stockpro.controller;

import com.stockpro.model.Purchase;
import com.stockpro.service.PurchaseService;
import com.stockpro.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Purchase>> getAllPurchases(@AuthenticationPrincipal UserDetails userDetails) {
        // This assumes userService is set up to retrieve user and store details.
        Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();
        List<Purchase> purchases = purchaseService.getPurchasesByStoreId(storeId);
        return ResponseEntity.ok(purchases);
    }
    @PostMapping
    public ResponseEntity<Purchase> addPurchase(@RequestBody Purchase purchase, @AuthenticationPrincipal UserDetails userDetails) {
        // Optional: Set the store ID automatically from the logged-in user's details if not set
        if (purchase.getStoreId() == null) {
            Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();
            purchase.setStoreId(storeId);
        }
        Purchase savedPurchase = purchaseService.addPurchase(purchase);
        return ResponseEntity.ok(savedPurchase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Purchase> updatePurchase(@PathVariable Long id, @RequestBody Purchase purchaseDetails, @AuthenticationPrincipal UserDetails userDetails) {
        Long storeId = userService.findByEmail(userDetails.getUsername()).getStore().getId();
        Purchase updatedPurchase = purchaseService.updatePurchase(id, purchaseDetails, storeId);
        if (updatedPurchase != null) {
            return ResponseEntity.ok(updatedPurchase);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.ok().build();
    }
}
