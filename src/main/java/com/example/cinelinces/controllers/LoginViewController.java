package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.ClienteDAO;
import com.example.cinelinces.DAO.impl.ClienteDAOImpl;
import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.utils.Animations.AnimationUtil;
import com.example.cinelinces.utils.Animations.TabAnimationHelper; // Asumiendo que tienes esta clase
import com.example.cinelinces.utils.Forms.AlertUtil;
import com.example.cinelinces.utils.Forms.EnterKeyUtil;
import com.example.cinelinces.utils.Forms.FormValidator;
import com.example.cinelinces.utils.Forms.PreferencesUtil;
import com.example.cinelinces.utils.Security.PasswordUtil;
import com.example.cinelinces.utils.SessionManager; // Importar SessionManager

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform; // Importar Platform
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;         // Import necesario
import javafx.scene.control.TabPane;     // Import necesario
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    @FXML
    private TabPane mainTabPane; // Declaración FXML añadida
    @FXML
    private Tab signInTab;       // Declaración FXML añadida
    @FXML
    private Tab signUpTab;       // Declaración FXML añadida

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
    private MainViewController mainViewController; // Referencia al controlador principal

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

        // Asegúrate de que mainTabPane no sea null aquí. Si lo es, revisa tu FXML.
        if (mainTabPane != null) {
            mainTabPane.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((obs, oldTab, newTab) -> {
                        if (oldTab != null && newTab != null && oldTab != newTab && TabAnimationHelper.class != null) { // Verificación extra para TabAnimationHelper
                            TabAnimationHelper.animate(mainTabPane, oldTab, newTab);
                        }
                    });
            // Seleccionar la pestaña de inicio de sesión por defecto
            if (signInTab != null) {
                mainTabPane.getSelectionModel().select(signInTab);
            }
        }


        signInUsernameField.textProperty().addListener((o, a, b) -> updateSignInButton());
        signInPasswordField.textProperty().addListener((o, a, b) -> updateSignInButton());
        fullNameField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        emailField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        signUpUsernameField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        signUpPasswordField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        confirmPasswordField.textProperty().addListener((o, a, b) -> updateSignUpButton());
        termsCheckbox.selectedProperty().addListener((o, a, b) -> updateSignUpButton());

        updateSignInButton();
        updateSignUpButton();
    }

    // Método para que MainViewController se establezca
    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        String usernameOrEmailFromField = signInUsernameField.getText().trim();
        String passwordFromField = signInPasswordField.getText();

        if (!FormValidator.validateSignIn(usernameOrEmailFromField, passwordFromField)) {
            AlertUtil.showError("Campos incompletos", "Por favor, ingresa tu usuario/email y contraseña.");
            AnimationUtil.shake(signInUsernameField);
            return;
        }

        signInButton.setText("INICIANDO SESIÓN...");
        signInButton.setDisable(true);

        new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            Cliente cliente = clienteDAO.findByEmail(usernameOrEmailFromField);

            System.out.println("\n-------------------- INTENTO DE LOGIN --------------------");
            System.out.println("Email/Usuario ingresado para login: [" + usernameOrEmailFromField + "]");
            System.out.println("Contraseña en plano ingresada para login: [" + passwordFromField + "]");

            if (cliente != null) {
                System.out.println("Cliente encontrado en BD: " + cliente.getEmail());
                System.out.println("Hash recuperado de BD para " + cliente.getEmail() + ": [" + cliente.getContrasenaHash() + "]");
                boolean passwordsMatch = PasswordUtil.checkPassword(passwordFromField, cliente.getContrasenaHash());
                System.out.println("¿Las contraseñas coinciden (PasswordUtil.checkPassword)?: " + passwordsMatch);

                if (passwordsMatch) {
                    SessionManager.getInstance().setCurrentCliente(cliente); // Establecer sesión
                    if (rememberMeCheckbox.isSelected()) {
                        PreferencesUtil.saveRememberedUser(usernameOrEmailFromField);
                    } else {
                        PreferencesUtil.clearRememberedUser();
                    }
                    Platform.runLater(() -> {
                        AlertUtil.showSuccess("¡Bienvenido!", "Inicio de sesión exitoso para " + cliente.getNombre());
                        if (mainViewController != null) {
                            mainViewController.showAccount(); // Notificar a MainView para que recargue la vista de cuenta
                        }
                    });
                } else { // Contraseña no coincide
                    Platform.runLater(() -> AlertUtil.showError("Autenticación fallida", "Email o contraseña incorrectos."));
                    signInPasswordField.clear();
                    AnimationUtil.shake(signInPasswordField);
                }
            } else { // Cliente no encontrado
                System.out.println("No se encontró cliente con email/usuario: [" + usernameOrEmailFromField + "]");
                Platform.runLater(() -> AlertUtil.showError("Autenticación fallida", "Email o contraseña incorrectos."));
                signInPasswordField.clear();
                AnimationUtil.shake(signInUsernameField);
            }
            System.out.println("------------------------------------------------------\n");
            signInButton.setText("INICIAR SESIÓN");
            updateSignInButton();
        })).play();
    }


    @FXML
    private void handleSignUp(ActionEvent event) {
        String name = fullNameField.getText().trim();
        String emailFromField = emailField.getText().trim();
        String usernameFromField = signUpUsernameField.getText().trim();
        String passFromField = signUpPasswordField.getText();
        String confirm = confirmPasswordField.getText();
        boolean terms = termsCheckbox.isSelected();

        if (!FormValidator.validateSignUp(name, emailFromField, usernameFromField, passFromField, confirm, terms)) {
            AlertUtil.showError("Error de Validación", "Por favor, revisa todos los campos del formulario y acepta los términos.");
            AnimationUtil.shake(signUpButton.getParent()); // O un campo específico
            return;
        }

        if (clienteDAO.findByEmail(emailFromField) != null) {
            AlertUtil.showError("Error de Registro", "El email ingresado ya está en uso.");
            AnimationUtil.shake(emailField);
            return;
        }

        signUpButton.setText("CREANDO CUENTA...");
        signUpButton.setDisable(true);

        new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
            String hashedPassword = PasswordUtil.hashPassword(passFromField);

            System.out.println("\n-------------------- REGISTRO DE USUARIO --------------------");
            System.out.println("Email ingresado para registro: [" + emailFromField + "]");
            System.out.println("Contraseña en plano para registro: [" + passFromField + "]");
            System.out.println("Hash generado para registro: [" + hashedPassword + "]");
            System.out.println("-----------------------------------------------------------\n");

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
            nuevoCliente.setTelefono("N/A"); // Considera añadir campo en el formulario
            nuevoCliente.setFechaNacimiento(LocalDate.of(2000, 1, 1)); // Considera añadir campo en el formulario
            nuevoCliente.setFechaRegistro(LocalDateTime.now());

            clienteDAO.save(nuevoCliente);

            if (nuevoCliente.getIdCliente() > 0) {
                Platform.runLater(() -> {
                    AlertUtil.showSuccess("¡Cuenta creada!", "Registro exitoso para " + nuevoCliente.getNombre() + ". Ahora puedes iniciar sesión.");
                    clearSignUpForm();
                    if (mainTabPane != null && signInTab != null) { // Asegurar que no sean null
                        mainTabPane.getSelectionModel().select(signInTab);
                    }
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
        boolean disable = true;
        if (signInUsernameField != null && signInPasswordField != null) { // Chequeo defensivo
            disable = signInUsernameField.getText().trim().isEmpty() ||
                    signInPasswordField.getText().isEmpty();
        }
        if (signInButton != null) signInButton.setDisable(disable);
    }

    private void updateSignUpButton() {
        boolean disable = true;
        // Chequeos defensivos para todos los campos FXML antes de acceder a sus propiedades
        if (fullNameField != null && emailField != null && signUpUsernameField != null &&
                signUpPasswordField != null && confirmPasswordField != null && termsCheckbox != null) {
            disable = fullNameField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() ||
                    signUpUsernameField.getText().trim().isEmpty() ||
                    signUpPasswordField.getText().isEmpty() ||
                    confirmPasswordField.getText().isEmpty() ||
                    !termsCheckbox.isSelected() ||
                    !signUpPasswordField.getText().equals(confirmPasswordField.getText());
        }
        if (signUpButton != null) signUpButton.setDisable(disable);
    }

    private void clearSignUpForm() {
        if (fullNameField != null) fullNameField.clear();
        if (emailField != null) emailField.clear();
        if (signUpUsernameField != null) signUpUsernameField.clear();
        if (signUpPasswordField != null) signUpPasswordField.clear();
        if (confirmPasswordField != null) confirmPasswordField.clear();
        if (termsCheckbox != null) termsCheckbox.setSelected(false);
    }
}