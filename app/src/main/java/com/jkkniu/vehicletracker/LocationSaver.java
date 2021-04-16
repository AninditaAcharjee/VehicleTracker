package com.jkkniu.vehicletracker;

public class LocationSaver {
    private double longitude;
    private double latitude;

    public LocationSaver() {

    }

    public LocationSaver(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}