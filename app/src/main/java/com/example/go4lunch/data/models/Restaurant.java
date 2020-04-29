package com.example.go4lunch.data.models;

import com.google.android.libraries.places.api.model.PhotoMetadata;

public class Restaurant {
    private String id;
    private String name;
    private String address;
    private String openingHours;
    private String distance;
    private int workmates;
    private Double ratingBar;
    private PhotoMetadata photo;
    private String websiteUrl;
    private String phoneNumber;
    private Boolean isOpen;

    public Restaurant(String id, String name, String address, String openingHours, String distance, int workmates, Double ratingBar, PhotoMetadata photo, String websiteUrl, String phoneNumber, Boolean isOpen) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.distance = distance;
        this.workmates = workmates;
        this.ratingBar = ratingBar;
        this.photo = photo;
        this.websiteUrl = websiteUrl;
        this.phoneNumber = phoneNumber;
        this.isOpen = isOpen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Double getRatingBar() {
        return ratingBar;
    }

    public void setRatingBar(Double ratingBar) {
        this.ratingBar = ratingBar;
    }

    public PhotoMetadata getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoMetadata photo) {
        this.photo = photo;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean isOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }
}



