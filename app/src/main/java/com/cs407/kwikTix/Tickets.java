package com.cs407.kwikTix;

public class Tickets {
    private String title;
    private String date;
    private String college;
    private String username;
    private String price;

    public Tickets(String title, String date, String price, String college, String username) {
        this.title = title;
        this.date = date;
        this.college = college;
        this.username = username;
        this.price = price;
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
}
