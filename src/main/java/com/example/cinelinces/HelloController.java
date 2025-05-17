package com.example.cinelinces;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("¡Hola desde JavaFX 25!");
    }

    @FXML
    protected void onAnimatedButtonClick() {
        welcomeText.setText("¡El botón animado funciona!");
    }
}