package com.example.cinelinces.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SideBarViewController {
    @FXML
    public VBox sidebar;
    @FXML
    private Button btnHome, btnPopulares, btnMiCuenta;

    private MainViewController mainController;

    public void setMainController(MainViewController main) {
        this.mainController = main;
        highlightButton(btnHome);
    }

    @FXML
    private void showHome() {
        mainController.showHome();
        highlightButton(btnHome);
    }

    @FXML
    private void showPopular() {
        mainController.showPopular();
        highlightButton(btnPopulares);
    }

    @FXML
    private void showAccount() {
        mainController.showAccount();
        highlightButton(btnMiCuenta);
    }

    private void highlightButton(Button activa) {
        for (Button b : new Button[]{btnHome, btnPopulares, btnMiCuenta}) {
            b.getStyleClass().remove("active");
        }
        activa.getStyleClass().add("active");
    }
}
