package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Cliente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ClientDashboardViewController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label registroLabel;

    private Cliente clienteActual;

    public void setClienteData(Cliente cliente) {
        this.clienteActual = cliente;
        if (cliente != null) {
            welcomeLabel.setText("¡Bienvenido, " + cliente.getNombre() + " " + cliente.getApellido() + "!");
            emailLabel.setText("Email: " + cliente.getEmail());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");
            registroLabel.setText("Fecha de registro: " + cliente.getFechaRegistro().format(formatter));
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Lógica para cerrar sesión (ej. limpiar preferencias, etc.)
        System.out.println("Cerrando sesión para: " + (clienteActual != null ? clienteActual.getEmail() : "N/A"));
        clienteActual = null; // Limpiar el cliente actual

        // Volver a la pantalla de login
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/login-view.fxml")); // Ruta a tu FXML de login
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            // scene.getStylesheets().add(getClass().getResource("/com/example/styles/login.css").toExternalForm()); // Si tienes estilos específicos
            stage.setScene(scene);
            stage.setTitle("CineLince - Iniciar Sesión");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar error al cargar la vista de login
        }
    }
}