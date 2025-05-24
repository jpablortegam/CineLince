// HomeViewController.java
package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import com.example.cinelinces.services.MovieService;
import com.example.cinelinces.utils.ButtonHoverAnimator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeViewController implements Initializable {
    @FXML private StackPane rootStack;
    @FXML private Pane overlayPane;
    @FXML private Button btnVerHorarios;
    @FXML private FlowPane upcomingPane;
    @FXML private Button btnVerTodas;

    private final MovieService movieService = new MovieService();
    private List<com.example.cinelinces.controllers.MovieCardViewController> cardControllers = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ButtonHoverAnimator.applyHoverEffect(btnVerHorarios);
        loadMovieCards();
    }

    private void loadMovieCards() {
        List<Movie> upcoming = movieService.fetchUpcoming();
        upcomingPane.getChildren().clear();
        cardControllers.clear();

        for (Movie movie : upcoming) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/cinelinces/movieCard.fxml")
                );
                Node cardNode = loader.load();

                MovieCardViewController ctrl = loader.getController();
                ctrl.setMovieData(movie);
                // Inicializar contexto para expansión con blur
                ctrl.initContext(upcomingPane, overlayPane);

                // Habilitar hover y click handlers
                cardNode.setOnMouseEntered(ctrl::onCardHover);
                cardNode.setOnMouseExited(ctrl::onCardHoverExit);
                cardNode.setOnMouseClicked(ctrl::onCardClick);

                upcomingPane.getChildren().add(cardNode);
                cardControllers.add(ctrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
