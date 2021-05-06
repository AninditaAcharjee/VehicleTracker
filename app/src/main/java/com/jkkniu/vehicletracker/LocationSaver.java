package com.jkkniu.vehicletracker;


public class LocationSaver {
    private double longitude;
    private double latitude;
    private String timestamp;


    public LocationSaver() {

    }

    public LocationSaver(double longitude, double latitude, String timeStamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp= timeStamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocationSaver setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public LocationSaver setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public LocationSaver setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}