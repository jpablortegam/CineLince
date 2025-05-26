package com.example.cinelinces.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML private BorderPane rootPane;
    @FXML private BorderPane contentArea;

    private final Map<String, Parent> viewCache = new HashMap<>();
    private String currentViewPath = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showHome();
    }

    public void showHome()       { loadView("/com/example/cinelinces/home-view.fxml"); }
    public void showPopular()    { loadView("/com/example/cinelinces/popular-view.fxml"); }
    public void showAccount()    { loadView("/com/example/cinelinces/login-view.fxml"); }

    private void loadView(String fxmlPath) {
        if (fxmlPath.equals(currentViewPath) && contentArea.getCenter() != null) {
            return;
        }
        try {
            Parent viewNode;
            if (viewCache.containsKey(fxmlPath)) {
                viewNode = viewCache.get(fxmlPath);
            } else {

                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
                viewNode = loader.load();
                viewCache.put(fxmlPath, viewNode);
                // Opcional: guardar el controlador para interacciones futuras si se recarga desde caché
                // viewNode.getProperties().put("controller", loader.getController());
            }
            contentArea.setCenter(viewNode);
            currentViewPath = fxmlPath;
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + fxmlPath);
            e.printStackTrace();
            // Considera mostrar un mensaje de error al usuario en la UI
        }
    }
    public BorderPane getRootPane() { return rootPane; }
}