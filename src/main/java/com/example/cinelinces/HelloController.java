package com.example.cinelinces;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
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
    private double origW, origH, origCenterX, origCenterY;
    private Pane backdrop;
    private StackPane overlayPane;
    private Rectangle clip;
    private VBox content;

    // Colores del tema con gradientes
    private final Color BUTTON_COLOR = Color.web("#10b981"); // Verde esmeralda
    private final Color DIALOG_COLOR = Color.WHITE;
    private final Color DIALOG_BORDER_COLOR = Color.web("#e5e7eb"); // Gris claro para bordes

    // Duración de animaciones más fluidas
    private final Duration FAST_DURATION = Duration.millis(250);
    private final Duration MEDIUM_DURATION = Duration.millis(400);
    private final Duration SLOW_DURATION = Duration.millis(600); // Para la animación principal de morphing

    @FXML
    public void onVerHorarios(ActionEvent e) {
        if (!isExpanded) {
            expandToDialog();
        } else {
            collapseToButton();
        }
    }

    private void expandToDialog() {
        if (isExpanded) return; // Prevenir expansiones múltiples

        // 1) Capturar bounds del botón y calcular centro exacto
        Bounds btnBounds = btnVerHorarios.getBoundsInParent();
        origCenterX = btnBounds.getMinX() + btnBounds.getWidth() / 2;
        origCenterY = btnBounds.getMinY() + btnBounds.getHeight() / 2;
        origW = btnBounds.getWidth();
        origH = btnBounds.getHeight();

        // 2) Calcular dimensiones finales
        double targetW = Math.min(rootPane.getWidth() * 0.82, 450);
        double targetH = Math.min(rootPane.getHeight() * 0.82, 620);
        double targetCenterX = rootPane.getWidth() / 2;
        double targetCenterY = rootPane.getHeight() / 2;

        // 3) Backdrop con efecto de desenfoque
        backdrop = new Pane();
        backdrop.setStyle("-fx-background-color: rgba(0,0,0,0);"); // Inicialmente transparente
        backdrop.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        backdrop.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        backdrop.setOnMouseClicked(evt -> collapseToButton()); // Cierra al hacer clic fuera
        backdrop.setOpacity(0); // Inicialmente invisible

        // 4) OverlayPane - posición inicial exacta del botón
        overlayPane = new StackPane();
        overlayPane.setLayoutX(origCenterX - origW / 2);
        overlayPane.setLayoutY(origCenterY - origH / 2);
        overlayPane.setPrefSize(origW, origH);
        overlayPane.setMaxSize(origW, origH); // Limita el tamaño máximo al inicial para evitar que se redimensione antes de la animación

        // 5) Background inicial igual al botón con gradiente sutil
        BackgroundFill initialFill = new BackgroundFill(
                BUTTON_COLOR,
                new CornerRadii(8),
                Insets.EMPTY
        );
        overlayPane.setBackground(new Background(initialFill));

        // 6) Shadow inicial más sutil
        DropShadow initialShadow = new DropShadow();
        initialShadow.setRadius(6);
        initialShadow.setOffsetY(3);
        initialShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        overlayPane.setEffect(initialShadow);

        // 7) Clip para bordes redondeados con animación
        clip = new Rectangle(origW, origH);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        overlayPane.setClip(clip);

        // 8) Contenido del diálogo
        content = createDialogContent();
        content.setOpacity(0); // Inicialmente invisible
        // No pre-escalar o trasladar aquí, la animación de contenido se encarga de eso

        overlayPane.getChildren().add(content);
        StackPane.setAlignment(content, Pos.CENTER); // Centra el contenido dentro del overlay

        // 9) Añadir al rootPane con efecto de blur en el fondo
        addBlurEffect();
        rootPane.getChildren().addAll(backdrop, overlayPane);
        btnVerHorarios.setVisible(false); // Ocultar el botón original

        // 10) Secuencia de animaciones mejorada
        animateExpansionImproved(targetW, targetH, targetCenterX, targetCenterY);
    }

    private void addBlurEffect() {
        GaussianBlur blur = new GaussianBlur(0);
        Timeline blurAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(blur.radiusProperty(), 0)),
                new KeyFrame(MEDIUM_DURATION, new KeyValue(blur.radiusProperty(), 8, Interpolator.EASE_OUT))
        );

        // Aplica el blur a todos los nodos del rootPane excepto los que acabamos de añadir
        rootPane.getChildren().forEach(node -> {
            if (node != backdrop && node != overlayPane) {
                node.setEffect(blur);
            }
        });

        blurAnimation.play();
    }

    private void animateExpansionImproved(double targetW, double targetH, double targetCenterX, double targetCenterY) {
        // Interpolador más suave y natural (ease-out cubic bezier)
        Interpolator smoothBezier = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);

        // 1) Backdrop con fade in suave
        Timeline backdropAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(backdrop.opacityProperty(), 0)),
                new KeyFrame(MEDIUM_DURATION,
                        new KeyValue(backdrop.opacityProperty(), 1, Interpolator.EASE_OUT))
        );
        backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.65);"); // Establecer color final

        // 2) Morphing mejorado con scaling natural (overshoot inicial)
        Timeline morphingAnimation = new Timeline();
        morphingAnimation.getKeyFrames().addAll(
                // Estado inicial (ya establecido)
                // Estado intermedio para efecto más orgánico (overshoot)
                new KeyFrame(SLOW_DURATION.multiply(0.6), // Alcanza el overshoot al 60%
                        new KeyValue(overlayPane.scaleXProperty(), 1.08, smoothBezier),
                        new KeyValue(overlayPane.scaleYProperty(), 1.08, smoothBezier),
                        new KeyValue(clip.arcWidthProperty(), 20.0, smoothBezier), // Ajuste sutil de bordes
                        new KeyValue(clip.arcHeightProperty(), 20.0, smoothBezier)
                ),
                // Estado final
                new KeyFrame(SLOW_DURATION,
                        new KeyValue(overlayPane.layoutXProperty(), targetCenterX - targetW / 2, smoothBezier),
                        new KeyValue(overlayPane.layoutYProperty(), targetCenterY - targetH / 2, smoothBezier),
                        new KeyValue(overlayPane.prefWidthProperty(), targetW, smoothBezier),
                        new KeyValue(overlayPane.prefHeightProperty(), targetH, smoothBezier),
                        new KeyValue(overlayPane.maxWidthProperty(), targetW, smoothBezier),
                        new KeyValue(overlayPane.maxHeightProperty(), targetH, smoothBezier),
                        new KeyValue(overlayPane.scaleXProperty(), 1.0, smoothBezier),
                        new KeyValue(overlayPane.scaleYProperty(), 1.0, smoothBezier),
                        new KeyValue(clip.widthProperty(), targetW, smoothBezier),
                        new KeyValue(clip.heightProperty(), targetH, smoothBezier),
                        new KeyValue(clip.arcWidthProperty(), 28.0, smoothBezier), // Bordes más grandes
                        new KeyValue(clip.arcHeightProperty(), 28.0, smoothBezier)
                )
        );

        // 3) Transición de color más gradual con múltiples pasos
        Timeline colorTransition = new Timeline();
        colorTransition.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(overlayPane.backgroundProperty(),
                                new Background(new BackgroundFill(BUTTON_COLOR, new CornerRadii(8), Insets.EMPTY)))
                ),
                new KeyFrame(SLOW_DURATION.multiply(0.3), // Color intermedio
                        new KeyValue(overlayPane.backgroundProperty(),
                                new Background(new BackgroundFill(Color.web("#0891b2"), new CornerRadii(12), Insets.EMPTY)),
                                Interpolator.EASE_BOTH)
                ),
                new KeyFrame(SLOW_DURATION, // Color final del diálogo
                        new KeyValue(overlayPane.backgroundProperty(),
                                new Background(new BackgroundFill(DIALOG_COLOR, new CornerRadii(28), Insets.EMPTY)), // Misma curvatura que el clip
                                Interpolator.EASE_OUT)
                )
        );

        // 4) Shadow con evolución gradual
        Timeline shadowAnimation = new Timeline();
        DropShadow initialShadow = (DropShadow) overlayPane.getEffect();
        shadowAnimation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(initialShadow.radiusProperty(), 6),
                        new KeyValue(initialShadow.offsetYProperty(), 3),
                        new KeyValue(initialShadow.colorProperty(), Color.rgb(0, 0, 0, 0.15))
                ),
                new KeyFrame(SLOW_DURATION,
                        new KeyValue(initialShadow.radiusProperty(), 35, smoothBezier),
                        new KeyValue(initialShadow.offsetYProperty(), 18, smoothBezier),
                        new KeyValue(initialShadow.colorProperty(), Color.rgb(0, 0, 0, 0.28), smoothBezier)
                )
        );

        // Configurar callback para animar contenido después de la expansión principal
        morphingAnimation.setOnFinished(e -> animateContentInImproved());

        // Ejecutar todas las animaciones en paralelo
        ParallelTransition masterAnimation = new ParallelTransition(
                backdropAnimation, morphingAnimation, colorTransition, shadowAnimation
        );
        masterAnimation.play();
    }

    private VBox createDialogContent() {
        VBox container = new VBox(28); // Espacio entre elementos
        container.setPadding(new Insets(36));
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxWidth(Region.USE_PREF_SIZE);

        // Título con estilo mejorado
        Label title = new Label("Horarios Disponibles");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#111827")); // Gris oscuro casi negro
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 1, 0, 0, 1);"); // Sombra de texto suave

        // Subtítulo con mejor spacing
        Label subtitle = new Label("Dune: Parte Dos");
        subtitle.setFont(Font.font("System", FontWeight.MEDIUM, 17));
        subtitle.setTextFill(Color.web("#6b7280")); // Gris medio

        // Separador visual sutil
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: linear-gradient(to right, transparent, " + DIALOG_BORDER_COLOR.toString().replace("0x", "#") + ", transparent);");
        separator.setMaxWidth(200);

        // Grid de horarios con mejor diseño
        GridPane horariosGrid = new GridPane();
        horariosGrid.setAlignment(Pos.CENTER);
        horariosGrid.setHgap(18); // Espacio horizontal
        horariosGrid.setVgap(14); // Espacio vertical

        String[] horarios = {
                "10:00 AM", "1:30 PM", "4:00 PM",
                "6:30 PM", "9:00 PM", "11:30 PM"
        };

        for (int i = 0; i < horarios.length; i++) {
            Button btnHorario = createHorarioButtonImproved(horarios[i]);
            horariosGrid.add(btnHorario, i % 3, i / 3); // 3 columnas
        }

        // Botón cerrar mejorado
        Button closeButton = createCloseButtonImproved();

        container.getChildren().addAll(title, subtitle, separator, horariosGrid, closeButton);
        return container;
    }

    private Button createHorarioButtonImproved(String horario) {
        Button btnHorario = new Button(horario);

        // Estilos con gradiente sutil y sombra inicial
        btnHorario.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ffffff, #f8fafc);" + // Blanco a blanco muy claro
                        "-fx-border-color: #e2e8f0;" + // Gris muy claro
                        "-fx-border-width: 1.5px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 14px 28px;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-text-fill: #334155;" + // Gris azulado oscuro
                        "-fx-min-width: 110px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 2);" // Sombra suave
        );

        // Animaciones de hover más suaves
        btnHorario.setOnMouseEntered(e -> {
            btnHorario.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #10b981, #059669);" + // Verde a verde más oscuro
                            "-fx-border-color: #10b981;" +
                            "-fx-border-width: 1.5px;" +
                            "-fx-border-radius: 12px;" +
                            "-fx-background-radius: 12px;" +
                            "-fx-padding: 14px 28px;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: 600;" +
                            "-fx-text-fill: #ffffff;" +
                            "-fx-min-width: 110px;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(16,185,129,0.4), 8, 0, 0, 4);" // Sombra colorida
            );
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), btnHorario);
            scaleUp.setToX(1.05);
            scaleUp.setToY(1.05);
            scaleUp.setInterpolator(Interpolator.EASE_OUT);
            scaleUp.play();
        });

        btnHorario.setOnMouseExited(e -> {
            btnHorario.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #ffffff, #f8fafc);" +
                            "-fx-border-color: #e2e8f0;" +
                            "-fx-border-width: 1.5px;" +
                            "-fx-border-radius: 12px;" +
                            "-fx-background-radius: 12px;" +
                            "-fx-padding: 14px 28px;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: 600;" +
                            "-fx-text-fill: #334155;" +
                            "-fx-min-width: 110px;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 2);"
            );
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), btnHorario);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.setInterpolator(Interpolator.EASE_OUT);
            scaleDown.play();
        });

        btnHorario.setOnMousePressed(e -> {
            ScaleTransition scalePress = new ScaleTransition(Duration.millis(80), btnHorario);
            scalePress.setToX(0.96);
            scalePress.setToY(0.96);
            scalePress.setInterpolator(Interpolator.EASE_IN);
            scalePress.play();
        });

        btnHorario.setOnMouseReleased(e -> {
            // Regresa al estado de hover si el ratón todavía está sobre el botón, de lo contrario a normal
            if (btnHorario.isHover()) {
                ScaleTransition scaleRelease = new ScaleTransition(Duration.millis(150), btnHorario);
                scaleRelease.setToX(1.05);
                scaleRelease.setToY(1.05);
                scaleRelease.setInterpolator(Interpolator.EASE_OUT);
                scaleRelease.play();
            } else {
                ScaleTransition scaleRelease = new ScaleTransition(Duration.millis(150), btnHorario);
                scaleRelease.setToX(1.0);
                scaleRelease.setToY(1.0);
                scaleRelease.setInterpolator(Interpolator.EASE_OUT);
                scaleRelease.play();
            }
        });

        btnHorario.setOnAction(e -> {
            System.out.println("Horario seleccionado: " + horario);

            // Efecto de confirmación más elegante
            ParallelTransition confirmation = new ParallelTransition();

            // Flash suave
            FadeTransition flash = new FadeTransition(Duration.millis(120), btnHorario);
            flash.setFromValue(1.0);
            flash.setToValue(0.6);
            flash.setCycleCount(2);
            flash.setAutoReverse(true);

            // Scale de confirmación
            ScaleTransition confirmScale = new ScaleTransition(Duration.millis(240), btnHorario);
            confirmScale.setToX(1.1);
            confirmScale.setToY(1.1);
            confirmScale.setAutoReverse(true);
            confirmScale.setCycleCount(2);

            confirmation.getChildren().addAll(flash, confirmScale);
            confirmation.setOnFinished(event -> collapseToButton()); // Cierra después de la confirmación
            confirmation.play();
        });

        return btnHorario;
    }

    private Button createCloseButtonImproved() {
        Button closeButton = new Button("Cerrar");
        closeButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #6b7280, #4b5563);" + // Gris a gris oscuro
                        "-fx-border-radius: 12px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 14px 28px;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-min-width: 130px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);"
        );

        // Hover y Pressed
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #4b5563, #374151);" +
                        "-fx-border-radius: 12px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 14px 28px;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-min-width: 130px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 4);"
        ));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #6b7280, #4b5563);" +
                        "-fx-border-radius: 12px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 14px 28px;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-min-width: 130px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);"
        ));
        closeButton.setOnMousePressed(e -> {
            ScaleTransition scalePress = new ScaleTransition(Duration.millis(80), closeButton);
            scalePress.setToX(0.96);
            scalePress.setToY(0.96);
            scalePress.setInterpolator(Interpolator.EASE_IN);
            scalePress.play();
        });
        closeButton.setOnMouseReleased(e -> {
            if (closeButton.isHover()) {
                ScaleTransition scaleRelease = new ScaleTransition(Duration.millis(150), closeButton);
                scaleRelease.setToX(1.0); // No scaling up on release if still hovering
                scaleRelease.setToY(1.0);
                scaleRelease.setInterpolator(Interpolator.EASE_OUT);
                scaleRelease.play();
            } else {
                ScaleTransition scaleRelease = new ScaleTransition(Duration.millis(150), closeButton);
                scaleRelease.setToX(1.0);
                scaleRelease.setToY(1.0);
                scaleRelease.setInterpolator(Interpolator.EASE_OUT);
                scaleRelease.play();
            }
        });

        closeButton.setOnAction(e -> collapseToButton());
        return closeButton;
    }

    private void animateContentInImproved() {
        VBox container = content;
        ParallelTransition allContentIn = new ParallelTransition();

        for (int i = 0; i < container.getChildren().size(); i++) {
            var child = container.getChildren().get(i);
            child.setOpacity(0);
            child.setTranslateY(30); // Desplazamiento inicial hacia abajo
            child.setScaleX(0.9);
            child.setScaleY(0.9);

            // Delay escalonado para efecto cascada
            Duration delay = Duration.millis(i * 70); // Ajustado para ser un poco más rápido

            FadeTransition fade = new FadeTransition(Duration.millis(350), child); // Más rápido
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition slide = new TranslateTransition(Duration.millis(400), child); // Más rápido
            slide.setFromY(30);
            slide.setToY(0);
            slide.setInterpolator(Interpolator.SPLINE(0.16, 1, 0.3, 1)); // Bounce sutil al final

            ScaleTransition scale = new ScaleTransition(Duration.millis(400), child); // Más rápido
            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(Interpolator.EASE_OUT);

            ParallelTransition childAnimation = new ParallelTransition(fade, slide, scale);
            childAnimation.setDelay(delay);
            allContentIn.getChildren().add(childAnimation);
        }

        allContentIn.setOnFinished(e -> isExpanded = true); // Marcar como expandido al finalizar todas las animaciones
        allContentIn.play();
    }

    private void collapseToButton() {
        if (!isExpanded) return; // Prevenir colapsos múltiples o si no está expandido
        isExpanded = false; // Marcar como no expandido inmediatamente para evitar re-clics

        // Animación de salida del contenido en cascada inversa
        VBox container = content;
        ParallelTransition allContentOut = new ParallelTransition();

        for (int i = 0; i < container.getChildren().size(); i++) {
            var child = container.getChildren().get(i);
            // Invertir el orden para la cascada inversa
            Duration delay = Duration.millis((container.getChildren().size() - 1 - i) * 30); // Menor delay para salida más rápida

            FadeTransition fade = new FadeTransition(Duration.millis(150), child); // Más rápido
            fade.setToValue(0);
            fade.setInterpolator(Interpolator.EASE_IN);

            TranslateTransition slide = new TranslateTransition(Duration.millis(200), child); // Más rápido
            slide.setToY(20); // Se mueve ligeramente hacia abajo
            slide.setInterpolator(Interpolator.EASE_IN);

            ScaleTransition scale = new ScaleTransition(Duration.millis(180), child); // Más rápido
            scale.setToX(0.9);
            scale.setToY(0.9);
            scale.setInterpolator(Interpolator.EASE_IN);

            ParallelTransition childAnimation = new ParallelTransition(fade, slide, scale);
            childAnimation.setDelay(delay);
            allContentOut.getChildren().add(childAnimation);
        }

        allContentOut.setOnFinished(e -> {
            performCollapse(); // Ejecutar el colapso principal después de que el contenido salga
            removeBlurEffect(); // Remover blur después de la animación del contenido
        });
        allContentOut.play();
    }

    private void removeBlurEffect() {
        Timeline blurRemoval = new Timeline();
        // Recorre solo los elementos que tienen blur aplicado
        rootPane.getChildren().forEach(node -> {
            if (node.getEffect() instanceof GaussianBlur blur) {
                blurRemoval.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(blur.radiusProperty(), blur.getRadius())),
                        new KeyFrame(FAST_DURATION, new KeyValue(blur.radiusProperty(), 0, Interpolator.EASE_IN))
                );
            }
        });

        blurRemoval.setOnFinished(e -> {
            // Elimina el efecto de blur completamente
            rootPane.getChildren().forEach(node -> {
                if (node != backdrop && node != overlayPane) { // Asegurarse de no eliminar el blur de los elementos que ya no tienen
                    node.setEffect(null);
                }
            });
        });
        blurRemoval.play();
    }

    private void performCollapse() {
        // Interpolador para el colapso (ease-in cubic bezier)
        Interpolator collapseEasing = Interpolator.SPLINE(0.32, 0, 0.67, 0);

        // Restaurar color y propiedades del botón
        BackgroundFill buttonFill = new BackgroundFill(BUTTON_COLOR, new CornerRadii(8), Insets.EMPTY);

        // Animación de color de vuelta al botón
        Timeline colorRestore = new Timeline(
                new KeyFrame(MEDIUM_DURATION.multiply(0.8), // La mitad del tiempo para que el color cambie antes de la geometría
                        new KeyValue(overlayPane.backgroundProperty(), new Background(buttonFill), Interpolator.EASE_IN)
                )
        );

        Timeline collapseGeometry = new Timeline();
        collapseGeometry.getKeyFrames().addAll(
                // Estado inicial (actual)
                new KeyFrame(Duration.ZERO,
                        new KeyValue(overlayPane.layoutXProperty(), overlayPane.getLayoutX()),
                        new KeyValue(overlayPane.layoutYProperty(), overlayPane.getLayoutY()),
                        new KeyValue(overlayPane.prefWidthProperty(), overlayPane.getPrefWidth()),
                        new KeyValue(overlayPane.prefHeightProperty(), overlayPane.getPrefHeight()),
                        new KeyValue(overlayPane.maxWidthProperty(), overlayPane.getMaxWidth()),
                        new KeyValue(overlayPane.maxHeightProperty(), overlayPane.getMaxHeight()),
                        new KeyValue(overlayPane.scaleXProperty(), 1.0),
                        new KeyValue(overlayPane.scaleYProperty(), 1.0),
                        new KeyValue(clip.widthProperty(), clip.getWidth()),
                        new KeyValue(clip.heightProperty(), clip.getHeight()),
                        new KeyValue(clip.arcWidthProperty(), clip.getArcWidth()),
                        new KeyValue(clip.arcHeightProperty(), clip.getArcHeight())
                ),
                // Overshoot sutil para efecto más natural (se encoge un poco más de lo normal)
                new KeyFrame(MEDIUM_DURATION.multiply(0.7),
                        new KeyValue(overlayPane.scaleXProperty(), 0.95, collapseEasing),
                        new KeyValue(overlayPane.scaleYProperty(), 0.95, collapseEasing)
                ),
                // Estado final (tamaño y posición del botón original)
                new KeyFrame(MEDIUM_DURATION,
                        new KeyValue(overlayPane.layoutXProperty(), origCenterX - origW / 2, collapseEasing),
                        new KeyValue(overlayPane.layoutYProperty(), origCenterY - origH / 2, collapseEasing),
                        new KeyValue(overlayPane.prefWidthProperty(), origW, collapseEasing),
                        new KeyValue(overlayPane.prefHeightProperty(), origH, collapseEasing),
                        new KeyValue(overlayPane.maxWidthProperty(), origW, collapseEasing),
                        new KeyValue(overlayPane.maxHeightProperty(), origH, collapseEasing),
                        new KeyValue(overlayPane.scaleXProperty(), 1.0, collapseEasing),
                        new KeyValue(overlayPane.scaleYProperty(), 1.0, collapseEasing),
                        new KeyValue(clip.widthProperty(), origW, collapseEasing),
                        new KeyValue(clip.heightProperty(), origH, collapseEasing),
                        new KeyValue(clip.arcWidthProperty(), 16.0, collapseEasing),
                        new KeyValue(clip.arcHeightProperty(), 16.0, collapseEasing)
                )
        );

        FadeTransition backdropFadeOut = new FadeTransition(MEDIUM_DURATION.multiply(0.6), backdrop); // Fade out más rápido
        backdropFadeOut.setToValue(0);
        backdropFadeOut.setInterpolator(Interpolator.EASE_IN);

        // Shadow restoration
        Timeline shadowRestore = new Timeline();
        DropShadow currentShadow = (DropShadow) overlayPane.getEffect();
        if (currentShadow != null) { // Asegurarse de que el efecto exista
            shadowRestore.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(currentShadow.radiusProperty(), currentShadow.getRadius()),
                            new KeyValue(currentShadow.offsetYProperty(), currentShadow.getOffsetY()),
                            new KeyValue(currentShadow.colorProperty(), currentShadow.getColor())
                    ),
                    new KeyFrame(MEDIUM_DURATION,
                            new KeyValue(currentShadow.radiusProperty(), 6, collapseEasing),
                            new KeyValue(currentShadow.offsetYProperty(), 3, collapseEasing),
                            new KeyValue(currentShadow.colorProperty(), Color.rgb(0, 0, 0, 0.15), collapseEasing)
                    )
            );
        }

        ParallelTransition masterCollapseAnimation = new ParallelTransition(
                colorRestore, collapseGeometry, backdropFadeOut, shadowRestore
        );

        masterCollapseAnimation.setOnFinished(e -> {
            rootPane.getChildren().removeAll(backdrop, overlayPane);
            btnVerHorarios.setVisible(true);
            // Restablecer el efecto del botón, si es necesario, aunque su estilo se define al crearlo
            if (btnVerHorarios.getEffect() instanceof DropShadow) {
                ((DropShadow) btnVerHorarios.getEffect()).setRadius(6);
                ((DropShadow) btnVerHorarios.getEffect()).setOffsetY(3);
                ((DropShadow) btnVerHorarios.getEffect()).setColor(Color.rgb(0, 0, 0, 0.15));
            }
        });
        masterCollapseAnimation.play();
    }
}