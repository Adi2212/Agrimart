package com.example.agrimart.model;

public class Product {
    private String id; // Firebase key
    private String productName;
    private String quantity;
    private String phoneNumber;

    // Empty constructor for Firebase
    public Product() {}

    public Product(String productName, String quantity, String phoneNumber) {
        this.productName = productName;

        this.quantity = quantity;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
