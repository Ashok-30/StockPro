package com.stockpro.serviceimpl;

import com.stockpro.model.Purchase;
import com.stockpro.repository.PurchaseRepository;
import com.stockpro.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    public Purchase addPurchase(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Override
    public List<Purchase> getPurchasesByStoreId(Long storeId) {
        return purchaseRepository.findByStoreId(storeId);
    }


    @Override
    public Purchase updatePurchase(Long id, Purchase purchaseDetails, Long storeId) {
        Optional<Purchase> purchaseOptional = purchaseRepository.findById(id);
        if (purchaseOptional.isPresent()) {
            Purchase purchase = purchaseOptional.get();
            if (purchase.getStoreId().equals(storeId)) { // Check if the storeId matches
                purchase.setName(purchaseDetails.getName());
                purchase.setPrice(purchaseDetails.getPrice());
                purchase.setSupplier(purchaseDetails.getSupplier());
                purchase.setQuantity(purchaseDetails.getQuantity());
                return purchaseRepository.save(purchase);
            }
            throw new IllegalStateException("Attempt to update purchase from a different store");
        }
        return null; // Or consider throwing a custom exception if the purchase does not exist
    }

    @Override
    public void deletePurchase(Long id) {
        purchaseRepository.deleteById(id);
    }
}
