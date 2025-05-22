package com.example.cinelinces.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;


public class LoginViewController implements Initializable {

    // Tab Pane
    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab signInTab;

    @FXML
    private Tab signUpTab;

    // Sign In Components
    @FXML
    private TextField signInUsernameField;

    @FXML
    private PasswordField signInPasswordField;

    @FXML
    private CheckBox rememberMeCheckbox;

    @FXML
    private Button signInButton;

    // Sign Up Components
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

    // Animation variables
    private Animation currentAnimation;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,}$");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFormValidation();
        setupEnterKeyHandling();
        setupTabChangeAnimations();
        setupRealTimeValidation();

        // Default to Sign In
        mainTabPane.getSelectionModel().select(signInTab);
    }

    private void setupFormValidation() {
        updateSignInButtonState();
        updateSignUpButtonState();
    }

    private void setupEnterKeyHandling() {
        // Sign In: press ENTER to submit
        signInPasswordField.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER && !signInButton.isDisabled()) {
                handleSignIn(new ActionEvent());
            }
        });
        // Sign Up: press ENTER on confirmPassword
        confirmPasswordField.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER && !signUpButton.isDisabled()) {
                handleSignUp(new ActionEvent());
            }
        });
    }

    private void setupTabChangeAnimations() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (oldTab != null && newTab != null && oldTab != newTab) {
                animateTabTransition(oldTab, newTab);
            }
        });
    }

    private void animateTabTransition(Tab oldTab, Tab newTab) {
        if (currentAnimation != null) currentAnimation.stop();

        VBox oldContent = (VBox) oldTab.getContent();
        VBox newContent = (VBox) newTab.getContent();
        boolean isRight = mainTabPane.getTabs().indexOf(newTab) > mainTabPane.getTabs().indexOf(oldTab);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), oldContent);
        fadeOut.setFromValue(1.0); fadeOut.setToValue(0.0);
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), oldContent);
        slideOut.setFromX(0); slideOut.setToX(isRight ? -30 : 30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newContent);
        fadeIn.setFromValue(0.0); fadeIn.setToValue(1.0);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), newContent);
        slideIn.setFromX(isRight ? 30 : -30); slideIn.setToX(0);

        newContent.setOpacity(0.0);
        newContent.setTranslateX(isRight ? 30 : -30);

        ParallelTransition out = new ParallelTransition(fadeOut, slideOut);
        ParallelTransition in = new ParallelTransition(fadeIn, slideIn);
        out.setInterpolator(Interpolator.EASE_IN);
        in.setInterpolator(Interpolator.EASE_OUT);

        SequentialTransition seq = new SequentialTransition(out, in);
        currentAnimation = seq;
        seq.play();
        seq.play();
    }

    // ===== SIGN IN =====
    @FXML
    private void handleSignIn(ActionEvent event) {
        String username = signInUsernameField.getText().trim();
        String password = signInPasswordField.getText();
        boolean remember = rememberMeCheckbox.isSelected();

        if (!validateSignInInput(username, password)) return;

        signInButton.setText("INICIANDO SESIÓN...");
        signInButton.setDisable(true);

        new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            if (authenticateUser(username, password)) {
                if (remember) saveUserPreferences(username);
                showSuccessAlert("¡Bienvenido!", "Inicio de sesión exitoso");
                navigateToMainWindow();
            } else {
                showErrorAlert("Error de autenticación", "Usuario o contraseña incorrectos");
                signInPasswordField.clear();
                signInPasswordField.requestFocus();
                addShakeAnimation(signInButton);
            }
            signInButton.setText("INICIAR SESIÓN");
            signInButton.setDisable(false);
        })).play();
    }

    // ===== SIGN UP =====
    @FXML
    private void handleSignUp(ActionEvent event) {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = signUpUsernameField.getText().trim();
        String password = signUpPasswordField.getText();
        String confirm = confirmPasswordField.getText();
        boolean terms = termsCheckbox.isSelected();

        if (!validateSignUpInput(fullName, email, username, password, confirm, terms)) return;

        signUpButton.setText("CREANDO CUENTA...");
        signUpButton.setDisable(true);

        new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            if (registerUser(fullName, email, username, password)) {
                showSuccessAlert("¡Cuenta creada!", "Tu cuenta ha sido creada exitosamente.");
                clearSignUpForm();
                mainTabPane.getSelectionModel().select(signInTab);
                signInUsernameField.setText(username);
            } else {
                showErrorAlert("Error de registro", "El usuario o email ya existe.");
                addShakeAnimation(signUpButton);
            }
            signUpButton.setText("CREAR CUENTA");
            signUpButton.setDisable(false);
        })).play();
    }

    // ===== VALIDATION =====
    private boolean validateSignInInput(String user, String pass) {
        if (user.isEmpty()) {
            showErrorAlert("Campo requerido", "Por favor ingresa tu usuario o email");
            signInUsernameField.requestFocus();
            addShakeAnimation(signInUsernameField);
            return false;
        }
        if (pass.isEmpty()) {
            showErrorAlert("Campo requerido", "Por favor ingresa tu contraseña");
            signInPasswordField.requestFocus();
            addShakeAnimation(signInPasswordField);
            return false;
        }
        if (pass.length() < 6) {
            showErrorAlert("Contraseña inválida", "La contraseña debe tener al menos 6 caracteres");
            signInPasswordField.requestFocus();
            addShakeAnimation(signInPasswordField);
            return false;
        }
        return true;
    }

    private boolean validateSignUpInput(String name, String email, String user,
                                        String pass, String confirm, boolean terms) {
        if (name.isEmpty()) {
            showErrorAlert("Campo requerido", "Por favor ingresa tu nombre completo");
            fullNameField.requestFocus();
            addShakeAnimation(fullNameField);
            return false;
        }
        if (!isValidEmail(email)) {
            showErrorAlert("Email inválido", "Por favor ingresa un email válido");
            emailField.requestFocus();
            addShakeAnimation(emailField);
            return false;
        }
        if (user.isEmpty() || user.length() < 3) {
            showErrorAlert("Usuario inválido", "El usuario debe tener al menos 3 caracteres");
            signUpUsernameField.requestFocus();
            addShakeAnimation(signUpUsernameField);
            return false;
        }
        if (pass.length() < 6) {
            showErrorAlert("Contraseña inválida", "La contraseña debe tener al menos 6 caracteres");
            signUpPasswordField.requestFocus();
            addShakeAnimation(signUpPasswordField);
            return false;
        }
        if (!pass.equals(confirm)) {
            showErrorAlert("Contraseñas no coinciden", "Las contraseñas no coinciden");
            confirmPasswordField.requestFocus();
            addShakeAnimation(confirmPasswordField);
            return false;
        }
        if (!terms) {
            showErrorAlert("Términos requeridos", "Debes aceptar los términos y condiciones");
            addShakeAnimation(termsCheckbox);
            return false;
        }
        return true;
    }

    private void setupRealTimeValidation() {
        signInUsernameField.textProperty().addListener((o, a, b) -> updateSignInButtonState());
        signInPasswordField.textProperty().addListener((o, a, b) -> updateSignInButtonState());
        fullNameField.textProperty().addListener((o, a, b) -> updateSignUpButtonState());
        emailField.textProperty().addListener((o, a, b) -> updateSignUpButtonState());
        signUpUsernameField.textProperty().addListener((o, a, b) -> updateSignUpButtonState());
        signUpPasswordField.textProperty().addListener((o, a, b) -> updateSignUpButtonState());
        confirmPasswordField.textProperty().addListener((o, a, b) -> updateSignUpButtonState());
        termsCheckbox.selectedProperty().addListener((o, a, b) -> updateSignUpButtonState());
    }

    private void updateSignInButtonState() {
        boolean valid = !signInUsernameField.getText().trim().isEmpty()
                && !signInPasswordField.getText().isEmpty();
        signInButton.setDisable(!valid);
    }

    private void updateSignUpButtonState() {
        boolean valid = !fullNameField.getText().trim().isEmpty()
                && isValidEmail(emailField.getText().trim())
                && signUpUsernameField.getText().trim().length() >= 3
                && signUpPasswordField.getText().length() >= 6
                && signUpPasswordField.getText().equals(confirmPasswordField.getText())
                && termsCheckbox.isSelected();
        signUpButton.setDisable(!valid);
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // ===== STUBS =====
    private boolean authenticateUser(String user, String pass) {
        // TODO: Replace with real authentication logic
        return "admin".equals(user) && "password".equals(pass);
    }

    private boolean registerUser(String name, String email, String user, String pass) {
        // TODO: Replace with real registration logic
        return true;
    }

    private void saveUserPreferences(String user) {
        Preferences prefs = Preferences.userNodeForPackage(LoginViewController.class);
        prefs.put("rememberedUser", user);
    }

    private void showSuccessAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void addShakeAnimation(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }

    private void clearSignUpForm() {
        fullNameField.clear();
        emailField.clear();
        signUpUsernameField.clear();
        signUpPasswordField.clear();
        confirmPasswordField.clear();
        termsCheckbox.setSelected(false);
    }

    private void navigateToMainWindow() {
        // TODO: Implement navigation to the main application window
    }

    public void handleForgotPassword(MouseEvent mouseEvent) {

    }
}
