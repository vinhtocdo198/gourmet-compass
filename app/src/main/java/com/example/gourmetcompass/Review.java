package com.example.gourmetcompass;

import com.example.gourmetcompass.models.Restaurant;

public class Review {
    String reviewDescription;
    Restaurant restaurant;

    public Review(String reviewDescription, Restaurant restaurant) {
        this.reviewDescription = reviewDescription;
        this.restaurant = restaurant;
    }

    public String getReviewDescription() {
        return reviewDescription;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }
}
