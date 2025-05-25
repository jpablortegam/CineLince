package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import com.example.cinelinces.database.MovieService;
import com.example.cinelinces.utils.Animations.ButtonHoverAnimator;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
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

    private final MovieService movieService = new MovieService();
    private List<CardMovieViewController> cardControllers = new ArrayList<>();
    private DialogAnimationHelper dialogHelper;
    private Node dialogPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar DialogAnimationHelper
        dialogHelper = new DialogAnimationHelper(rootStack, overlayPane);
        ButtonHoverAnimator.applyHoverEffect(btnVerHorarios);
        loadMovieCards();
        // Cargar diálogo FXML y obtener el VBox como panel a animar
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/dialogPane-view.fxml")
            );
            AnchorPane root = loader.load();
            DialogPaneViewController dlgCtrl = loader.getController();
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
            throw new RuntimeException("No se pudo cargar dialogPane-view.fxml", e);
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
                        getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml")
                );
                Node cardNode = loader.load();

                CardMovieViewController ctrl = loader.getController();
                ctrl.setMovieData(movie);
                // Inicializar contexto para expansión con blur
                // Pasamos rootStack y dialogHelper al CardMovieViewController
                ctrl.initContext(upcomingPane, overlayPane, rootStack, dialogHelper);

                // Los handlers de mouse ya se definen dentro de CardMovieViewController FXML
                // Si quieres anularlos o añadir otros aquí, puedes hacerlo.
                // cardNode.setOnMouseEntered(ctrl::onCardHover);
                // cardNode.setOnMouseExited(ctrl::onCardHoverExit);
                // cardNode.setOnMouseClicked(ctrl::onCardClick);

                upcomingPane.getChildren().add(cardNode);
                cardControllers.add(ctrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}