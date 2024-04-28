package com.example.gourmetcompass.models;

public class Review {
    String id, description, ratings; // TODO: Create User class
    int replyCount, likeCount, dislikeCount;
    boolean liked, disliked;

    public Review() {
    }

    public Review(String id, String description, String ratings, int replyCount, int likeCount, int dislikeCount) {
        this.id = id;
        this.description = description;
        this.ratings = ratings;
        this.replyCount = replyCount;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
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
