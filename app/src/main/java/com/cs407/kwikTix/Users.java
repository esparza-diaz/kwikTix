package com.cs407.kwikTix;

public class Users {
    private String username;
    private String password;
    private String email;
    private String college;

    public Users(String username, String password, String email, String college) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.college = college;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getCollege() {
        return college;
    }
}
