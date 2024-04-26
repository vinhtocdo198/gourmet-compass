package com.example.gourmetcompass.models;

public class Dish {

    String id, name, description, ratings;
    int ratingCount;

    public Dish() {
    }

    public Dish(String id, String name, String description, String ratings, int ratingCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ratings = ratings;
        this.ratingCount = ratingCount;
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

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
