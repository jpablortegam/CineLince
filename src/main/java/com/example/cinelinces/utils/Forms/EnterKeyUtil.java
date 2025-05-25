package com.example.cinelinces.utils.Forms;

/*
 * Permite disparar acciones al presionar ENTER.
 */

import javafx.scene.control.TextInputControl;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class EnterKeyUtil {
    public static void register(TextInputControl field, Button button, Runnable action) {
        field.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER && !button.isDisabled()) {
                action.run();
            }
        });
    }
}
