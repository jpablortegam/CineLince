package com.example.cinelinces.model;

import javafx.scene.image.Image;

import java.util.List;

public class Movie {
    private String title;
    private String synopsis;
    private int duration;         // duración en minutos
    private List<String> cast;    // lista de nombres de actores
    private String posterUrl;
    private List<String> genre;   // ← nuevo

    public Movie() { }

    // Constructor completo
    public Movie(String title,
                 String synopsis,
                 int duration,
                 List<String> cast,
                 String posterUrl,
                 List<String> genre) {
        this.title     = title;
        this.synopsis  = synopsis;
        this.duration  = duration;
        this.cast      = cast;
        this.posterUrl = posterUrl;
        this.genre     = genre;
    }

    // Getter/Setter para title
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    // Método separado para obtener el año si está entre paréntesis al final
    public String getYear() {
        if (title != null) {
            int open = title.lastIndexOf('(');
            int close = title.lastIndexOf(')');
            if (open > 0 && close > open) {
                return title.substring(open + 1, close);
            }
        }
        return "";
    }

    // Getters/Setters para posterUrl
    public String getPosterUrl() {
        return posterUrl;
    }
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    // Getters/Setters para synopsis
    public String getSynopsis() {
        return synopsis;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    // Getters/Setters para duration
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    // Getters/Setters para cast
    public List<String> getCast() {
        return cast;
    }
    public void setCast(List<String> cast) {
        this.cast = cast;
    }

    public Image getPosterImage() {
        if (posterUrl != null) {


        }
        return null;
    }



    public String getImageUrl() {
        return posterUrl;
    }

    public List<String> getGenre() {
        return genre;
    }
    public void setGenre(List<String> genre) {
        this.genre = genre;
    }
}
