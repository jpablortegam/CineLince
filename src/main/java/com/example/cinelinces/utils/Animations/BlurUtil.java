package com.example.cinelinces.utils.Animations;

/*
 * Utilidad para crear animaciones de blur sobre nodos.
 */

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

public class BlurUtil {
    public static Timeline blurIn(Node node, double fromRadius, double toRadius, Duration duration, Interpolator interp) {
        GaussianBlur blur = new GaussianBlur(fromRadius);
        node.setEffect(blur);
        KeyValue kv = new KeyValue(blur.radiusProperty(), toRadius, interp);
        KeyFrame kf = new KeyFrame(duration, kv);
        return new Timeline(kf);
    }

    public static Timeline blurOut(Node node, double fromRadius, double toRadius, Duration duration, Interpolator interp) {
        GaussianBlur blur = (GaussianBlur) node.getEffect();
        if (blur == null) {
            blur = new GaussianBlur(fromRadius);
            node.setEffect(blur);
        }
        KeyValue kv = new KeyValue(blur.radiusProperty(), toRadius, interp);
        KeyFrame kf = new KeyFrame(duration, kv);
        return new Timeline(kf);
    }
}