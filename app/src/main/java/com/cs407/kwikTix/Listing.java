package com.cs407.kwikTix;

public class Listing {
    private String title;
    private Double price;

    public Listing(String title, Double price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return this.title;
    }

    public Double getPrice() {
        return this.price;
    }
}
