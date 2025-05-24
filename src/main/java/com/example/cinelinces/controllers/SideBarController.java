package com.example.cinelinces.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SideBarController {
    @FXML
    public VBox sidebar;  // tu contenedor
    @FXML private Button btnEstrenos, btnProximamente, btnPopulares, btnMiCuenta;

    private MainController mainController;

    /** Este setter lo llama MainController.initialize() */
    public void setMainController(MainController main) {
        this.mainController = main;

        // marcamos ya el primer botón
        highlightButton(btnEstrenos);
    }

    @FXML private void showHome() {
        mainController.showHome();
        highlightButton(btnEstrenos);
    }
    @FXML private void showUpcoming() {
        mainController.showUpcoming();
        highlightButton(btnProximamente);
    }
    @FXML private void showPopular() {
        mainController.showPopular();
        highlightButton(btnPopulares);
    }
    @FXML private void showAccount() {
        mainController.showAccount();
        highlightButton(btnMiCuenta);
    }

    private void highlightButton(Button activa) {
        for (Button b : new Button[]{btnEstrenos, btnProximamente, btnPopulares, btnMiCuenta}) {
            b.getStyleClass().remove("active");
        }
        activa.getStyleClass().add("active");
    }
}
