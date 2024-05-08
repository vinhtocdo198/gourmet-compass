package com.example.gourmetcompass.models;

public class Reply {
    String id, replierId, description;
    long timestamp;

    public Reply() {
    }

    public Reply(String id, String replierId, String description, long timestamp) {
        this.id = id;
        this.replierId = replierId;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReplierId() {
        return replierId;
    }

    public void setReplierId(String replierId) {
        this.replierId = replierId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
