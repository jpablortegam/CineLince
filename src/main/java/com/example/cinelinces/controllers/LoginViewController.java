package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.ClienteDAO;
import com.example.cinelinces.DAO.impl.ClienteDAOImpl;
import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.utils.Animations.AnimationUtil;
import com.example.cinelinces.utils.Animations.TabAnimationHelper;
import com.example.cinelinces.utils.Forms.AlertUtil;
import com.example.cinelinces.utils.Forms.EnterKeyUtil;
import com.example.cinelinces.utils.Forms.FormValidator;
import com.example.cinelinces.utils.Forms.PreferencesUtil;
import com.example.cinelinces.utils.Security.PasswordUtil;
import com.example.cinelinces.utils.SessionManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.util.Duration;

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
    @FXML
    private TextField signInUsernameField;
    @FXML
    private PasswordField signInPasswordField;
    @FXML
    private CheckBox rememberMeCheckbox;
    @FXML
    private Button signInButton;
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
    private MainViewController mainViewController;

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

        if (mainTabPane != null) {
            mainTabPane.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((obs, oldTab, newTab) -> {
                        if (oldTab != null && newTab != null && oldTab != newTab && TabAnimationHelper.class != null) {
                            TabAnimationHelper.animate(mainTabPane, oldTab, newTab);
                        }
                    });
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
            if (cliente != null) {
                boolean passwordsMatch = PasswordUtil.checkPassword(passwordFromField, cliente.getContrasenaHash());
                if (passwordsMatch) {
                    SessionManager.getInstance().setCurrentCliente(cliente);
                    if (rememberMeCheckbox.isSelected()) {
                        PreferencesUtil.saveRememberedUser(usernameOrEmailFromField);
                    } else {
                        PreferencesUtil.clearRememberedUser();
                    }
                    Platform.runLater(() -> {
                        AlertUtil.showSuccess("¡Bienvenido!", "Inicio de sesión exitoso para " + cliente.getNombre());
                        if (mainViewController != null) {
                            mainViewController.showAccount();
                        }
                    });
                } else {
                    Platform.runLater(() -> AlertUtil.showError("Autenticación fallida", "Email o contraseña incorrectos."));
                    signInPasswordField.clear();
                    AnimationUtil.shake(signInPasswordField);
                }
            } else {
                Platform.runLater(() -> AlertUtil.showError("Autenticación fallida", "Email o contraseña incorrectos."));
                signInPasswordField.clear();
                AnimationUtil.shake(signInUsernameField);
            }
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
            AnimationUtil.shake(signUpButton.getParent());
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
            nuevoCliente.setTelefono("N/A");
            nuevoCliente.setFechaNacimiento(LocalDate.of(2000, 1, 1));
            nuevoCliente.setFechaRegistro(LocalDateTime.now());

            clienteDAO.save(nuevoCliente);

            if (nuevoCliente.getIdCliente() > 0) {
                Platform.runLater(() -> {
                    AlertUtil.showSuccess("¡Cuenta creada!", "Registro exitoso para " + nuevoCliente.getNombre() + ". Ahora puedes iniciar sesión.");
                    clearSignUpForm();
                    if (mainTabPane != null && signInTab != null) {
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
        if (signInUsernameField != null && signInPasswordField != null) {
            disable = signInUsernameField.getText().trim().isEmpty() ||
                    signInPasswordField.getText().isEmpty();
        }
        if (signInButton != null) signInButton.setDisable(disable);
    }

    private void updateSignUpButton() {
        boolean disable = true;
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