package com.stockpro.service;

import com.stockpro.model.Purchase;
import java.util.List;

public interface PurchaseService {
    Purchase addPurchase(Purchase purchase);
    List<Purchase> getAllPurchases();
    List<Purchase> getPurchasesByStoreId(Long storeId);
    Purchase updatePurchase(Long id, Purchase purchaseDetails, Long storeId);
    void deletePurchase(Long id);
}
