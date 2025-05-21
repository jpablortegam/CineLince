package com.example.cinelinces.model;

public class Movie {
    private String title;
    private String posterUrl;

    public Movie() { }

    public Movie(String title, String posterUrl) {
        this.title = title;
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getYear() {
        // Assuming the year is part of the title for demonstration purposes
        // In a real application, you would likely have a separate field for the year
        return title.substring(title.length() - 4);
    }
}
