package com.example.cinelinces.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML private BorderPane rootPane;
    @FXML private BorderPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showHome();
    }

    public void showHome()       { loadView("/com/example/cinelinces/home-view.fxml"); }
    public void showUpcoming()   { loadView("/com/example/cinelinces/upcoming-view.fxml"); }
    public void showPopular()    { loadView("/com/example/cinelinces/popular-view.fxml"); }
    public void showAccount()    { loadView("/com/example/cinelinces/login-view.fxml"); }


    private void loadView(String fxml) {
        try {
            Parent vista = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(fxml))
            );
            contentArea.setCenter(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BorderPane getRootPane() { return rootPane; }
}

