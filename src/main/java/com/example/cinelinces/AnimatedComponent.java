package com.example.cinelinces;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Componente reutilizable para transformar un botón en un diálogo centrado con animaciones suaves.
 */
public class AnimatedComponent {
    private final Pane rootPane;
    private final Button trigger;
    private boolean expanded = false;
    private double origW, origH, origCenterX, origCenterY;

    private Pane backdrop;
    private StackPane overlay;
    private Rectangle clip;
    private VBox content;

    // Colores del tema mejorados
    private final Color buttonColor;
    private final Color buttonHoverColor;
    private final Color dialogColor;

    public AnimatedComponent(Pane rootPane, Button trigger,
                             Color buttonColor, Color buttonHoverColor, Color dialogColor) {
        this.rootPane = rootPane;
        this.trigger = trigger;
        this.buttonColor = buttonColor;
        this.buttonHoverColor = buttonHoverColor;
        this.dialogColor = dialogColor;

        // Mejorar el estilo del botón trigger
        enhanceTriggerButton();
        trigger.setOnAction(e -> toggle());
    }

    private void enhanceTriggerButton() {
        // Crear un gradiente sutil para el botón
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, buttonColor.brighter()),
                new Stop(1, buttonColor)
        );

        trigger.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " +
                        toRgbString(buttonColor.brighter()) + ", " +
                        toRgbString(buttonColor) + ");" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-border-color: " + toRgbString(buttonColor.darker()) + ";" +
                        "-fx-border-width: 1px;" +
                        "-fx-padding: 14px 28px;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-text-fill: white;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 2);"
        );

        // Animaciones de hover más suaves
        trigger.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), trigger);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();

            trigger.setStyle(trigger.getStyle().replace(
                    "dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 2)",
                    "dropshadow(three-pass-box, rgba(0,0,0,0.3), 12, 0, 0, 4)"
            ));
        });

        trigger.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), trigger);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

            trigger.setStyle(trigger.getStyle().replace(
                    "dropshadow(three-pass-box, rgba(0,0,0,0.3), 12, 0, 0, 4)",
                    "dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 2)"
            ));
        });
    }

    private String toRgbString(Color color) {
        return String.format("rgb(%.0f, %.0f, %.0f)",
                color.getRed() * 255,
                color.getGreen() * 255,
                color.getBlue() * 255);
    }

    void toggle() {
        if (!expanded) expand(); else collapse();
    }

    public void expand() {
        // Obtener las coordenadas exactas del botón
        Bounds b = trigger.getBoundsInParent();
        origCenterX = b.getMinX() + b.getWidth() / 2;
        origCenterY = b.getMinY() + b.getHeight() / 2;
        origW = b.getWidth();
        origH = b.getHeight();

        // Tamaños del diálogo
        double targetW = Math.min(rootPane.getWidth() * 0.85, 500);
        double targetH = Math.min(rootPane.getHeight() * 0.85, 650);
        double targetCenterX = rootPane.getWidth() / 2;
        double targetCenterY = rootPane.getHeight() / 2;

        // Crear el fondo con efecto blur
        backdrop = new Pane();
        backdrop.setStyle("-fx-background-color: rgba(0,0,0,0);");
        backdrop.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        backdrop.setOnMouseClicked(evt -> collapse());

        // Crear el overlay que se transformará
        overlay = new StackPane();
        overlay.setLayoutX(origCenterX - origW / 2);
        overlay.setLayoutY(origCenterY - origH / 2);
        overlay.setPrefSize(origW, origH);

        // Gradiente inicial igual al botón
        overlay.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " +
                        toRgbString(buttonColor.brighter()) + ", " +
                        toRgbString(buttonColor) + ");"
        );

        // Clip con esquinas redondeadas
        clip = new Rectangle(origW, origH);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        overlay.setClip(clip);

        // Crear contenido del diálogo (inicialmente invisible)
        content = createDialogContent();
        content.setOpacity(0);
        content.setScaleX(0.7);
        content.setScaleY(0.7);
        overlay.getChildren().add(content);

        // Añadir elementos al panel
        rootPane.getChildren().addAll(backdrop, overlay);
        trigger.setVisible(false);

        // Animación del fondo con blur
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), backdrop);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Cambiar gradualmente el fondo del backdrop
        Timeline backdropColorChange = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(backdrop.styleProperty(), "-fx-background-color: rgba(0,0,0,0);")),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(backdrop.styleProperty(), "-fx-background-color: rgba(0,0,0,0.6);"))
        );

        // Interpolador con curva más natural
        Interpolator easeOutCubic = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);

        // Animación principal de transformación
        Timeline morph = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(overlay.layoutXProperty(), origCenterX - origW / 2),
                        new KeyValue(overlay.layoutYProperty(), origCenterY - origH / 2),
                        new KeyValue(overlay.prefWidthProperty(), origW),
                        new KeyValue(overlay.prefHeightProperty(), origH),
                        new KeyValue(clip.widthProperty(), origW),
                        new KeyValue(clip.heightProperty(), origH),
                        new KeyValue(clip.arcWidthProperty(), 12),
                        new KeyValue(clip.arcHeightProperty(), 12)
                ),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(overlay.layoutXProperty(), targetCenterX - targetW / 2, easeOutCubic),
                        new KeyValue(overlay.layoutYProperty(), targetCenterY - targetH / 2, easeOutCubic),
                        new KeyValue(overlay.prefWidthProperty(), targetW, easeOutCubic),
                        new KeyValue(overlay.prefHeightProperty(), targetH, easeOutCubic),
                        new KeyValue(clip.widthProperty(), targetW, easeOutCubic),
                        new KeyValue(clip.heightProperty(), targetH, easeOutCubic),
                        new KeyValue(clip.arcWidthProperty(), 24, easeOutCubic),
                        new KeyValue(clip.arcHeightProperty(), 24, easeOutCubic)
                )
        );

        // Cambio gradual del color de fondo del overlay
        Timeline overlayColorChange = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(overlay.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom, " +
                                        toRgbString(buttonColor.brighter()) + ", " +
                                        toRgbString(buttonColor) + ");")),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(overlay.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom, " +
                                        toRgbString(dialogColor.brighter()) + ", " +
                                        toRgbString(dialogColor) + ");", easeOutCubic))
        );

        // Ejecutar animaciones
        ParallelTransition expandTransition = new ParallelTransition(
                fadeIn, backdropColorChange, morph, overlayColorChange
        );

        expandTransition.setOnFinished(ev -> animateContentIn());
        expandTransition.play();
    }

    private VBox createDialogContent() {
        VBox v = new VBox(28);
        v.setPadding(new Insets(40));
        v.setAlignment(Pos.TOP_CENTER);

        // Título con gradiente de texto
        Label title = new Label("Horarios Disponibles");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle(
                "-fx-text-fill: linear-gradient(to bottom, #1a365d, #2d3748);" +
                        "-fx-effect: dropshadow(one-pass-box, rgba(255,255,255,0.5), 1, 0, 0, 1);"
        );

        // Subtítulo mejorado
        Label subtitle = new Label("Dune: Parte Dos");
        subtitle.setFont(Font.font("System", FontWeight.MEDIUM, 18));
        subtitle.setTextFill(Color.web("#4a5568"));

        // Grid con mejor espaciado
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(16);

        String[] horarios = {"10:00 AM","1:30 PM","4:00 PM","6:30 PM","9:00 PM","11:30 PM"};
        for (int i = 0; i < horarios.length; i++) {
            Button b = createHorarioButton(horarios[i]);
            grid.add(b, i % 3, i / 3);
        }

        Button close = createCloseButton();

        v.getChildren().addAll(title, subtitle, grid, close);
        return v;
    }

    private Button createHorarioButton(String horario) {
        Button btn = new Button(horario);

        // Estilo base más elegante
        String baseStyle =
                "-fx-background-color: linear-gradient(to bottom, #ffffff, #f7fafc);" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1.5px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 16px 32px;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-text-fill: #2d3748;" +
                        "-fx-min-width: 110px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);";

        String hoverStyle =
                "-fx-background-color: linear-gradient(to bottom, #10b981, #059669);" +
                        "-fx-border-color: #047857;" +
                        "-fx-text-fill: white;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(16, 185, 129, 0.4), 12, 0, 0, 4);";

        btn.setStyle(baseStyle);

        // Animaciones de hover más refinadas
        btn.setOnMouseEntered(e -> {
            btn.setStyle(baseStyle.substring(0, baseStyle.indexOf("-fx-background-color:")) + hoverStyle);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        btn.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(0.96);
            st.setToY(0.96);
            st.play();
        });

        btn.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });

        btn.setOnAction(e -> collapse());
        return btn;
    }

    private Button createCloseButton() {
        Button btn = new Button("✕ Cerrar");

        String baseStyle =
                "-fx-background-color: linear-gradient(to bottom, #718096, #4a5568);" +
                        "-fx-border-radius: 12px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 16px 32px;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-min-width: 140px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 2);";

        String hoverStyle =
                "-fx-background-color: linear-gradient(to bottom, #e53e3e, #c53030);" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(229, 62, 62, 0.4), 12, 0, 0, 4);";

        btn.setStyle(baseStyle);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(baseStyle.substring(0, baseStyle.indexOf("-fx-background-color:")) + hoverStyle);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        btn.setOnAction(e -> collapse());
        return btn;
    }

    private void animateContentIn() {
        // Añadir sombra más dramática al diálogo expandido
        DropShadow dialogShadow = new DropShadow();
        dialogShadow.setRadius(40);
        dialogShadow.setOffsetY(20);
        dialogShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        overlay.setEffect(dialogShadow);

        // Animación de entrada del contenido más suave
        FadeTransition fadeContent = new FadeTransition(Duration.millis(500), content);
        fadeContent.setFromValue(0);
        fadeContent.setToValue(1);

        ScaleTransition scaleContent = new ScaleTransition(Duration.millis(500), content);
        scaleContent.setFromX(0.7);
        scaleContent.setFromY(0.7);
        scaleContent.setToX(1.0);
        scaleContent.setToY(1.0);
        scaleContent.setInterpolator(Interpolator.SPLINE(0.34, 1.56, 0.64, 1));

        TranslateTransition slideContent = new TranslateTransition(Duration.millis(500), content);
        slideContent.setFromY(30);
        slideContent.setToY(0);

        ParallelTransition contentAnimation = new ParallelTransition(
                fadeContent, scaleContent, slideContent
        );

        contentAnimation.setDelay(Duration.millis(200));
        contentAnimation.setOnFinished(e -> expanded = true);
        contentAnimation.play();
    }

    public void collapse() {
        if (!expanded) return;

        expanded = false;

        // Animación de salida del contenido
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), content);
        fadeOut.setToValue(0);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(250), content);
        scaleOut.setToX(0.8);
        scaleOut.setToY(0.8);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(250), content);
        slideOut.setToY(-20);

        ParallelTransition contentOut = new ParallelTransition(fadeOut, scaleOut, slideOut);
        contentOut.setOnFinished(e -> playCollapseAnimations());
        contentOut.play();
    }

    private void playCollapseAnimations() {
        // Interpolador suave para el colapso
        Interpolator easeInOut = Interpolator.SPLINE(0.42, 0, 0.58, 1);

        // Animación de transformación inversa
        Timeline collapseTransform = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(overlay.layoutXProperty(), overlay.getLayoutX()),
                        new KeyValue(overlay.layoutYProperty(), overlay.getLayoutY()),
                        new KeyValue(overlay.prefWidthProperty(), overlay.getWidth()),
                        new KeyValue(overlay.prefHeightProperty(), overlay.getHeight()),
                        new KeyValue(clip.widthProperty(), clip.getWidth()),
                        new KeyValue(clip.heightProperty(), clip.getHeight()),
                        new KeyValue(clip.arcWidthProperty(), clip.getArcWidth()),
                        new KeyValue(clip.arcHeightProperty(), clip.getArcHeight())
                ),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(overlay.layoutXProperty(), origCenterX - origW / 2, easeInOut),
                        new KeyValue(overlay.layoutYProperty(), origCenterY - origH / 2, easeInOut),
                        new KeyValue(overlay.prefWidthProperty(), origW, easeInOut),
                        new KeyValue(overlay.prefHeightProperty(), origH, easeInOut),
                        new KeyValue(clip.widthProperty(), origW, easeInOut),
                        new KeyValue(clip.heightProperty(), origH, easeInOut),
                        new KeyValue(clip.arcWidthProperty(), 12, easeInOut),
                        new KeyValue(clip.arcHeightProperty(), 12, easeInOut)
                )
        );

        // Cambio de color de vuelta al botón original
        Timeline colorRevert = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(overlay.styleProperty(), overlay.getStyle())),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(overlay.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom, " +
                                        toRgbString(buttonColor.brighter()) + ", " +
                                        toRgbString(buttonColor) + ");", easeInOut))
        );

        // Desvanecimiento del fondo
        FadeTransition backdropFadeOut = new FadeTransition(Duration.millis(500), backdrop);
        backdropFadeOut.setToValue(0);

        Timeline backdropColorRevert = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(backdrop.styleProperty(), backdrop.getStyle())),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(backdrop.styleProperty(), "-fx-background-color: rgba(0,0,0,0);"))
        );

        ParallelTransition collapseTransition = new ParallelTransition(
                collapseTransform, colorRevert, backdropFadeOut, backdropColorRevert
        );

        collapseTransition.setOnFinished(e -> cleanup());
        collapseTransition.play();
    }

    private void cleanup() {
        rootPane.getChildren().removeAll(backdrop, overlay);
        overlay.setClip(null);
        trigger.setVisible(true);
    }
}