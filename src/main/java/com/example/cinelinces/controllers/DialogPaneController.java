package com.example.cinelinces.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Controlador vacío para inyección de IDs del diálogo.
 */
public class DialogPaneController {
    @FXML private VBox dialogPanel;
    @FXML private Button closeBtn;

    public VBox getDialogPanel() { return dialogPanel; }
    public Button getCloseBtn()     { return closeBtn;     }
}