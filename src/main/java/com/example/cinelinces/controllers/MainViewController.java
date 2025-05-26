package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.utils.SessionManager; // Importar SessionManager
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
    private String currentViewPathForCache = ""; // Para vistas que sí usan el caché simple

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showHome(); // Cargar la vista de inicio por defecto
    }

    public void showHome()       { loadCachedView("/com/example/cinelinces/home-view.fxml"); }
    public void showPopular()    { loadCachedView("/com/example/cinelinces/popular-view.fxml"); }

    /**
     * Muestra la vista de la cuenta del usuario.
     * Si el usuario ha iniciado sesión, muestra el ClientDashboardView.
     * De lo contrario, muestra el LoginView.
     */
    public void showAccount() {
        String fxmlPath;
        SessionManager session = SessionManager.getInstance();

        if (session.isLoggedIn()) {
            fxmlPath = "/com/example/cinelinces/client-dashboard-view.fxml";
        } else {
            fxmlPath = "/com/example/cinelinces/login-view.fxml";
        }

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Parent viewNode = loader.load();

            if (session.isLoggedIn() && fxmlPath.equals("/com/example/cinelinces/client-dashboard-view.fxml")) {
                ClientDashboardViewController controller = loader.getController();
                if (controller != null) {
                    controller.setClienteData(session.getCurrentCliente());
                    controller.setMainViewController(this); // Para que el dashboard pueda llamar a showAccount en logout
                }
            } else if (!session.isLoggedIn() && fxmlPath.equals("/com/example/cinelinces/login-view.fxml")) {
                LoginViewController controller = loader.getController();
                if (controller != null) {
                    controller.setMainViewController(this); // Para que login pueda llamar a showAccount después de login exitoso
                }
            }

            contentArea.setCenter(viewNode);
            // No actualizamos currentViewPathForCache aquí porque showAccount maneja su propia lógica de carga
            // y no necesariamente queremos cachear el login/dashboard de la misma manera simple.
        } catch (IOException e) {
            System.err.println("Error al cargar la vista de cuenta: " + fxmlPath);
            e.printStackTrace();
            // Considera mostrar un mensaje de error al usuario en la UI
        }
    }


    /**
     * Carga una vista desde FXML y la muestra en el contentArea.
     * Utiliza un caché para evitar recargar vistas ya cargadas.
     * Este método es para vistas que no requieren pasar datos dinámicos al inicializarse.
     * @param fxmlPath La ruta al archivo FXML.
     */
    private void loadCachedView(String fxmlPath) {
        if (fxmlPath.equals(currentViewPathForCache) && contentArea.getCenter() != null) {
            return; // Ya está cargada la vista correcta
        }
        try {
            Parent viewNode;
            if (viewCache.containsKey(fxmlPath)) {
                viewNode = viewCache.get(fxmlPath);
                // Si necesitas reinicializar algo en el controlador cacheado, podrías hacerlo aquí.
                // Object cachedController = viewNode.getProperties().get("controller");
                // if (cachedController instanceof HomeController && fxmlPath.equals("/com/example/cinelinces/home-view.fxml")) {
                //     ((HomeController) cachedController).refreshData(); // Método hipotético
                // }
            } else {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
                viewNode = loader.load();
                viewCache.put(fxmlPath, viewNode);
                // Opcional: guardar el controlador para interacciones futuras si se recarga desde caché
                // viewNode.getProperties().put("controller", loader.getController());
            }
            contentArea.setCenter(viewNode);
            currentViewPathForCache = fxmlPath;
        } catch (IOException e) {
            System.err.println("Error al cargar la vista cacheada: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public BorderPane getRootPane() { return rootPane; }
}