package com.example.gourmetcompass.models;

public class Restaurant {
    private String id, name, description, address, phoneNo, openingHours, ratings, category, tag;

    public Restaurant() {
    }

    public Restaurant(String id, String name, String description, String address, String phoneNo, String openingHours, String ratings, String category, String tag) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.phoneNo = phoneNo;
        this.openingHours = openingHours;
        this.ratings = ratings;
        this.category = category;
        this.tag = tag;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
