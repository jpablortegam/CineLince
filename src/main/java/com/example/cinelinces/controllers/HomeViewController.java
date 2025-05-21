package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import com.example.cinelinces.services.MovieService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.List;

public class HomeViewController {

    // Inyectamos el FlowPane definido en el FXML (fx:id="upcomingPane")
    @FXML
    private FlowPane upcomingPane;

    // Servicio para obtener la lista de películas
    private final MovieService movieService = new MovieService();

    /**
     * Este método se ejecuta automáticamente tras cargar el FXML.
     * Carga sólo las 3 primeras películas en forma de "MovieCard.fxml".
     */
    @FXML
    private void initialize() {
        // Obtenemos todas las próximas películas
        List<Movie> all = movieService.fetchUpcoming();

        // Iteramos sólo hasta 3 (o menos si hay menos películas)
        int limit = Math.min(3, all.size());
        for (int i = 0; i < limit; i++) {
            Movie m = all.get(i);
            try {
                // Cargamos el FXML de la tarjeta de película
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/cinelinces/movieCard.fxml")
                );
                Node card = loader.load();

                // Obtenemos su controlador y le pasamos la película
                MovieCardViewController ctrl = loader.getController();
                ctrl.setMovie(m);

                // Añadimos la tarjeta al FlowPane
                upcomingPane.getChildren().add(card);

            } catch (IOException e) {
                // Si algo falla, lo informamos por consola
                System.err.println("Error cargando MovieCard.fxml: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
