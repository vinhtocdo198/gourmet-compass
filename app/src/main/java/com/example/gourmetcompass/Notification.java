package com.example.gourmetcompass;

public class Notification {
    private String id;
    private int icon;
    private long timestamp;
    private String description;

    public Notification(String id, int icon, long timestamp, String description) {
        this.id = id;
        this.icon = icon;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        long diffTime = System.currentTimeMillis() - timestamp;

        long diffSeconds = diffTime / 1000;
        long diffMinutes = diffSeconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;

        if (diffSeconds == 0){
            return "Just now";
        }else if (diffSeconds >0 && diffSeconds < 60) {
            return diffSeconds + " seconds ago";
        }else if (diffSeconds >= 60 && diffSeconds < 3600){
            return diffMinutes + " minutes ago";
        } else if (diffSeconds >= 3600 && diffSeconds < 86400) {
            return diffHours + " hours ago";
        }
        return diffDays + " days ago";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


