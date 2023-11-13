package com.cs407.kwikTix;

public class Colleges {
    private String college;
    private String latitude;
    private String longitude;

    public Colleges(String college, String latitude, String longitude) {
        this.college = college;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCollege() {
        return college;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
