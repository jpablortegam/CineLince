package com.example.cinelinces.utils.Animations;

import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.Node;


public class AnimationUtil {
    public static void shake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
}
