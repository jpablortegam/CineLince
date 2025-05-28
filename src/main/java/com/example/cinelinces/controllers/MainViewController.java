package com.example.cinelinces.controllers;


import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainViewController implements Initializable {
    @FXML private BorderPane rootPane;
    @FXML private BorderPane contentArea;

    private final Map<String, Parent> viewCache = new HashMap<>();
    private String currentViewPath = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SessionManager.init();
        showHome();
    }

    public void showHome() {
        loadCachedView("/com/example/cinelinces/home-view.fxml");
    }
    public void showPopular() {
        loadCachedView("/com/example/cinelinces/popular-view.fxml");
    }

    public void showAccount() {
        var session = SessionManager.getInstance();
        String path = session.isLoggedIn()
                ? "/com/example/cinelinces/client-dashboard-view.fxml"
                : "/com/example/cinelinces/login-view.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(path)));
            Parent node = loader.load();

            if (session.isLoggedIn()) {
                var ctrl = loader.<ClientDashboardViewController>getController();
                ctrl.setClienteData(session.getCurrentCliente());
                ctrl.setMainViewController(this);
                List<CompraDetalladaDTO> compras = session.getComprasDetalladas();
                ctrl.setCompras(compras);
            } else {
                var ctrl = loader.<LoginViewController>getController();
                ctrl.setMainViewController(this);
            }

            contentArea.setCenter(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCachedView(String fxmlPath) {
        if (fxmlPath.equals(currentViewPath) && contentArea.getCenter() != null) return;
        try {
            Parent node;
            if (viewCache.containsKey(fxmlPath)) {
                node = viewCache.get(fxmlPath);
            } else {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
                node = loader.load();
                viewCache.put(fxmlPath, node);
            }
            contentArea.setCenter(node);
            currentViewPath = fxmlPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BorderPane getRootPane() { return rootPane; }
}
