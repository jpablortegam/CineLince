package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.ClienteDAO;
import com.example.cinelinces.DAO.impl.ClienteDAOImpl;
import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.utils.Animations.AnimationUtil; // Asumiendo que tienes esta clase
import com.example.cinelinces.utils.Animations.TabAnimationHelper; // Asumiendo que tienes esta clase
import com.example.cinelinces.utils.Forms.AlertUtil;
import com.example.cinelinces.utils.Forms.EnterKeyUtil; // Asumiendo que tienes esta clase
import com.example.cinelinces.utils.Forms.FormValidator;
import com.example.cinelinces.utils.Forms.PreferencesUtil; // Asumiendo que tienes esta clase
import com.example.cinelinces.utils.Security.PasswordUtil;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform; // Asegúrate de tener este import
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab signInTab;
    @FXML
    private Tab signUpTab;

    // Campos para Iniciar Sesión
    @FXML
    private TextField signInUsernameField;
    @FXML
    private PasswordField signInPasswordField;
    @FXML
    private CheckBox rememberMeCheckbox;
    @FXML
    private Button signInButton;

    // Campos para Registrarse
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField signUpUsernameField;
    @FXML
    private PasswordField signUpPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private CheckBox termsCheckbox;
    @FXML
    private Button signUpButton;

    private ClienteDAO clienteDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clienteDAO = new ClienteDAOImpl();

        String remembered = PreferencesUtil.getRememberedUser();
        if (remembered != null && !remembered.isEmpty()) {
            signInUsernameField.setText(remembered);
            rememberMeCheckbox.setSelected(true);
        }

        EnterKeyUtil.register(signInPasswordField, signInButton, () -> handleSignIn(null));
        EnterKeyUtil.register(confirmPasswordField, signUpButton, () -> handleSignUp(null));

        mainTabPane.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldTab, newTab) -> {
                    if (oldTab != null && newTab != null && oldTab != newTab) {
                        TabAnimationHelper.animate(mainTabPane, oldTab, newTab);
                    }
                });

        signInUsernameField.textProperty().addListener((o, a, b) -> updateSignInButton());
        signInPasswordField.textProperty().addListener((o, a, b) -> updateSignInButton());
        fullNameField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        emailField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        signUpUsernameField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        signUpPasswordField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        confirmPasswordField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        termsCheckbox.selectedProperty().addListener((o, a, b) -> updateSignUpButton());

        mainTabPane.getSelectionModel().select(signInTab);
        updateSignInButton();
        updateSignUpButton();
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        String usernameOrEmailFromField = signInUsernameField.getText().trim();
        String passwordFromField = signInPasswordField.getText(); // Contraseña en texto plano del formulario de login

        if (!FormValidator.validateSignIn(usernameOrEmailFromField, passwordFromField)) {
            AlertUtil.showError("Campos incompletos", "Por favor, ingresa tu usuario/email y contraseña.");
            AnimationUtil.shake(signInUsernameField);
            return;
        }

        signInButton.setText("INICIANDO SESIÓN...");
        signInButton.setDisable(true);

        new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            Cliente cliente = clienteDAO.findByEmail(usernameOrEmailFromField);

            // ---- DEBUGGING LOGIN ----
            System.out.println("\n-------------------- INTENTO DE LOGIN --------------------");
            System.out.println("Email/Usuario ingresado para login: [" + usernameOrEmailFromField + "]");
            System.out.println("Contraseña en plano ingresada para login: [" + passwordFromField + "]");
            // ---- END DEBUGGING LOGIN ----

            if (cliente != null) {
                // ---- DEBUGGING LOGIN ----
                System.out.println("Cliente encontrado en BD: " + cliente.getEmail());
                System.out.println("Hash recuperado de BD para " + cliente.getEmail() + ": [" + cliente.getContrasenaHash() + "]");
                // ---- END DEBUGGING LOGIN ----

                boolean passwordsMatch = PasswordUtil.checkPassword(passwordFromField, cliente.getContrasenaHash());
                // ---- DEBUGGING LOGIN ----
                System.out.println("¿Las contraseñas coinciden (PasswordUtil.checkPassword)?: " + passwordsMatch);
                // ---- END DEBUGGING LOGIN ----

                if (passwordsMatch) {
                    if (rememberMeCheckbox.isSelected()) {
                        PreferencesUtil.saveRememberedUser(usernameOrEmailFromField);
                    } else {
                        PreferencesUtil.clearRememberedUser();
                    }
                    Platform.runLater(() -> {
                        AlertUtil.showSuccess("¡Bienvenido!", "Inicio de sesión exitoso para " + cliente.getNombre());
                        navigateToClientDashboard(cliente);
                    });
                } else { // Contraseña no coincide
                    Platform.runLater(() -> {
                        AlertUtil.showError("Autenticación fallida", "Email o contraseña incorrectos.");
                    });
                    signInPasswordField.clear();
                    AnimationUtil.shake(signInPasswordField);
                }
            } else { // Cliente no encontrado por email
                // ---- DEBUGGING LOGIN ----
                System.out.println("No se encontró cliente con email/usuario: [" + usernameOrEmailFromField + "]");
                // ---- END DEBUGGING LOGIN ----
                Platform.runLater(() -> {
                    AlertUtil.showError("Autenticación fallida", "Email o contraseña incorrectos.");
                });
                signInPasswordField.clear(); // Limpiar contraseña igual por seguridad/UX
                AnimationUtil.shake(signInUsernameField); // Shake al campo de usuario/email
            }
            // ---- DEBUGGING LOGIN ----
            System.out.println("------------------------------------------------------\n");
            // ---- END DEBUGGING LOGIN ----

            signInButton.setText("INICIAR SESIÓN");
            updateSignInButton();
        })).play();
    }

    private void navigateToClientDashboard(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/client-dashboard-view.fxml"));
            Parent root = loader.load();

            ClientDashboardViewController controller = loader.getController();
            controller.setClienteData(cliente);

            Stage stage = (Stage) signInButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Panel de Cliente - " + cliente.getNombre());
            stage.centerOnScreen();
            stage.show();

        } catch (IOException ex) {
            System.err.println("Error al cargar client-dashboard-view.fxml: " + ex.getMessage());
            ex.printStackTrace();
            AlertUtil.showError("Error de Navegación", "No se pudo cargar la pantalla del panel de cliente.");
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        String name = fullNameField.getText().trim();
        String emailFromField = emailField.getText().trim(); // Email del formulario de registro
        String usernameFromField = signUpUsernameField.getText().trim(); // Usuario del formulario de registro
        String passFromField = signUpPasswordField.getText(); // Contraseña en texto plano del formulario de registro
        String confirm = confirmPasswordField.getText();
        boolean terms = termsCheckbox.isSelected();

        if (!FormValidator.validateSignUp(name, emailFromField, usernameFromField, passFromField, confirm, terms)) {
            AlertUtil.showError("Error de Validación", "Por favor, revisa todos los campos del formulario y acepta los términos.");
            AnimationUtil.shake(signUpButton.getParent());
            return;
        }

        if (clienteDAO.findByEmail(emailFromField) != null) {
            AlertUtil.showError("Error de Registro", "El email ingresado ya está en uso.");
            AnimationUtil.shake(emailField);
            return;
        }
        // Aquí también podrías verificar si 'usernameFromField' ya existe si es un campo único.

        signUpButton.setText("CREANDO CUENTA...");
        signUpButton.setDisable(true);

        new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
            String hashedPassword = PasswordUtil.hashPassword(passFromField);

            // ---- DEBUGGING REGISTRO ----
            System.out.println("\n-------------------- REGISTRO DE USUARIO --------------------");
            System.out.println("Email ingresado para registro: [" + emailFromField + "]");
            // System.out.println("Username ingresado para registro: [" + usernameFromField + "]"); // Si usas username
            System.out.println("Contraseña en plano para registro: [" + passFromField + "]");
            System.out.println("Hash generado para registro: [" + hashedPassword + "]");
            System.out.println("-----------------------------------------------------------\n");
            // ---- END DEBUGGING REGISTRO ----


            Cliente nuevoCliente = new Cliente();
            String[] nombreCompletoParts = name.split(" ", 2);
            nuevoCliente.setNombre(nombreCompletoParts[0]);
            if (nombreCompletoParts.length > 1) {
                nuevoCliente.setApellido(nombreCompletoParts[1]);
            } else {
                nuevoCliente.setApellido("");
            }
            nuevoCliente.setEmail(emailFromField);
            nuevoCliente.setContrasenaHash(hashedPassword);
            // Si vas a usar el campo username de signUpUsernameField:
            // nuevoCliente.setUsername(usernameFromField); // Necesitarías añadir este campo al modelo Cliente y a la BD

            nuevoCliente.setTelefono("N/A");
            nuevoCliente.setFechaNacimiento(LocalDate.of(2000, 1, 1));
            nuevoCliente.setFechaRegistro(LocalDateTime.now());

            clienteDAO.save(nuevoCliente);

            if (nuevoCliente.getIdCliente() > 0) {
                Platform.runLater(() -> {
                    AlertUtil.showSuccess("¡Cuenta creada!", "Registro exitoso para " + nuevoCliente.getNombre() + ". Ahora puedes iniciar sesión.");
                    clearSignUpForm();
                    mainTabPane.getSelectionModel().select(signInTab);
                });
            } else {
                Platform.runLater(() -> {
                    AlertUtil.showError("Error de Registro", "No se pudo crear la cuenta en la base de datos. Intenta de nuevo.");
                });
            }

            signUpButton.setText("CREAR CUENTA");
            updateSignUpButton();
        })).play();
    }

    private void updateSignInButton() {
        boolean disable = signInUsernameField.getText().trim().isEmpty() ||
                signInPasswordField.getText().isEmpty();
        signInButton.setDisable(disable);
    }

    private void updateSignUpButton() {
        boolean disable = fullNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                signUpUsernameField.getText().trim().isEmpty() ||
                signUpPasswordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty() ||
                !termsCheckbox.isSelected() ||
                !signUpPasswordField.getText().equals(confirmPasswordField.getText());
        signUpButton.setDisable(disable);
    }

    private void clearSignUpForm() {
        fullNameField.clear();
        emailField.clear();
        signUpUsernameField.clear();
        signUpPasswordField.clear();
        confirmPasswordField.clear();
        termsCheckbox.setSelected(false);
    }
}