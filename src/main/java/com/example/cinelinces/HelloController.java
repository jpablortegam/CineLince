package com.example.cinelinces;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class HelloController {
    @FXML private AnchorPane rootPane;
    @FXML private Button btnVerHorarios;

    private boolean isExpanded = false;
    private double origW, origH, origX, origY;
    private Pane backdrop;
    private StackPane overlayPane;
    private Rectangle clip;
    private VBox content;

    @FXML
    public void onVerHorarios(ActionEvent e) {
        if (!isExpanded) expandToDialog();
        else collapseToButton();
    }

    private void expandToDialog() {
        // 1) Capturar bounds del botón con mejor precisión
        Bounds btnBounds = btnVerHorarios.getBoundsInParent();
        origX = btnBounds.getMinX();
        origY = btnBounds.getMinY();
        origW = btnBounds.getWidth();
        origH = btnBounds.getHeight();

        // 2) Backdrop con fade in suave
        backdrop = new Pane();
        backdrop.setStyle("-fx-background-color: rgba(0,0,0,0);");
        backdrop.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        backdrop.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        backdrop.setOnMouseClicked(evt -> collapseToButton());

        // 3) OverlayPane inicial (mismo tamaño y posición que el botón)
        overlayPane = new StackPane();
        overlayPane.setLayoutX(origX);
        overlayPane.setLayoutY(origY);
        overlayPane.setPrefSize(origW, origH);
        overlayPane.setMaxSize(origW, origH);

        // Background inicial con border radius igual al botón
        BackgroundFill initialFill = new BackgroundFill(
                Color.web("#007bff"), // Color del botón original
                new CornerRadii(origH/2),
                Insets.EMPTY
        );
        overlayPane.setBackground(new Background(initialFill));

        // Shadow inicial sutil
        DropShadow initialShadow = new DropShadow();
        initialShadow.setRadius(5);
        initialShadow.setOffsetY(2);
        initialShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        overlayPane.setEffect(initialShadow);

        // 4) Clip para bordes redondeados
        clip = new Rectangle(origW, origH);
        clip.setArcWidth(origH);
        clip.setArcHeight(origH);
        overlayPane.setClip(clip);

        // 5) Contenido del diálogo (inicialmente invisible)
        content = createDialogContent();
        content.setOpacity(0);
        content.setScaleX(0.8);
        content.setScaleY(0.8);
        overlayPane.getChildren().add(content);

        // 6) Añadir elementos al rootPane
        rootPane.getChildren().addAll(backdrop, overlayPane);
        btnVerHorarios.setVisible(false);

        // 7) Animación del backdrop
        FadeTransition backdropFade = new FadeTransition(Duration.millis(200), backdrop);
        backdropFade.setFromValue(0);
        backdropFade.setToValue(1);
        backdropFade.setOnFinished(e -> backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.5);"));

        // 8) Cálculos para el tamaño final
        double targetW = Math.min(rootPane.getWidth() * 0.8, 500);
        double targetH = Math.min(rootPane.getHeight() * 0.8, 600);
        double targetX = (rootPane.getWidth() - targetW) / 2;
        double targetY = (rootPane.getHeight() - targetH) / 2;

        // 9) Animación principal del morph
        Timeline morphAnimation = new Timeline();

        // Keyframes para posición y tamaño
        morphAnimation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(overlayPane.layoutXProperty(), origX),
                        new KeyValue(overlayPane.layoutYProperty(), origY),
                        new KeyValue(overlayPane.prefWidthProperty(), origW),
                        new KeyValue(overlayPane.prefHeightProperty(), origH),
                        new KeyValue(overlayPane.maxWidthProperty(), origW),
                        new KeyValue(overlayPane.maxHeightProperty(), origH),
                        new KeyValue(clip.widthProperty(), origW),
                        new KeyValue(clip.heightProperty(), origH),
                        new KeyValue(clip.arcWidthProperty(), origH),
                        new KeyValue(clip.arcHeightProperty(), origH)
                ),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(overlayPane.layoutXProperty(), targetX, Interpolator.SPLINE(0.23, 1, 0.32, 1)),
                        new KeyValue(overlayPane.layoutYProperty(), targetY, Interpolator.SPLINE(0.23, 1, 0.32, 1)),
                        new KeyValue(overlayPane.prefWidthProperty(), targetW, Interpolator.SPLINE(0.23, 1, 0.32, 1)),
                        new KeyValue(overlayPane.prefHeightProperty(), targetH, Interpolator.SPLINE(0.23, 1, 0.32, 1)),
                        new KeyValue(overlayPane.maxWidthProperty(), targetW, Interpolator.SPLINE(0.23, 1, 0.32, 1)),
                        new KeyValue(overlayPane.maxHeightProperty(), targetH, Interpolator.SPLINE(0.23, 1, 0.32, 1)),
                        new KeyValue(clip.widthProperty(), targetW, Interpolator.SPLINE(0.23, 1, 0.32, 1)),
                        new KeyValue(clip.heightProperty(), targetH, Interpolator.SPLINE(0.23, 1, 0.32, 1)),
                        new KeyValue(clip.arcWidthProperty(), 24, Interpolator.SPLINE(0.23, 1, 0.32, 1)),
                        new KeyValue(clip.arcHeightProperty(), 24, Interpolator.SPLINE(0.23, 1, 0.32, 1))
                )
        );

        // Background color transition
        BackgroundFill finalFill = new BackgroundFill(
                Color.WHITE,
                new CornerRadii(12),
                Insets.EMPTY
        );

        morphAnimation.setOnFinished(e -> {
            overlayPane.setBackground(new Background(finalFill));

            // Shadow final más prominente
            DropShadow finalShadow = new DropShadow();
            finalShadow.setRadius(20);
            finalShadow.setOffsetY(10);
            finalShadow.setColor(Color.rgb(0, 0, 0, 0.2));
            overlayPane.setEffect(finalShadow);

            // Animar contenido
            animateContentIn();
        });

        // Ejecutar animaciones
        backdropFade.play();
        morphAnimation.play();
    }

    private VBox createDialogContent() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxWidth(Region.USE_PREF_SIZE);

        // Título
        Label title = new Label("Horarios Disponibles");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#333333"));

        // Subtítulo
        Label subtitle = new Label("Dune: Parte Dos");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web("#666666"));

        // Contenedor de horarios
        VBox horariosContainer = new VBox(12);
        horariosContainer.setAlignment(Pos.CENTER);

        // Horarios con formato mejorado
        String[] horarios = {
                "10:00 AM", "1:30 PM", "4:00 PM",
                "6:30 PM", "9:00 PM", "11:30 PM"
        };

        for (String horario : horarios) {
            Button btnHorario = new Button(horario);
            btnHorario.setStyle(
                    "-fx-background-color: #f8f9fa;" +
                            "-fx-border-color: #dee2e6;" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 12 24;" +
                            "-fx-font-size: 16;" +
                            "-fx-min-width: 120;"
            );

            // Hover effect
            btnHorario.setOnMouseEntered(e ->
                    btnHorario.setStyle(
                            "-fx-background-color: #007bff;" +
                                    "-fx-border-color: #007bff;" +
                                    "-fx-border-radius: 8;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-padding: 12 24;" +
                                    "-fx-font-size: 16;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-min-width: 120;"
                    )
            );

            btnHorario.setOnMouseExited(e ->
                    btnHorario.setStyle(
                            "-fx-background-color: #f8f9fa;" +
                                    "-fx-border-color: #dee2e6;" +
                                    "-fx-border-radius: 8;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-padding: 12 24;" +
                                    "-fx-font-size: 16;" +
                                    "-fx-min-width: 120;"
                    )
            );

            btnHorario.setOnAction(e -> {
                // Aquí puedes agregar la lógica para seleccionar un horario
                System.out.println("Horario seleccionado: " + horario);
                collapseToButton();
            });

            horariosContainer.getChildren().add(btnHorario);
        }

        // Botón cerrar
        Button closeButton = new Button("✕ Cerrar");
        closeButton.setStyle(
                "-fx-background-color: #6c757d;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 24;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16;"
        );
        closeButton.setOnAction(e -> collapseToButton());

        container.getChildren().addAll(title, subtitle, horariosContainer, closeButton);
        return container;
    }

    private void animateContentIn() {
        // Fade in y scale up del contenido
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), content);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.setInterpolator(Interpolator.SPLINE(0.23, 1, 0.32, 1));

        ParallelTransition contentIn = new ParallelTransition(fadeIn, scaleIn);
        contentIn.setOnFinished(e -> isExpanded = true);
        contentIn.play();
    }

    private void collapseToButton() {
        if (!isExpanded) return;

        // Fade out del contenido
        FadeTransition contentFadeOut = new FadeTransition(Duration.millis(150), content);
        contentFadeOut.setToValue(0);

        ScaleTransition contentScaleOut = new ScaleTransition(Duration.millis(150), content);
        contentScaleOut.setToX(0.8);
        contentScaleOut.setToY(0.8);

        ParallelTransition contentOut = new ParallelTransition(contentFadeOut, contentScaleOut);

        contentOut.setOnFinished(e -> {
            // Cambiar background de vuelta al color del botón
            BackgroundFill buttonFill = new BackgroundFill(
                    Color.web("#007bff"),
                    new CornerRadii(origH/2),
                    Insets.EMPTY
            );
            overlayPane.setBackground(new Background(buttonFill));

            // Animación de vuelta al botón
            Timeline collapseAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(overlayPane.layoutXProperty(), overlayPane.getLayoutX()),
                            new KeyValue(overlayPane.layoutYProperty(), overlayPane.getLayoutY()),
                            new KeyValue(overlayPane.prefWidthProperty(), overlayPane.getWidth()),
                            new KeyValue(overlayPane.prefHeightProperty(), overlayPane.getHeight()),
                            new KeyValue(overlayPane.maxWidthProperty(), overlayPane.getWidth()),
                            new KeyValue(overlayPane.maxHeightProperty(), overlayPane.getHeight()),
                            new KeyValue(clip.widthProperty(), clip.getWidth()),
                            new KeyValue(clip.heightProperty(), clip.getHeight()),
                            new KeyValue(clip.arcWidthProperty(), clip.getArcWidth()),
                            new KeyValue(clip.arcHeightProperty(), clip.getArcHeight())
                    ),
                    new KeyFrame(Duration.millis(350),
                            new KeyValue(overlayPane.layoutXProperty(), origX, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53)),
                            new KeyValue(overlayPane.layoutYProperty(), origY, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53)),
                            new KeyValue(overlayPane.prefWidthProperty(), origW, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53)),
                            new KeyValue(overlayPane.prefHeightProperty(), origH, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53)),
                            new KeyValue(overlayPane.maxWidthProperty(), origW, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53)),
                            new KeyValue(overlayPane.maxHeightProperty(), origH, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53)),
                            new KeyValue(clip.widthProperty(), origW, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53)),
                            new KeyValue(clip.heightProperty(), origH, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53)),
                            new KeyValue(clip.arcWidthProperty(), origH, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53)),
                            new KeyValue(clip.arcHeightProperty(), origH, Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53))
                    )
            );

            // Fade out del backdrop
            FadeTransition backdropFadeOut = new FadeTransition(Duration.millis(300), backdrop);
            backdropFadeOut.setToValue(0);

            collapseAnimation.setOnFinished(e2 -> cleanup());

            ParallelTransition finalAnimation = new ParallelTransition(collapseAnimation, backdropFadeOut);
            finalAnimation.play();
        });

        contentOut.play();
    }

    private void cleanup() {
        rootPane.getChildren().removeAll(backdrop, overlayPane);
        overlayPane.setClip(null);
        btnVerHorarios.setVisible(true);
        isExpanded = false;

        // Reset de propiedades
        backdrop = null;
        overlayPane = null;
        clip = null;
        content = null;
    }

    // Handlers de navegación
    public void onEstrenos(ActionEvent e) {
        // Implementar navegación a estrenos
        System.out.println("Navegando a Estrenos");
    }

    public void onProximamente(ActionEvent e) {
        // Implementar navegación a próximamente
        System.out.println("Navegando a Próximamente");
    }

    public void onPopulares(ActionEvent e) {
        // Implementar navegación a populares
        System.out.println("Navegando a Populares");
    }

    public void onMiCuenta(ActionEvent e) {
        // Implementar navegación a mi cuenta
        System.out.println("Navegando a Mi Cuenta");
    }

    public void onSearch(ActionEvent e) {
        // Implementar funcionalidad de búsqueda
        System.out.println("Ejecutando búsqueda");
    }
}