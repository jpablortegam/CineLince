// HomeViewController.java
package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import com.example.cinelinces.services.MovieService;
import com.example.cinelinces.utils.ButtonHoverAnimator;
import com.example.cinelinces.utils.DialogAnimationHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeViewController implements Initializable {
    @FXML private StackPane rootStack;
    @FXML private Pane overlayPane;
    @FXML private Button btnVerHorarios;
    @FXML private FlowPane upcomingPane;
    @FXML private Button btnVerTodas;

    private final MovieService movieService = new MovieService();
    private List<com.example.cinelinces.controllers.MovieCardViewController> cardControllers = new ArrayList<>();
    private DialogAnimationHelper dialogHelper;
    private Node dialogPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialogHelper = new DialogAnimationHelper(rootStack, overlayPane);
        ButtonHoverAnimator.applyHoverEffect(btnVerHorarios);
        loadMovieCards();
        // Cargar diálogo FXML y obtener el VBox como panel a animar
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/dialogPane.fxml")
            );
            AnchorPane root = loader.load();
            DialogPaneController dlgCtrl = loader.getController();
            VBox panel = dlgCtrl.getDialogPanel();
            dialogPanel = panel;
            // oculto al inicio
            panel.setVisible(false);
            panel.setOpacity(0);
            overlayPane.getChildren().add(panel);

            // configurar botón de cierre
            Button closeBtn = dlgCtrl.getCloseBtn();
            ButtonHoverAnimator.applyHoverEffect(closeBtn);
            closeBtn.setOnAction(e -> dialogHelper.hideDialog(panel, btnVerHorarios));
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar dialogPane.fxml", e);
        }

        // acción de mostrar diálogo animado
        btnVerHorarios.setOnAction(e -> dialogHelper.showDialog(dialogPanel, btnVerHorarios));

    }

    private void loadMovieCards() {
        List<Movie> upcoming = movieService.fetchUpcoming();
        upcomingPane.getChildren().clear();
        cardControllers.clear();

        for (Movie movie : upcoming) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/cinelinces/movieCard.fxml")
                );
                Node cardNode = loader.load();

                MovieCardViewController ctrl = loader.getController();
                ctrl.setMovieData(movie);
                // Inicializar contexto para expansión con blur
                ctrl.initContext(upcomingPane, overlayPane);

                // Habilitar hover y click handlers
                cardNode.setOnMouseEntered(ctrl::onCardHover);
                cardNode.setOnMouseExited(ctrl::onCardHoverExit);
                cardNode.setOnMouseClicked(ctrl::onCardClick);

                upcomingPane.getChildren().add(cardNode);
                cardControllers.add(ctrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
