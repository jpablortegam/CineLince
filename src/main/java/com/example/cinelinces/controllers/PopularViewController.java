package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.CineDAO;
import com.example.cinelinces.DAO.FuncionDAO;
import com.example.cinelinces.DAO.TipoPeliculaDAO;
import com.example.cinelinces.DAO.impl.CineDAOImpl;
import com.example.cinelinces.DAO.impl.FuncionDAOImpl;
import com.example.cinelinces.DAO.impl.TipoPeliculaDAOImpl;
import com.example.cinelinces.model.Cine;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.TipoPelicula;
import com.example.cinelinces.utils.Animations.ButtonHoverAnimator;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PopularViewController implements Initializable {

    @FXML
    private StackPane rootStack;
    @FXML
    private Pane overlayPane;
    @FXML
    private ComboBox<Cine> cineComboBox;
    @FXML
    private ComboBox<TipoPelicula> categoryComboBox;
    @FXML
    private FlowPane popularMoviesFlowPane;

    private CineDAO cineDAO;
    private FuncionDAO funcionDAO;
    private TipoPeliculaDAO tipoPeliculaDAO;

    private DialogAnimationHelper dialogHelper;
    private DialogPaneViewController dialogPaneController;
    private Node dialogPanel;

    private static final double MIN_RATING = 3.5;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cineDAO = new CineDAOImpl();
        funcionDAO = new FuncionDAOImpl();
        tipoPeliculaDAO = new TipoPeliculaDAOImpl();

        dialogHelper = new DialogAnimationHelper(rootStack, overlayPane);
        setupDialogPane();

        setupCineComboBox();
        setupCategoryComboBox();
    }

    private void setupDialogPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/dialog-pane-view.fxml"));
            Node root = loader.load();
            DialogPaneViewController ctrl = loader.getController();
            dialogPaneController = ctrl;
            dialogPanel = ctrl.getDialogPanel();
            dialogPanel.setVisible(false);
            dialogPanel.setOpacity(0);
            overlayPane.getChildren().add(dialogPanel);

            Button closeBtn = ctrl.getCloseBtn();
            if (closeBtn != null) {
                ButtonHoverAnimator.applyHoverEffect(closeBtn);
                closeBtn.setOnAction(e -> dialogHelper.hideDialog(dialogPanel, null));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar dialog-pane-view.fxml en PopularViewController.");
        }
    }


    private void setupCineComboBox() {
        new Thread(() -> {
            List<Cine> list = cineDAO.findAll();
            Platform.runLater(() -> {
                if (list != null && !list.isEmpty()) {
                    cineComboBox.setItems(FXCollections.observableArrayList(list));
                    cineComboBox.setConverter(new StringConverter<Cine>() {
                        @Override
                        public String toString(Cine c) {
                            return c != null ? c.getNombre() : "";
                        }

                        @Override
                        public Cine fromString(String s) {
                            return null;
                        }
                    });
                    cineComboBox.getSelectionModel().selectFirst();
                } else {
                    cineComboBox.setPromptText("No hay cines disponibles");
                }
            });
        }).start();

        cineComboBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            loadFilteredMovies();
        });
    }

    private void setupCategoryComboBox() {
        new Thread(() -> {
            List<TipoPelicula> tipos = tipoPeliculaDAO.findAll();
            Platform.runLater(() -> {
                if (tipos != null) {
                    TipoPelicula allCategories = new TipoPelicula();
                    allCategories.setIdTipoPelicula(0); // ID para "Todas las categorías"
                    allCategories.setNombre("Todas las categorías");
                    List<TipoPelicula> categoriesWithAll = new ArrayList<>();
                    categoriesWithAll.add(allCategories);
                    categoriesWithAll.addAll(tipos);

                    categoryComboBox.setItems(FXCollections.observableArrayList(categoriesWithAll));
                    categoryComboBox.setConverter(new StringConverter<TipoPelicula>() {
                        @Override
                        public String toString(TipoPelicula tp) {
                            return tp != null ? tp.getNombre() : "";
                        }

                        @Override
                        public TipoPelicula fromString(String s) {
                            return null;
                        }
                    });
                    categoryComboBox.getSelectionModel().selectFirst(); // Seleccionar "Todas las categorías" por defecto
                } else {
                    categoryComboBox.setPromptText("No hay categorías disponibles");
                }
            });
        }).start();

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            loadFilteredMovies();
        });
    }

    private void loadFilteredMovies() {
        Cine selectedCine = cineComboBox.getSelectionModel().getSelectedItem();
        TipoPelicula selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();

        if (selectedCine == null) {
            popularMoviesFlowPane.getChildren().clear();
            popularMoviesFlowPane.getChildren().add(new Label("Por favor, selecciona un cine para ver las películas populares."));
            return;
        }

        int idCine = selectedCine.getIdCine();
        Integer idCategory = (selectedCategory != null && selectedCategory.getIdTipoPelicula() != 0)
                ? selectedCategory.getIdTipoPelicula() : null;

        new Thread(() -> {
            List<FuncionDetallada> funciones = funcionDAO.findFuncionesDetalladasByCineId(idCine);

            // Usamos un mapa para obtener solo una función por película (la primera encontrada)
            Map<Integer, FuncionDetallada> porPelicula = new HashMap<>();
            for (FuncionDetallada f : funciones) {
                porPelicula.putIfAbsent(f.getIdPelicula(), f);
            }

            // Ahora filtramos por calificacion y categoría
            List<FuncionDetallada> filteredMovies = porPelicula.values().stream()
                    .filter(f -> f.getCalificacionPromedioPelicula() >= MIN_RATING)
                    .filter(f -> idCategory == null ||
                            (f.getIdTipoPelicula() != null && f.getIdTipoPelicula().equals(idCategory)))
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                popularMoviesFlowPane.getChildren().clear();

                if (filteredMovies.isEmpty()) {
                    popularMoviesFlowPane.getChildren().add(new Label("No hay películas populares (3.5+ estrellas) con los criterios seleccionados."));
                    return;
                }

                for (FuncionDetallada f : filteredMovies) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml"));
                        Node card = loader.load();
                        CardMovieViewController cc = loader.getController();
                        cc.initContext(popularMoviesFlowPane, overlayPane, rootStack, dialogHelper, dialogPaneController);
                        cc.setFuncionData(f);
                        popularMoviesFlowPane.getChildren().add(card);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        System.err.println("Error al cargar la tarjeta de película para: " + f.getTituloPelicula());
                    }
                }
            });
        }).start();
    }
}