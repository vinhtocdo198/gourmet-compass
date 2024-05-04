package com.example.gourmetcompass.models;

import java.util.ArrayList;

public class Review {
    String id, description, ratings, reviewerId;
    long timestamp;
    ArrayList<String> likedUserIds, dislikedUserIds;
    // TODO: upload image urls

    public Review() {
    }

    public Review(String id, String reviewerId, String description, String ratings, long timestamp, ArrayList<String> likedUserIds, ArrayList<String> dislikedUserIds) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.description = description;
        this.ratings = ratings;
        this.timestamp = timestamp;
        this.likedUserIds = likedUserIds;
        this.dislikedUserIds = dislikedUserIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
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
}