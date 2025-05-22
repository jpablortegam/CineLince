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
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class HomeViewController {

    @FXML public StackPane rootStack;
    @FXML public Pane overlayPane;
    @FXML public Button btnVerHorarios;
    @FXML private FlowPane upcomingPane;

    private final VBox dialogPanel = new VBox(15);
    private final Button closeBtn = new Button("Cerrar");
    private final Label titleLabel = new Label("Horarios Disponibles");
    private final Label placeholderContent = new Label("Próximamente se mostrarán los horarios detallados aquí.");

    // Capa semitransparente para oscurecer el fondo
    private final Rectangle backgroundOverlay = new Rectangle();
    // Blur que solo aplicaremos a los nodos de fondo
    private final GaussianBlur backgroundBlur = new GaussianBlur(0);

    private final MovieService movieService = new MovieService();

    // Duraciones
    private static final Duration ANIMATION_DURATION_SHOW = Duration.millis(600);
    private static final Duration ANIMATION_DURATION_HIDE = Duration.millis(450);

    // Interpoladores
    private static final Interpolator SMOOTH_EASE = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);
    private static final Interpolator BOUNCE_OUT   = Interpolator.SPLINE(0.68,  0.1,  0.265, 0.99);
    private static final Interpolator EASE_IN_OUT_BACK = Interpolator.SPLINE(0.68, 0.1, 0.32, 0.9);

    @FXML
    private void initialize() {
        setupBackgroundOverlay();
        createDialogPanel();
        setupButtonHoverEffects();

        btnVerHorarios.setOnAction(e -> {

                showDialogFromButton(btnVerHorarios);

        });
        closeBtn.setOnAction(e -> {

                hideDialogToButton(btnVerHorarios);

        });

        // Cargar tarjetas de próximas películas
        List<Movie> all = movieService.fetchUpcoming();
        int limit = Math.min(6, all.size());
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


    private void setupBackgroundOverlay() {
        backgroundOverlay.setFill(Color.BLACK);
        backgroundOverlay.setOpacity(0);
        backgroundOverlay.setVisible(false);
        backgroundOverlay.setMouseTransparent(false);
        backgroundOverlay.widthProperty().bind(overlayPane.widthProperty());
        backgroundOverlay.heightProperty().bind(overlayPane.heightProperty());
        backgroundOverlay.setOnMouseClicked(e -> {
            if (dialogPanel.isVisible()) {
                hideDialogToButton(btnVerHorarios);
            }
        });
        overlayPane.getChildren().add(0, backgroundOverlay);
    }

    private void setupButtonHoverEffects() {
        ScaleTransition hoverIn = new ScaleTransition(Duration.millis(150), btnVerHorarios);
        hoverIn.setToX(1.05);
        hoverIn.setToY(1.05);
        hoverIn.setInterpolator(SMOOTH_EASE);

        ScaleTransition hoverOut = new ScaleTransition(Duration.millis(150), btnVerHorarios);
        hoverOut.setToX(1.0);
        hoverOut.setToY(1.0);
        hoverOut.setInterpolator(SMOOTH_EASE);

        btnVerHorarios.setOnMouseEntered(e -> {
            if (!btnVerHorarios.isDisable()) hoverIn.play();
        });
        btnVerHorarios.setOnMouseExited(e -> {
            if (!btnVerHorarios.isDisable()) hoverOut.play();
        });
    }

    private void createDialogPanel() {
        dialogPanel.setId("dialogPanel");
        dialogPanel.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #ffffff, #f8f9fa);
            -fx-padding: 25;
            -fx-background-radius: 16;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 25, 0.4, 0, 8);
            -fx-border-radius: 16;
            -fx-border-color: rgba(0,0,0,0.1);
            -fx-border-width: 1;
        """);
        dialogPanel.setPrefSize(350, 300);
        dialogPanel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        titleLabel.setStyle("""
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            -fx-text-fill: #2c3e50;
        """);

        placeholderContent.setWrapText(true);
        placeholderContent.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");

        closeBtn.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b);
            -fx-text-fill: white;
            -fx-background-radius: 8;
            -fx-font-weight: bold;
            -fx-padding: 12 24;
            -fx-font-size: 14px;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.3, 0, 2);
        """);
        setupCloseButtonHover();

        VBox contentBox = new VBox(15, placeholderContent);
        contentBox.setAlignment(javafx.geometry.Pos.CENTER);

        dialogPanel.getChildren().addAll(titleLabel, contentBox, closeBtn);
        dialogPanel.setAlignment(javafx.geometry.Pos.CENTER);
        dialogPanel.setVisible(false);
        dialogPanel.setOpacity(0);

        // Asegurarnos de que siempre esté encima
        overlayPane.getChildren().remove(dialogPanel);
        overlayPane.getChildren().add(dialogPanel);
    }

    private void setupCloseButtonHover() {
        closeBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), closeBtn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.setInterpolator(SMOOTH_EASE);
            st.play();
        });
        closeBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), closeBtn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(SMOOTH_EASE);
            st.play();
        });
    }



    private void showDialogFromButton(Button button) {
        dialogPanel.toFront();

        // 1) Aplicar blur solo al fondo
        backgroundBlur.setRadius(0);
        for (Node child : rootStack.getChildren()) {
            if (child != overlayPane) {
                child.setEffect(backgroundBlur);
            }
        }

        // 2) Oscurecer el fondo
        backgroundOverlay.setVisible(true);
        FadeTransition fadeBgIn = new FadeTransition(ANIMATION_DURATION_SHOW.multiply(0.8), backgroundOverlay);
        fadeBgIn.setFromValue(0);
        fadeBgIn.setToValue(0.4);
        fadeBgIn.setInterpolator(SMOOTH_EASE);

        // 3) Animar blur de fondo
        Timeline blurIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(backgroundBlur.radiusProperty(), 0)),
                new KeyFrame(ANIMATION_DURATION_SHOW.multiply(0.8),
                        new KeyValue(backgroundBlur.radiusProperty(), 8, SMOOTH_EASE))
        );

        // 4) Preparar diálogo (posición y escala inicial)
        Bounds btnBounds    = button.localToScreen(button.getBoundsInLocal());
        Bounds parentBounds = overlayPane.localToScreen(overlayPane.getBoundsInLocal());
        double initX   = btnBounds.getMinX() - parentBounds.getMinX();
        double initY   = btnBounds.getMinY() - parentBounds.getMinY();
        double scaleX  = button.getWidth()  / dialogPanel.getPrefWidth();
        double scaleY  = button.getHeight() / dialogPanel.getPrefHeight();

        dialogPanel.setLayoutX(initX);
        dialogPanel.setLayoutY(initY);
        dialogPanel.setScaleX(scaleX);
        dialogPanel.setScaleY(scaleY);
        dialogPanel.setOpacity(0);
        dialogPanel.setVisible(true);

        double finalX = (overlayPane.getWidth() - dialogPanel.getPrefWidth())  / 2;
        double finalY = (overlayPane.getHeight() - dialogPanel.getPrefHeight()) / 2;

        // 5) Translate + escala con bounce
        TranslateTransition trans = new TranslateTransition(ANIMATION_DURATION_SHOW, dialogPanel);
        trans.setToX(finalX - initX);
        trans.setToY(finalY - initY);
        trans.setInterpolator(EASE_IN_OUT_BACK);

        Timeline scaleTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dialogPanel.scaleXProperty(), scaleX),
                        new KeyValue(dialogPanel.scaleYProperty(), scaleY)),
                new KeyFrame(ANIMATION_DURATION_SHOW.multiply(0.7),
                        new KeyValue(dialogPanel.scaleXProperty(), 1.08, SMOOTH_EASE),
                        new KeyValue(dialogPanel.scaleYProperty(), 1.08, SMOOTH_EASE)),
                new KeyFrame(ANIMATION_DURATION_SHOW,
                        new KeyValue(dialogPanel.scaleXProperty(), 1.0, BOUNCE_OUT),
                        new KeyValue(dialogPanel.scaleYProperty(), 1.0, BOUNCE_OUT))
        );

        // 6) Aparecer diálogo
        FadeTransition fadeDialogIn = new FadeTransition(ANIMATION_DURATION_SHOW.multiply(0.6), dialogPanel);
        fadeDialogIn.setFromValue(0);
        fadeDialogIn.setToValue(1);
        fadeDialogIn.setDelay(ANIMATION_DURATION_SHOW.multiply(0.2));
        fadeDialogIn.setInterpolator(SMOOTH_EASE);

        // 7) Pop extra
        ScaleTransition zoomIn = new ScaleTransition(ANIMATION_DURATION_SHOW, dialogPanel);
        zoomIn.setFromX(0.9);
        zoomIn.setFromY(0.9);
        zoomIn.setToX(1.0);
        zoomIn.setToY(1.0);
        zoomIn.setInterpolator(Interpolator.EASE_OUT);

        // 8) Animar contenido interno
        animateDialogContentIn();

        // 9) Ejecutar todo **sin** animación del botón
        ParallelTransition main = new ParallelTransition(trans, fadeDialogIn, fadeBgIn, zoomIn);
        main.setOnFinished(e -> dialogPanel.setUserData(false));
        ParallelTransition all = new ParallelTransition(main, blurIn, scaleTimeline);
        all.play();
    }


    private void hideDialogToButton(Button button) {
        dialogPanel.setUserData(true);

        // 1) Salida de contenido
        animateDialogContentOut();

        // 2) Desenfocar y oscurecer fondo
        Timeline blurOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(backgroundBlur.radiusProperty(), 8)),
                new KeyFrame(ANIMATION_DURATION_HIDE.multiply(0.8),
                        new KeyValue(backgroundBlur.radiusProperty(), 0, SMOOTH_EASE))
        );
        FadeTransition fadeBgOut = new FadeTransition(ANIMATION_DURATION_HIDE.multiply(0.8), backgroundOverlay);
        fadeBgOut.setToValue(0);
        fadeBgOut.setInterpolator(SMOOTH_EASE);
        fadeBgOut.setOnFinished(e -> {
            backgroundOverlay.setVisible(false);
            for (Node child : rootStack.getChildren()) {
                if (child != overlayPane) child.setEffect(null);
            }
        });

        // 3) Regresar diálogo al botón
        Bounds btnBounds    = button.localToScreen(button.getBoundsInLocal());
        Bounds parentBounds = overlayPane.localToScreen(overlayPane.getBoundsInLocal());
        double finalX    = btnBounds.getMinX() - parentBounds.getMinX();
        double finalY    = btnBounds.getMinY() - parentBounds.getMinY();
        double finalScaleX = button.getWidth()  / dialogPanel.getPrefWidth();
        double finalScaleY = button.getHeight() / dialogPanel.getPrefHeight();

        TranslateTransition trans = new TranslateTransition(ANIMATION_DURATION_HIDE, dialogPanel);
        trans.setToX(finalX - dialogPanel.getLayoutX());
        trans.setToY(finalY - dialogPanel.getLayoutY());
        trans.setInterpolator(EASE_IN_OUT_BACK);

        ScaleTransition scale = new ScaleTransition(ANIMATION_DURATION_HIDE, dialogPanel);
        scale.setToX(finalScaleX);
        scale.setToY(finalScaleY);
        scale.setInterpolator(SMOOTH_EASE);

        FadeTransition fadeDialogOut = new FadeTransition(ANIMATION_DURATION_HIDE.multiply(0.7), dialogPanel);
        fadeDialogOut.setToValue(0);
        fadeDialogOut.setDelay(ANIMATION_DURATION_HIDE.multiply(0.2));
        fadeDialogOut.setInterpolator(SMOOTH_EASE);

        // 4) Ejecutar todo **sin** animación de botón
        ParallelTransition pt = new ParallelTransition(trans, scale, fadeDialogOut, fadeBgOut);
        pt.setOnFinished(e -> {
            dialogPanel.setVisible(false);
            dialogPanel.setOpacity(1);
            dialogPanel.setScaleX(1);
            dialogPanel.setScaleY(1);
            dialogPanel.setTranslateX(0);
            dialogPanel.setTranslateY(0);
            dialogPanel.setUserData(false);
        });

        ParallelTransition all = new ParallelTransition(pt, blurOut);
        all.play();
    }


    private void animateDialogContentIn() {
        titleLabel.setOpacity(0);    titleLabel.setTranslateY(-20);
        placeholderContent.setOpacity(0); placeholderContent.setTranslateY(-15);
        closeBtn.setOpacity(0);      closeBtn.setTranslateY(-10);

        Timeline tl = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(titleLabel.opacityProperty(),    1, SMOOTH_EASE),
                        new KeyValue(titleLabel.translateYProperty(), 0, SMOOTH_EASE)),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(placeholderContent.opacityProperty(),    1, SMOOTH_EASE),
                        new KeyValue(placeholderContent.translateYProperty(), 0, SMOOTH_EASE)),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(closeBtn.opacityProperty(),    1, SMOOTH_EASE),
                        new KeyValue(closeBtn.translateYProperty(), 0, SMOOTH_EASE))
        );
        tl.play();
    }

    private void animateDialogContentOut() {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(closeBtn.opacityProperty(), 1),
                        new KeyValue(placeholderContent.opacityProperty(), 1),
                        new KeyValue(titleLabel.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(150),
                        new KeyValue(closeBtn.opacityProperty(), 0, SMOOTH_EASE),
                        new KeyValue(closeBtn.translateYProperty(), 10, SMOOTH_EASE)),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(placeholderContent.opacityProperty(), 0, SMOOTH_EASE),
                        new KeyValue(placeholderContent.translateYProperty(), 15, SMOOTH_EASE)),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(titleLabel.opacityProperty(), 0, SMOOTH_EASE),
                        new KeyValue(titleLabel.translateYProperty(), 20, SMOOTH_EASE))
        );
        tl.play();
    }
}
