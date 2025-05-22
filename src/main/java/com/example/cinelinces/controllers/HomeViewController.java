package com.example.cinelinces.controllers;

import com.example.cinelinces.utils.ButtonHoverAnimator;
import com.example.cinelinces.utils.DialogAnimationHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeViewController implements Initializable {
    @FXML private StackPane rootStack;
    @FXML private Pane overlayPane;
    @FXML private Button btnVerHorarios;
    @FXML private FlowPane upcomingPane;

    private DialogAnimationHelper dialogHelper;
    private Node dialogPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar helpers
        dialogHelper = new DialogAnimationHelper(rootStack, overlayPane);
        ButtonHoverAnimator.applyHoverEffect(btnVerHorarios);

        // Cargar diálogo FXML y obtener el VBox como panel a animar
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/dialogPane.fxml")
            );
            AnchorPane root = loader.load();
            DialogPaneController dlgCtrl = loader.getController();
            VBox panel = dlgCtrl.getDialogPanel();
            dialogPanel = panel;
            // oculto al inicio
            panel.setVisible(false);
            panel.setOpacity(0);
            overlayPane.getChildren().add(panel);

            // configurar botón de cierre
            Button closeBtn = dlgCtrl.getCloseBtn();
            ButtonHoverAnimator.applyHoverEffect(closeBtn);
            closeBtn.setOnAction(e -> dialogHelper.hideDialog(panel, btnVerHorarios));
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar dialogPane.fxml", e);
        }

        // acción de mostrar diálogo animado
        btnVerHorarios.setOnAction(e -> dialogHelper.showDialog(dialogPanel, btnVerHorarios));

        // --- Carga de tarjetas omitida por brevedad ---
    }
}