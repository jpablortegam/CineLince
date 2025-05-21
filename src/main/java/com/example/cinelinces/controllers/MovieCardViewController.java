package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MovieCardViewController {

    @FXML
    private ImageView poster;

    @FXML
    private Label title;

    @FXML
    private Label subtitle;

    /**
     * Este método será llamado desde el loader para inicializar
     * la tarjeta con los datos de la película.
     */
    public void setMovie(Movie movie) {
        // Carga la imagen del póster
        try {
            Image img = new Image(getClass().getResourceAsStream(movie.getPosterUrl()));
            poster.setImage(img);
        } catch (Exception e) {
            // En caso de que no encuentre la imagen, dejamos un placeholder o la dejamos vacía
            System.err.println("No se pudo cargar la imagen: " + movie.getPosterUrl());
            e.printStackTrace();
        }

        // Establece el título y el subtítulo (aquí usamos getYear() si quieres)
        title.setText(movie.getTitle());
        subtitle.setText(movie.getYear());
    }
}
