package com.example.cinelinces.controllers;



import com.example.cinelinces.services.AuthService;
import com.example.cinelinces.utils.Animations.AnimationUtil;
import com.example.cinelinces.utils.Animations.TabAnimationHelper;
import com.example.cinelinces.utils.Forms.AlertUtil;
import com.example.cinelinces.utils.Forms.EnterKeyUtil;
import com.example.cinelinces.utils.Forms.FormValidator;
import com.example.cinelinces.utils.Forms.PreferencesUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    @FXML
    private TabPane mainTabPane;
    @FXML private Tab signInTab, signUpTab;

    @FXML private TextField signInUsernameField;
    @FXML private PasswordField signInPasswordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Button    signInButton;

    @FXML private TextField fullNameField, emailField, signUpUsernameField;
    @FXML private PasswordField signUpPasswordField, confirmPasswordField;
    @FXML private CheckBox termsCheckbox;
    @FXML private Button    signUpButton;

    private Timeline currentAnimation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) Rellenar usuario recordado
        String remembered = PreferencesUtil.getRememberedUser();
        if (!remembered.isEmpty()) {
            signInUsernameField.setText(remembered);
            rememberMeCheckbox.setSelected(true);
        }

        // 2) ENTER key handling
        EnterKeyUtil.register(signInPasswordField, signInButton,  () -> handleSignIn(null));
        EnterKeyUtil.register(confirmPasswordField,  signUpButton, () -> handleSignUp(null));

        // 3) Animación de cambio de pestañas
        mainTabPane.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldTab, newTab) -> {
                    if (oldTab != null && newTab != null && oldTab != newTab) {
                        TabAnimationHelper.animate(mainTabPane, oldTab, newTab);
                    }
                });

        // 4) Validación en tiempo real (habilita/deshabilita botones)
        signInUsernameField.textProperty().addListener((o,a,b) -> updateSignInButton());
        signInPasswordField.textProperty().addListener((o,a,b) -> updateSignInButton());
        fullNameField.textProperty().addListener((o,a,b) -> updateSignUpButton());
        emailField.textProperty().addListener((o,a,b) -> updateSignUpButton());
        signUpUsernameField.textProperty().addListener((o,a,b) -> updateSignUpButton());
        signUpPasswordField.textProperty().addListener((o,a,b) -> updateSignUpButton());
        confirmPasswordField.textProperty().addListener((o,a,b) -> updateSignUpButton());
        termsCheckbox.selectedProperty().addListener((o,a,b) -> updateSignUpButton());

        mainTabPane.getSelectionModel().select(signInTab);
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        String user = signInUsernameField.getText().trim();
        String pass = signInPasswordField.getText();

        if (!FormValidator.validateSignIn(user, pass)) {
            AlertUtil.showError("Error", "Usuario o contraseña inválidos");
            AnimationUtil.shake(signInUsernameField);
            return;
        }

        signInButton.setText("INICIANDO SESIÓN...");
        signInButton.setDisable(true);

        new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            if (AuthService.authenticate(user, pass)) {
                if (rememberMeCheckbox.isSelected()) {
                    PreferencesUtil.saveRememberedUser(user);
                }
                AlertUtil.showSuccess("¡Bienvenido!", "Inicio de sesión exitoso");
                // TODO: navegar a la ventana principal
            } else {
                AlertUtil.showError("Autenticación fallida", "Credenciales incorrectas");
                signInPasswordField.clear();
                AnimationUtil.shake(signInPasswordField);
            }
            signInButton.setText("INICIAR SESIÓN");
            signInButton.setDisable(false);
        })).play();
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        String name    = fullNameField.getText().trim();
        String email   = emailField.getText().trim();
        String user    = signUpUsernameField.getText().trim();
        String pass    = signUpPasswordField.getText();
        String confirm = confirmPasswordField.getText();
        boolean terms  = termsCheckbox.isSelected();

        if (!FormValidator.validateSignUp(name, email, user, pass, confirm, terms)) {
            AlertUtil.showError("Error", "Revisa los campos del formulario");
            AnimationUtil.shake(fullNameField);
            return;
        }

        signUpButton.setText("CREANDO CUENTA...");
        signUpButton.setDisable(true);

        new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            if (AuthService.register(name, email, user, pass)) {
                AlertUtil.showSuccess("¡Cuenta creada!", "Registro exitoso");
                clearSignUpForm();
                mainTabPane.getSelectionModel().select(signInTab);
            } else {
                AlertUtil.showError("Error de registro", "Usuario o email ya existe");
                AnimationUtil.shake(signUpButton);
            }
            signUpButton.setText("CREAR CUENTA");
            signUpButton.setDisable(false);
        })).play();
    }

    private void updateSignInButton() {
        boolean ok = FormValidator.validateSignIn(
                signInUsernameField.getText().trim(),
                signInPasswordField.getText()
        );
        signInButton.setDisable(!ok);
    }

    private void updateSignUpButton() {
        boolean ok = FormValidator.validateSignUp(
                fullNameField.getText().trim(),
                emailField.getText().trim(),
                signUpUsernameField.getText().trim(),
                signUpPasswordField.getText(),
                confirmPasswordField.getText(),
                termsCheckbox.isSelected()
        );
        signUpButton.setDisable(!ok);
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
