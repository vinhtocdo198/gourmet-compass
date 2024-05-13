package com.example.gourmetcompass.models;

import java.util.ArrayList;

public class Review {
    String id, description, ratings, reviewerName, restaurantId, reviewerAvaUrl;
    long timestamp;
    ArrayList<String> likedUserIds, dislikedUserIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getLikedUserIds() {
        return likedUserIds;
    }

    public void setLikedUserIds(ArrayList<String> likedUserIds) {
        this.likedUserIds = likedUserIds;
    }

    public ArrayList<String> getDislikedUserIds() {
        return dislikedUserIds;
    }

    public void setDislikedUserIds(ArrayList<String> dislikedUserIds) {
        this.dislikedUserIds = dislikedUserIds;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getReviewerAvaUrl() {
        return reviewerAvaUrl;
    }

    public void setReviewerAvaUrl(String reviewerAvaUrl) {
        this.reviewerAvaUrl = reviewerAvaUrl;
    }
}