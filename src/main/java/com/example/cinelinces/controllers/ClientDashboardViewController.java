package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.utils.SessionManager; // Importar SessionManager
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
// Quita estos imports si ya no los usas para navegación directa desde aquí:
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.stage.Stage;
// import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ClientDashboardViewController {

    @FXML private Label welcomeLabel;
    @FXML private Label emailLabel;
    @FXML private Label registroLabel;

    private Cliente clienteActual;
    private MainViewController mainViewController; // Referencia al controlador principal

    // Método para que MainViewController se establezca
    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void setClienteData(Cliente cliente) {
        this.clienteActual = cliente;
        if (cliente != null) {
            welcomeLabel.setText("¡Bienvenido, " + cliente.getNombre() + " " + cliente.getApellido() + "!");
            emailLabel.setText("Email: " + cliente.getEmail());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");
            if (cliente.getFechaRegistro() != null) {
                registroLabel.setText("Fecha de registro: " + cliente.getFechaRegistro().format(formatter));
            } else {
                registroLabel.setText("Fecha de registro: N/A");
            }
        } else {
            // Manejar el caso donde el cliente es null, quizás mostrando un mensaje o valores por defecto
            welcomeLabel.setText("Bienvenido, Invitado");
            emailLabel.setText("Email: N/A");
            registroLabel.setText("Fecha de registro: N/A");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().clearSession(); // Limpiar la sesión
        if (mainViewController != null) {
            mainViewController.showAccount(); // Notificar a MainView para que recargue y muestre login
        }
        // Ya no se maneja la navegación a login-view.fxml desde aquí
    }
}