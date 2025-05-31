package com.example.cinelinces.utils.Animations;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class TransitionFactory {
    public static TranslateTransition translate(Node node, Duration dur, double toX, double toY, Interpolator interp) {
        TranslateTransition tt = new TranslateTransition(dur, node);
        tt.setToX(toX);
        tt.setToY(toY);
        tt.setInterpolator(interp);
        return tt;
    }

    public static ScaleTransition scale(Node node, Duration dur, double fromX, double fromY, double toX, double toY, Interpolator interp) {
        ScaleTransition st = new ScaleTransition(dur, node);
        st.setFromX(fromX);
        st.setFromY(fromY);
        st.setToX(toX);
        st.setToY(toY);
        st.setInterpolator(interp);
        return st;
    }

    public static FadeTransition fade(Node node, Duration dur, double from, double to, Duration delay, Interpolator interp) {
        FadeTransition ft = new FadeTransition(dur, node);
        ft.setFromValue(from);
        ft.setToValue(to);
        if (delay != null) {
            ft.setDelay(delay);
        }
        ft.setInterpolator(interp);
        return ft;
    }

    public static ScaleTransition scale(Node node, Duration duration, double toX, double toY, Interpolator interpolator) {
        ScaleTransition st = new ScaleTransition(duration, node);
        st.setToX(toX);
        st.setToY(toY);
        st.setInterpolator(interpolator);
        return st;
    }
}