package com.example.gourmetcompass.models;

import java.util.ArrayList;

public class Restaurant {
    String id, name, description, address, phoneNo, openingHours, ratings, category, tag;
    String resThumbnail, resAppBar;
    ArrayList<String> resGallery;
    int ratingCount;

    boolean isGourmetsChoice;

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

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
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

    public boolean isGourmetsChoice() {
        return isGourmetsChoice;
    }

    public void setGourmetsChoice(boolean gourmetsChoice) {
        isGourmetsChoice = gourmetsChoice;
    }

    public String getResThumbnail() {
        return resThumbnail;
    }

    public void setResThumbnail(String resThumbnail) {
        this.resThumbnail = resThumbnail;
    }

    public String getResAppBar() {
        return resAppBar;
    }

    public void setResAppBar(String resAppBar) {
        this.resAppBar = resAppBar;
    }

    public ArrayList<String> getResGallery() {
        return resGallery;
    }

    public void setResGallery(ArrayList<String> resGallery) {
        this.resGallery = resGallery;
    }

}
