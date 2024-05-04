package com.example.gourmetcompass.models;

public class Review {
    String id, description, ratings, reviewerId;
    boolean liked, disliked;
    // TODO: upload image urls

    public Review() {
    }

    public Review(String id, String reviewerId, String description, String ratings, boolean liked, boolean disliked) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.description = description;
        this.ratings = ratings;
        this.liked = liked;
        this.disliked = disliked;
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

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isDisliked() {
        return disliked;
    }

    public void setDisliked(boolean disliked) {
        this.disliked = disliked;
    }

}

// Consider this model class for Firestore
//public class Review {
//    private String reviewId;
//    private String userId;
//    private String restaurantId;
//    private float rating;
//    private String comment;
//    private long timestamp;
//
//    // Empty constructor needed for Firestore
//    public Review() {}
//
//    public Review(String reviewId, String userId, String restaurantId, float rating, String comment, long timestamp) {
//        this.reviewId = reviewId;
//        this.userId = userId;
//        this.restaurantId = restaurantId;
//        this.rating = rating;
//        this.comment = comment;
//        this.timestamp = timestamp;
//    }
//
//    // Getters and setters
//    public String getReviewId() {
//        return reviewId;
//    }
//
//    public void setReviewId(String reviewId) {
//        this.reviewId = reviewId;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getRestaurantId() {
//        return restaurantId;
//    }
//
//    public void setRestaurantId(String restaurantId) {
//        this.restaurantId = restaurantId;
//    }
//
//    public float getRating() {
//        return rating;
//    }
//
//    public void setRating(float rating) {
//        this.rating = rating;
//    }
//
//    public String getComment() {
//        return comment;
//    }
//
//    public void setComment(String comment) {
//        this.comment = comment;
//    }
//
//    public long getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(long timestamp) {
//        this.timestamp = timestamp;
//    }
//}