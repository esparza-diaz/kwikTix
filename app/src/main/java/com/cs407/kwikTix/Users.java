package com.cs407.kwikTix;

public class Users {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String prefContactMethod;
    private String college;

    public Users(String username, String password, String email, String phone, String prefContactMethod, String college) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.prefContactMethod = prefContactMethod;
        this.college = college;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() { return this.phone; }

    public String getPrefContactMethod() { return this.prefContactMethod; }

    public String getCollege() {
        return this.college;
    }
}
