package com.example.cinelinces.utils.Animations;

import javafx.animation.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/*
 * Encapsula la animación de transición entre pestañas.
 */
public class TabAnimationHelper {
    private static final Duration OUT_DUR = Duration.millis(200);
    private static final Duration IN_DUR  = Duration.millis(300);

    public static void animate(TabPane tabPane, Tab oldTab, Tab newTab) {
        VBox oldContent = (VBox) oldTab.getContent();
        VBox newContent = (VBox) newTab.getContent();
        boolean isRight = tabPane.getTabs().indexOf(newTab) >
                tabPane.getTabs().indexOf(oldTab);

        // Fade + slide out (old)
        FadeTransition fadeOut = new FadeTransition(OUT_DUR, oldContent);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        TranslateTransition slideOut = new TranslateTransition(OUT_DUR, oldContent);
        slideOut.setFromX(0); slideOut.setToX(isRight ? -30 : 30);

        // Prepara el nuevo contenido
        newContent.setOpacity(0);
        newContent.setTranslateX(isRight ? 30 : -30);

        // Fade + slide in (new)
        FadeTransition fadeIn = new FadeTransition(IN_DUR, newContent);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);
        TranslateTransition slideIn = new TranslateTransition(IN_DUR, newContent);
        slideIn.setFromX(isRight ? 30 : -30); slideIn.setToX(0);

        ParallelTransition out = new ParallelTransition(fadeOut, slideOut);
        ParallelTransition in  = new ParallelTransition(fadeIn, slideIn);

        SequentialTransition seq = new SequentialTransition(out, in);
        seq.setInterpolator(Interpolator.EASE_BOTH);
        seq.play();
    }
}
