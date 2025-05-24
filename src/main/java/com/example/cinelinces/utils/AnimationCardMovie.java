package com.example.cinelinces.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationCardMovie {

    private static final Duration ANIMATION_DURATION = Duration.millis(400);
    private static final Duration STAGGER_DELAY = Duration.millis(50);

    // Animación de expansión con efecto blendy suave
    public static void expand(Node cardNode, Node detailsPane) {
        // Configurar el panel de detalles
        setupDetailsPane(detailsPane);

        // 1. Animación de escala con efecto elástico suave
        ScaleTransition scaleUp = new ScaleTransition(ANIMATION_DURATION, cardNode);
        scaleUp.setToX(1.15);
        scaleUp.setToY(1.15);
        scaleUp.setInterpolator(createSmoothInterpolator());

        // 2. Elevación sutil con efecto flotante
        TranslateTransition floatTransition = new TranslateTransition(ANIMATION_DURATION, cardNode);
        floatTransition.setByY(-15);
        floatTransition.setInterpolator(Interpolator.EASE_OUT);

        // 3. Efecto de sombra para dar profundidad
        Timeline shadowEffect = createShadowAnimation(cardNode, true);

        // 4. Aparición escalonada del contenido de detalles
        Timeline detailsReveal = createStaggeredReveal(detailsPane);

        // 5. Animación del contenedor de detalles
        FadeTransition detailsFade = new FadeTransition(Duration.millis(300), detailsPane);
        detailsFade.setFromValue(0.0);
        detailsFade.setToValue(1.0);
        detailsFade.setDelay(Duration.millis(100)); // Pequeño retraso para efecto secuencial
        detailsFade.setInterpolator(Interpolator.EASE_OUT);

        // Ejecutar todas las animaciones
        ParallelTransition mainAnimation = new ParallelTransition(scaleUp, floatTransition);

        SequentialTransition fullAnimation = new SequentialTransition(
                mainAnimation,
                new ParallelTransition(detailsFade, detailsReveal)
        );

        shadowEffect.play();
        fullAnimation.play();

        // Auto-colapso después de 4 segundos (reducido para mejor UX)
        fullAnimation.setOnFinished(event -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(4));
            pause.setOnFinished(e -> collapse(cardNode, detailsPane));
            pause.play();
        });
    }

    // Animación de contracción suave
    public static void collapse(Node cardNode, Node detailsPane) {
        // 1. Desvanecimiento rápido de detalles
        FadeTransition detailsFadeOut = new FadeTransition(Duration.millis(200), detailsPane);
        detailsFadeOut.setFromValue(1.0);
        detailsFadeOut.setToValue(0.0);
        detailsFadeOut.setInterpolator(Interpolator.EASE_IN);

        // 2. Contracción suave de la tarjeta
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(350), cardNode);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        scaleDown.setInterpolator(createSmoothInterpolator());

        // 3. Regreso a posición original
        TranslateTransition settle = new TranslateTransition(Duration.millis(350), cardNode);
        settle.setByY(15);
        settle.setInterpolator(Interpolator.EASE_OUT);

        // 4. Remover efecto de sombra
        Timeline shadowRemove = createShadowAnimation(cardNode, false);

        // Secuencia de animaciones
        ParallelTransition cardReturn = new ParallelTransition(scaleDown, settle);

        SequentialTransition collapseSequence = new SequentialTransition(
                detailsFadeOut,
                cardReturn
        );

        shadowRemove.play();
        collapseSequence.play();

        collapseSequence.setOnFinished(event -> {
            detailsPane.setVisible(false);
            detailsPane.setManaged(false);
        });
    }

    // Configurar el panel de detalles para la animación
    private static void setupDetailsPane(Node detailsPane) {
        detailsPane.setVisible(true);
        detailsPane.setManaged(true);
        detailsPane.setOpacity(0.0);

        // Aplicar transformación inicial a los elementos hijos si es un contenedor
        if (detailsPane instanceof javafx.scene.Parent) {
            javafx.scene.Parent parent = (javafx.scene.Parent) detailsPane;
            parent.getChildrenUnmodifiable().forEach(child -> {
                child.setOpacity(0.0);
                child.setTranslateY(10);
            });
        }
    }

    // Crear animación escalonada para revelar detalles
    private static Timeline createStaggeredReveal(Node detailsPane) {
        KeyFrame[] frames = new KeyFrame[0];

        if (detailsPane instanceof javafx.scene.Parent) {
            javafx.scene.Parent parent = (javafx.scene.Parent) detailsPane;
            var children = parent.getChildrenUnmodifiable();
            frames = new KeyFrame[children.size() * 2];

            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                Duration delay = STAGGER_DELAY.multiply(i);

                // Fade in
                frames[i * 2] = new KeyFrame(delay,
                        new KeyValue(child.opacityProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(child.translateYProperty(), 0.0, Interpolator.EASE_OUT)
                );
            }
        }

        return new Timeline(frames);
    }

    // Crear animación de sombra para efecto de profundidad
    private static Timeline createShadowAnimation(Node node, boolean expand) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.0));
        shadow.setRadius(0);
        shadow.setOffsetY(0);

        node.setEffect(shadow);

        KeyValue radiusTarget = expand ?
                new KeyValue(shadow.radiusProperty(), 20.0, Interpolator.EASE_OUT) :
                new KeyValue(shadow.radiusProperty(), 0.0, Interpolator.EASE_IN);

        KeyValue opacityTarget = expand ?
                new KeyValue(shadow.colorProperty(), Color.rgb(0, 0, 0, 0.3), Interpolator.EASE_OUT) :
                new KeyValue(shadow.colorProperty(), Color.rgb(0, 0, 0, 0.0), Interpolator.EASE_IN);

        KeyValue offsetTarget = expand ?
                new KeyValue(shadow.offsetYProperty(), 8.0, Interpolator.EASE_OUT) :
                new KeyValue(shadow.offsetYProperty(), 0.0, Interpolator.EASE_IN);

        return new Timeline(
                new KeyFrame(ANIMATION_DURATION, radiusTarget, opacityTarget, offsetTarget)
        );
    }

    // Interpolador personalizado para transiciones más suaves
    private static Interpolator createSmoothInterpolator() {
        // Curva de easing personalizada similar a cubic-bezier(0.25, 0.46, 0.45, 0.94)
        return Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);
    }

    // Método adicional para hover effect sutil
    public static void hoverEffect(Node cardNode, boolean entering) {
        ScaleTransition hover = new ScaleTransition(Duration.millis(200), cardNode);
        if (entering) {
            hover.setToX(1.05);
            hover.setToY(1.05);
        } else {
            hover.setToX(1.0);
            hover.setToY(1.0);
        }
        hover.setInterpolator(Interpolator.EASE_OUT);
        hover.play();
    }
}