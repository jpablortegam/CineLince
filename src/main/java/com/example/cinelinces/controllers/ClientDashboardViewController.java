package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Importar FXMLLoader
import javafx.scene.Node;       // Importar Node
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;     // Importar IOException
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientDashboardViewController {
    @FXML private Label welcomeLabel, emailLabel, registroLabel;
    @FXML private VBox purchasesContainer; // Este es el VBox dentro del ScrollPane

    private MainViewController mainController;
    private Cliente cliente;

    public void setMainViewController(MainViewController m) {
        this.mainController = m;
    }

    public void setClienteData(Cliente c) {
        this.cliente = c;
        welcomeLabel.setText("¡Bienvenido, " + c.getNombre() + " " + c.getApellido() + "!");
        emailLabel.setText("Email: " + c.getEmail());
        String fmt = "dd/MM/yyyy 'a las' HH:mm";
        registroLabel.setText("Registrado: " + c.getFechaRegistro().format(DateTimeFormatter.ofPattern(fmt)));
    }

    public void setCompras(List<CompraDetalladaDTO> compras) {
        purchasesContainer.getChildren().clear(); // Limpiar compras anteriores

        if (compras == null || compras.isEmpty()) {
            Label noPurchasesLabel = new Label("Aún no has realizado ninguna compra.");
            noPurchasesLabel.getStyleClass().add("no-purchases-label"); // Para estilo opcional
            // Asegúrate de que purchasesContainer no tenga un padding que desalinee esta etiqueta
            // o ajusta el padding de la etiqueta.
            purchasesContainer.getChildren().add(noPurchasesLabel);
            return;
        }

        for (CompraDetalladaDTO dto : compras) {
            try {
                // Asegúrate que la ruta al FXML de la tarjeta sea correcta
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/purchase-card-view.fxml"));
                Node purchaseCardNode = loader.load(); // Carga el FXML de la tarjeta

                PurchaseCardController cardController = loader.getController(); // Obtiene su controlador
                cardController.setData(dto); // Pasa los datos de la compra a la tarjeta

                purchasesContainer.getChildren().add(purchaseCardNode); // Añade la tarjeta al VBox
            } catch (IOException e) {
                e.printStackTrace();
                // Opcional: mostrar un mensaje de error en la UI para esta compra específica
                Label errorLabel = new Label("Error al cargar detalle de la compra: " + dto.getFuncion().getTituloPelicula());
                purchasesContainer.getChildren().add(errorLabel);
            }
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        mainController.showAccount(); // Redirige a la vista de cuenta (que mostrará el login)
    }
}