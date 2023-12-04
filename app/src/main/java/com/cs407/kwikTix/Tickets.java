package com.cs407.kwikTix;

import java.io.Serializable;

public class Tickets implements Serializable {
    private String title;
    private String date;
    private String college;
    private String username;
    private String price;
    private String id;
    private String available;

    public Tickets(String title, String id, String date, String price, String college, String username, String available) {
        this.title = title;
        this.date = date;
        this.college = college;
        this.username = username;
        this.price = price;
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

    public String getUsername() {
        return username;
    }

    public String getPrice() {
        return price;
    }
    public String getId() { return id; }
    public String getAvailable(){ return available;}
}
