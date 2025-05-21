package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import com.example.cinelinces.services.MovieService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class HomeViewController {

    @FXML public StackPane rootStack;
    @FXML public Pane overlayPane;
    @FXML public Button btnVerHorarios;
    @FXML private FlowPane upcomingPane;

    private final VBox dialogPanel = new VBox(10);
    private final Button closeBtn = new Button("Cerrar");

    private final MovieService movieService = new MovieService();

    @FXML
    private void initialize() {
        // Crear panel desde el inicio
        createDialogPanel();

        // Listener para el botón principal
        btnVerHorarios.setOnAction(e -> {
            if (!dialogPanel.isVisible()) {
                showDialogFromButton(btnVerHorarios);
            } else {
                hideDialogToButton(btnVerHorarios);
            }
        });

        closeBtn.setOnAction(e -> hideDialogToButton(btnVerHorarios));

        // Cargar películas
        List<Movie> all = movieService.fetchUpcoming();
        int limit = Math.min(3, all.size());
        for (int i = 0; i < limit; i++) {
            Movie m = all.get(i);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/movieCard.fxml"));
                Node card = loader.load();
                MovieCardViewController ctrl = loader.getController();
                ctrl.setMovie(m);
                upcomingPane.getChildren().add(card);
            } catch (IOException e) {
                System.err.println("Error cargando MovieCard.fxml: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createDialogPanel() {
        dialogPanel.setStyle("""
            -fx-background-color: white;
            -fx-padding: 20;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.3, 0, 4);
        """);
        dialogPanel.setPrefWidth(200); // define ancho deseado
        dialogPanel.setPrefHeight(150);

        dialogPanel.setOpacity(0);
        dialogPanel.setScaleX(0.1);
        dialogPanel.setScaleY(0.1);
        dialogPanel.setVisible(false);

        closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6;");

        dialogPanel.getChildren().addAll(new Label("Horarios disponibles..."), closeBtn);
        overlayPane.getChildren().add(dialogPanel);
    }

    private void showDialogFromButton(Button button) {
        Bounds btnScene = button.localToScene(button.getBoundsInLocal());
        Bounds stackScene = rootStack.localToScene(rootStack.getBoundsInLocal());

        double startX = btnScene.getMinX() - stackScene.getMinX();
        double startY = btnScene.getMinY() - stackScene.getMinY();

        double centerX = (rootStack.getWidth() - dialogPanel.getPrefWidth()) / 2;
        double centerY = (rootStack.getHeight() - dialogPanel.getPrefHeight()) / 2;

        dialogPanel.setLayoutX(startX);
        dialogPanel.setLayoutY(startY);
        dialogPanel.setScaleX(0.1);
        dialogPanel.setScaleY(0.1);
        dialogPanel.setOpacity(0);
        dialogPanel.setVisible(true);

        TranslateTransition move = new TranslateTransition(Duration.millis(300), dialogPanel);
        move.setToX(centerX - startX);
        move.setToY(centerY - startY);
        move.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), dialogPanel);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.millis(300), dialogPanel);
        fade.setToValue(1);

        new ParallelTransition(move, scale, fade).play();
    }

    private void hideDialogToButton(Button button) {
        Bounds btnScene = button.localToScene(button.getBoundsInLocal());
        Bounds stackScene = rootStack.localToScene(rootStack.getBoundsInLocal());

        double endX = btnScene.getMinX() - stackScene.getMinX();
        double endY = btnScene.getMinY() - stackScene.getMinY();

        double currentX = dialogPanel.getLayoutX();
        double currentY = dialogPanel.getLayoutY();

        TranslateTransition move = new TranslateTransition(Duration.millis(250), dialogPanel);
        move.setToX(endX - currentX);
        move.setToY(endY - currentY);
        move.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition scale = new ScaleTransition(Duration.millis(250), dialogPanel);
        scale.setToX(0.1);
        scale.setToY(0.1);
        scale.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fade = new FadeTransition(Duration.millis(250), dialogPanel);
        fade.setToValue(0);

        ParallelTransition transition = new ParallelTransition(move, scale, fade);
        transition.setOnFinished(e -> {
            dialogPanel.setVisible(false);
            dialogPanel.setTranslateX(0);
            dialogPanel.setTranslateY(0);
        });
        transition.play();
    }
}
