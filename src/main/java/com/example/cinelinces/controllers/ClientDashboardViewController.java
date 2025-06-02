package com.example.cinelinces.controllers;

// Importar todas las clases necesarias
import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.model.DTO.BoletoGeneradoDTO; // Importar la nueva DTO para boletos individuales
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
// Ya no necesitamos BigDecimal si solo mostramos el total de la CompraDetalladaDTO
// import java.math.BigDecimal;
import java.time.LocalDateTime; // Asegúrate de importar LocalDateTime si aún lo usas para agrupar
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map; // Para Collectors.groupingBy
import java.util.stream.Collectors; // Para Collectors

public class ClientDashboardViewController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label registroLabel;
    @FXML
    private VBox purchasesContainer; // Contenedor para las tarjetas de compra

    private MainViewController mainController;
    private Cliente cliente; // El cliente actualmente loggeado

    // Setter para el controlador principal (si lo usas para navegación)
    public void setMainViewController(MainViewController m) {
        this.mainController = m;
    }

    // Setter para los datos del cliente, para mostrar en la interfaz
    public void setClienteData(Cliente c) {
        this.cliente = c;
        welcomeLabel.setText("¡Bienvenido, " + c.getNombre() + " " + c.getApellido() + "!");
        emailLabel.setText("Email: " + c.getEmail());
        String fmt = "dd/MM/yyyy 'a las' HH:mm"; // Formato de fecha
        registroLabel.setText("Registrado: " + c.getFechaRegistro().format(DateTimeFormatter.ofPattern(fmt)));
    }

    // Método para establecer y mostrar las compras del cliente
    public void setCompras(List<CompraDetalladaDTO> compras) {
        purchasesContainer.getChildren().clear(); // Limpia cualquier compra anterior

        // Si no hay compras, mostrar un mensaje
        if (compras == null || compras.isEmpty()) {
            Label noPurchasesLabel = new Label("Aún no has realizado ninguna compra.");
            noPurchasesLabel.getStyleClass().add("no-purchases-label");
            purchasesContainer.getChildren().add(noPurchasesLabel);
            return;
        }

        // Ya no agrupamos por fecha aquí, ya que cada CompraDetalladaDTO
        // debería representar una única transacción de venta completa.
        // La lista 'compras' ya debe venir de la DAO correctamente agrupada por venta.

        for (CompraDetalladaDTO compra : compras) { // Iteramos directamente sobre las compras
            try {
                // Cargar el FXML de la tarjeta de compra
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/cinelinces/purchase-card-view.fxml")
                );
                Node purchaseCardNode = loader.load(); // Carga el nodo de la tarjeta

                PurchaseCardController cardController = loader.getController(); // Obtiene el controlador de la tarjeta
                cardController.setData(compra); // Pasa la CompraDetalladaDTO completa a la tarjeta

                purchasesContainer.getChildren().add(purchaseCardNode); // Añade la tarjeta al contenedor
            } catch (IOException e) {
                e.printStackTrace();
                // Manejo de errores si la tarjeta no se puede cargar
                Label errorLabel = new Label(
                        "Error al cargar detalle de la compra. ID Venta: " + compra.getIdVenta() + " - " + e.getMessage()
                );
                purchasesContainer.getChildren().add(errorLabel);
            }
        }
    }

    // Manejador del botón de cerrar sesión
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession(); // Limpia la sesión actual
        mainController.showAccount(); // Navega a la vista de la cuenta (probablemente la pantalla de inicio de sesión)
    }
}