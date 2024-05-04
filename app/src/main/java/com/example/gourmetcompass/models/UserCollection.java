package com.example.gourmetcompass.models;

public class UserCollection {
    String id, name, type;
    long timestamp;

    public UserCollection() {
    }

    public UserCollection(String id, String name, String type, long timestamp) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.timestamp = timestamp;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
