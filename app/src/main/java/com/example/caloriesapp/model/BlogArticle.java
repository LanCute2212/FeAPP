package com.example.caloriesapp.model;

public class BlogArticle {
    private int id;
    private String title;
    private String category;
    private String timeAgo;
    private int imageResourceId;
    private String imageUrl;
    private boolean isFeatured;

    public BlogArticle(int id, String title, String category, String timeAgo, int imageResourceId, boolean isFeatured) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.timeAgo = timeAgo;
        this.imageResourceId = imageResourceId;
        this.imageUrl = null;
        this.isFeatured = isFeatured;
    }

    public BlogArticle(int id, String title, String category, String timeAgo, String imageUrl, boolean isFeatured) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.timeAgo = timeAgo;
        this.imageResourceId = 0;
        this.imageUrl = imageUrl;
        this.isFeatured = isFeatured;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
