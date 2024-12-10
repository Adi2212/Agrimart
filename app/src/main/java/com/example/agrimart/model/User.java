package com.example.agrimart.model;

public class User {
    public String name;
    public String email;
    public String phone;
    public String address;
    public String password;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email,String phone, String address, String password) {
        this.name = name;
        this.email = email;
        this.phone=phone;
        this.address=address;
        this.password=password;
    }
}