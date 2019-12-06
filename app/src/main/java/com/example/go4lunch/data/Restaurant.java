package com.example.go4lunch.data;

public class Restaurant {
    private String name;
    private String address;
    private String openingHours;
    private String distance;
    private int workmates;
    private float ratingBar;
    private String photo;


    public Restaurant(String name, String address, String openingHours, String distance, int workmates, float ratingBar, String photo) {
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.distance = distance;
        this.workmates = workmates;
        this.ratingBar = ratingBar;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getWorkmates() {
        return workmates;
    }

    public void setWorkmates(int workmates) {
        this.workmates = workmates;
    }

    public float getRatingBar() {
        return ratingBar;
    }

    public void setRatingBar(float ratingBar) {
        this.ratingBar = ratingBar;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
