package com.stockpro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long orderId;

 @Column(name = "customer_name", nullable = false)
 private String customerName;

 @Column(name = "customer_number", nullable = false)
 private String customerNumber;

 @Column(name = "product_ids") 
 private String productIds;

 public Long getOrderId() {
	return orderId;
}

public void setOrderId(Long orderId) {
	this.orderId = orderId;
}

public String getCustomerName() {
	return customerName;
}

public void setCustomerName(String customerName) {
	this.customerName = customerName;
}

public String getCustomerNumber() {
	return customerNumber;
}

public void setCustomerNumber(String customerNumber) {
	this.customerNumber = customerNumber;
}

public String getProductIds() {
	return productIds;
}

public void setProductIds(String productIds) {
	this.productIds = productIds;
}

public Double getTotalAmount() {
	return totalAmount;
}

public void setTotalAmount(Double totalAmount) {
	this.totalAmount = totalAmount;
}

@Column(name = "total_amount", nullable = false)
 private Double totalAmount;


}
