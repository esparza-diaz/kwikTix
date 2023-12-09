package com.cs407.kwikTix;

import java.io.Serializable;

public class Tickets implements Serializable {
    private String title;
    private String date;
    private String college;
    private String seller;
    private String buyer;
    private String price;
    private String id;
    private String available;
    private String sellPrice;

    public Tickets(String title, String id, String date, String price, String sellPrice, String college, String seller, String buyer, String available) {
        this.title = title;
        this.date = date;
        this.college = college;
        this.seller = seller;
        this.buyer = buyer;
        this.price = price;
        this.sellPrice = sellPrice;
        this.id = id;
        this.available = available;

    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getCollege() {
        return college;
    }

    public String getSeller() {
        return seller;
    }
    public String getBuyer() { return buyer; }

    public String getPrice() {
        return price;
    }
    public String getId() { return id; }
    public String getAvailable(){ return available;}

    public String getSellPrice() {
        return sellPrice;
    }
}
