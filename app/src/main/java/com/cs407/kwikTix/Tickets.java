package com.cs407.kwikTix;

public class Tickets {
    private String title;
    private String date;
    private String college;
    private String username;

    public Tickets(String title, String date, String college, String username) {
        this.title = title;
        this.date = date;
        this.college = college;
        this.username = username;
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
}
