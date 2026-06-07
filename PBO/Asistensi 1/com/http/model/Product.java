package com.http.model;

public class Product {
    private String name;
    private String price;
    private String stock;
    
    public Product(String name, String price, String stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getStock() {
        return stock;
    }
}
