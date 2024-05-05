package com.example.gourmetcompass.models;

import java.util.ArrayList;

public class UserCollection {
    String id, name, type;
    ArrayList<String> restaurantIds;
    boolean isChecked;

    public UserCollection() {
    }

    public UserCollection(String id, String name, String type, ArrayList<String> restaurantIds, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.restaurantIds = restaurantIds;
        this.isChecked = isChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String collectionId) {
        this.id = collectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String collectionName) {
        this.name = collectionName;
    }

    public String getType() {
        return type;
    }

    public void setType(String collectionType) {
        this.type = collectionType;
    }

    public ArrayList<String> getRestaurantIds() {
        return restaurantIds;
    }

    public void setRestaurantIds(ArrayList<String> restaurantIds) {
        this.restaurantIds = restaurantIds;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

}
