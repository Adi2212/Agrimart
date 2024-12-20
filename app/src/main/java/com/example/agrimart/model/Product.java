package com.example.agrimart.model;

public class Product {
    private String productName;
    private String quantity;
    private String phoneNumber;
    private String key; // Unique key for the product in the database
    private String category; // Category to classify the product

    public Product() {
    }

    public Product(String productName, String quantity,String phoneNumber) {
        this.productName = productName;
        this.quantity = quantity;
        this.phoneNumber=phoneNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){

        this.phoneNumber=phoneNumber;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}