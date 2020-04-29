package com.example.go4lunch.data.models;

import java.util.List;

public class User {
    private String username;
    private String email;
    private String photo;
    private String restaurantId;
    private String restaurantName;

    public User() {
    }

    public User(String name, String email, String photo) {
        this.username = name;
        this.email = email;
        this.photo = photo;
        this.restaurantId = null;
        this.restaurantName = null;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}

