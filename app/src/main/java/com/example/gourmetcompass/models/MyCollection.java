package com.example.gourmetcompass.models;

import java.util.ArrayList;

public class MyCollection {
    String id, name, type;
    ArrayList<String> restaurants, dishes;
    boolean isChecked;

    public MyCollection() {
    }

    public MyCollection(String id, String name, String type, ArrayList<String> restaurants, ArrayList<String> dishes, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.restaurants = restaurants;
        this.dishes = dishes;
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

    public ArrayList<String> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(ArrayList<String> restaurants) {
        this.restaurants = restaurants;
    }

    public ArrayList<String> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<String> dishes) {
        this.dishes = dishes;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

}
