package com.example.gourmetcompass.models;

public class Reply {
    String id, replierName, description, replierAvaUrl, replierId;
    long timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReplierName() {
        return replierName;
    }

    public void setReplierName(String replierName) {
        this.replierName = replierName;
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

    public String getReplierAvaUrl() {
        return replierAvaUrl;
    }

    public void setReplierAvaUrl(String replierAvaUrl) {
        this.replierAvaUrl = replierAvaUrl;
    }
}
