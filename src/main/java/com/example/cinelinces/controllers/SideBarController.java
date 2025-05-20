package com.example.cinelinces.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SideBarController {
    public VBox sidebar;
    @FXML private Button btnEstrenos, btnProximamente, btnPopulares, btnMiCuenta;

    // Referencia al controlador principal
    private HelloController mainController;



    // Metodo para resaltar el botón inicial
    private void highlightInitialButton() {
        if (btnEstrenos != null && !btnEstrenos.getStyleClass().contains("active")) {
            btnEstrenos.getStyleClass().add("active");
        }
    }

    @FXML
    private void showHome() {
        if (mainController != null) {
            mainController.showHome();
            highlightButton(btnEstrenos);
        }
    }

    @FXML
    private void showUpcoming() {
        if (mainController != null) {
            mainController.showUpcoming();
            highlightButton(btnProximamente);
        }
    }

    @FXML
    private void showPopular() {
        if (mainController != null) {
            mainController.showPopular();
            highlightButton(btnPopulares);
        }
    }

    @FXML
    private void showAccount() {
        if (mainController != null) {
            mainController.showAccount();
            highlightButton(btnMiCuenta);
        }
    }

    // Método para resaltar el botón activo y quitar "active" del resto
    private void highlightButton(Button active) {
        Button[] all = { btnEstrenos, btnProximamente, btnPopulares, btnMiCuenta };
        for (Button b : all) {
            if (b != null) {
                b.getStyleClass().remove("active");
            }
        }
        if (active != null && !active.getStyleClass().contains("active")) {
            active.getStyleClass().add("active");
        }
    }
}