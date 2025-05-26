package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.CineDAO;
import com.example.cinelinces.DAO.FuncionDAO;
import com.example.cinelinces.DAO.impl.CineDAOImpl;
import com.example.cinelinces.DAO.impl.FuncionDAOImpl;
import com.example.cinelinces.model.Cine;
import com.example.cinelinces.model.DTO.FuncionDetallada; // Ensure this DTO has getIdPelicula()
import com.example.cinelinces.utils.Animations.ButtonHoverAnimator;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet; // For tracking unique movie IDs
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set; // For tracking unique movie IDs

public class HomeViewController implements Initializable {
    @FXML private StackPane rootStack;
    @FXML private Pane overlayPane;
    @FXML private Button btnVerHorarios;
    @FXML private FlowPane enCarteleraPane;
    @FXML private FlowPane proximamentePane;
    @FXML private ComboBox<Cine> cineComboBox;

    @FXML private StackPane featuredBannerPane;
    @FXML private ImageView featuredImage;
    @FXML private Label featuredTitle;
    @FXML private Label featuredSubtitle;

    private CineDAO cineDAO;
    private FuncionDAO funcionDAO;
    private DialogAnimationHelper dialogHelper;
    private Node dialogPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cineDAO = new CineDAOImpl();
        funcionDAO = new FuncionDAOImpl();

        configureFeaturedBannerImage();
        dialogHelper = new DialogAnimationHelper(rootStack, overlayPane);
        setupCineComboBox();
        loadProximamenteCards(); // Consider similar logic for "Próximamente" if needed
        setupDialogPane();
    }

    private void configureFeaturedBannerImage() {
        try {
            String imagePath = "/com/example/images/dune_banner.jpg";
            InputStream featuredStream = getClass().getResourceAsStream(imagePath);

            if (featuredStream != null) {
                Image image = new Image(featuredStream);
                featuredImage.setImage(image);
                featuredImage.fitWidthProperty().bind(featuredBannerPane.widthProperty());
                featuredImage.fitHeightProperty().bind(featuredBannerPane.heightProperty());
                featuredImage.setPreserveRatio(false);

                double originalImageWidth = image.getWidth();
                double originalImageHeight = image.getHeight();
                double sectionStartXRatio = 0.05;
                double sectionStartYRatio = 0.05;
                double sectionWidthRatio = 0.5;
                double sectionHeightRatio = 0.5;
                double vpX = originalImageWidth * sectionStartXRatio;
                double vpY = originalImageHeight * sectionStartYRatio;
                double vpWidth = originalImageWidth * sectionWidthRatio;
                double vpHeight = originalImageHeight * sectionHeightRatio;
                final double MIN_DIMENSION_FALLBACK_RATIO = 0.05;

                if (vpX < 0) vpX = 0;
                if (vpY < 0) vpY = 0;
                if (vpWidth <= 0) vpWidth = originalImageWidth * MIN_DIMENSION_FALLBACK_RATIO;
                if (vpHeight <= 0) vpHeight = originalImageHeight * MIN_DIMENSION_FALLBACK_RATIO;
                if (vpX + vpWidth > originalImageWidth) {
                    vpWidth = originalImageWidth - vpX;
                    if (vpWidth <= 0) {
                        vpX = originalImageWidth * (1.0 - MIN_DIMENSION_FALLBACK_RATIO);
                        vpWidth = originalImageWidth * MIN_DIMENSION_FALLBACK_RATIO;
                    }
                }
                if (vpY + vpHeight > originalImageHeight) {
                    vpHeight = originalImageHeight - vpY;
                    if (vpHeight <= 0) {
                        vpY = originalImageHeight * (1.0 - MIN_DIMENSION_FALLBACK_RATIO);
                        vpHeight = originalImageHeight * MIN_DIMENSION_FALLBACK_RATIO;
                    }
                }
                Rectangle2D viewportRect = new Rectangle2D(vpX, vpY, vpWidth, vpHeight);
                featuredImage.setViewport(viewportRect);
            } else {
                System.err.println("Error: No se pudo cargar la imagen del banner. Verifica la ruta: " + imagePath);
                featuredBannerPane.setStyle("-fx-background-color: #2c3e50;");
                featuredTitle.setText("Banner no disponible");
            }
        } catch (Exception e) {
            System.err.println("Excepción al cargar o configurar la imagen del banner: " + e.getMessage());
            e.printStackTrace();
            featuredBannerPane.setStyle("-fx-background-color: #2c3e50;");
            featuredTitle.setText("Error al cargar banner");
        }
    }

    private void setupDialogPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/dialogPane-view.fxml"));
            Node dialogRootNode = loader.load();
            DialogPaneViewController dialogController = loader.getController();
            this.dialogPanel = dialogController.getDialogPanel();

            this.dialogPanel.setVisible(false);
            this.dialogPanel.setOpacity(0);

            if (!overlayPane.getChildren().contains(this.dialogPanel)) {
                overlayPane.getChildren().add(this.dialogPanel);
            }

            Button closeBtn = dialogController.getCloseBtn();
            if (closeBtn != null) {
                ButtonHoverAnimator.applyHoverEffect(closeBtn);
                closeBtn.setOnAction(e -> dialogHelper.hideDialog(this.dialogPanel, btnVerHorarios));
            }

            if (btnVerHorarios != null) {
                ButtonHoverAnimator.applyHoverEffect(btnVerHorarios);
                btnVerHorarios.setOnAction(e -> {
                    if (this.dialogPanel != null) {
                        dialogHelper.showDialog(this.dialogPanel, btnVerHorarios);
                    } else {
                        System.err.println("Error: El panel del diálogo (dialogPanel) no ha sido inicializado.");
                    }
                });
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error crítico durante la configuración del diálogo: " + e.getMessage());
            e.printStackTrace();
            Label errorLabel = new Label("Error al cargar opciones. Intente más tarde.");
            if (!overlayPane.getChildren().contains(errorLabel)) {
                overlayPane.getChildren().add(errorLabel);
            }
        }
    }

    private void setupCineComboBox() {
        new Thread(() -> {
            List<Cine> cines = cineDAO.findAll();
            Platform.runLater(() -> {
                if (cines != null && !cines.isEmpty()) {
                    cineComboBox.setItems(FXCollections.observableArrayList(cines));
                    cineComboBox.setConverter(new StringConverter<Cine>() {
                        @Override public String toString(Cine cine) { return cine != null ? cine.getNombre() : ""; }
                        @Override public Cine fromString(String string) { return null; }
                    });
                    cineComboBox.setCellFactory(lv -> new ListCell<Cine>() {
                        @Override protected void updateItem(Cine cine, boolean empty) {
                            super.updateItem(cine, empty);
                            setText(empty || cine == null ? null : cine.getNombre());
                        }
                    });
                    if (!cines.isEmpty()) {
                        cineComboBox.getSelectionModel().selectFirst();
                        // loadFuncionesEnCartelera will be called by the listener below
                    }
                } else {
                    cineComboBox.setPromptText("No hay cines disponibles");
                }
            });
        }).start();

        cineComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) { // No need to check oldVal if you always want to reload on new selection
                loadFuncionesEnCartelera(newVal.getIdCine());
            }
        });
    }

    private void loadFuncionesEnCartelera(int idCine) {
        new Thread(() -> {
            // This call should ideally return functions for movies that are 'En Cartelera' or 'Estreno'
            // and sorted in a way that the first few are good representatives if you need to pick one per movie.
            List<FuncionDetallada> todasLasFuncionesEnCartelera = funcionDAO.findFuncionesDetalladasByCineId(idCine);

            Platform.runLater(() -> {
                enCarteleraPane.getChildren().clear();

                if (todasLasFuncionesEnCartelera == null || todasLasFuncionesEnCartelera.isEmpty()) {
                    Label noFuncionesLabel = new Label("No hay funciones en cartelera para este cine en este momento.");
                    noFuncionesLabel.getStyleClass().add("text-body");
                    enCarteleraPane.getChildren().add(noFuncionesLabel);
                    return;
                }

                List<FuncionDetallada> funcionesParaMostrar = new ArrayList<>();
                Set<Integer> idPeliculasMostradas = new HashSet<>(); // To keep track of unique movie IDs shown
                final int MAX_PELICULAS_A_MOSTRAR = 4;

                for (FuncionDetallada funcion : todasLasFuncionesEnCartelera) {
                    // Assumes FuncionDetallada has getIdPelicula() method
                    if (!idPeliculasMostradas.contains(funcion.getIdPelicula())) {
                        if (funcionesParaMostrar.size() < MAX_PELICULAS_A_MOSTRAR) {
                            funcionesParaMostrar.add(funcion);
                            idPeliculasMostradas.add(funcion.getIdPelicula());
                        } else {
                            break; // We have found 4 unique movies
                        }
                    }
                }

                if (funcionesParaMostrar.isEmpty()) {
                    // This case should be rare if 'todasLasFuncionesEnCartelera' was not empty
                    // and contained at least one movie.
                    Label noPeliculasLabel = new Label("No se encontraron películas únicas en cartelera para mostrar.");
                    noPeliculasLabel.getStyleClass().add("text-body");
                    enCarteleraPane.getChildren().add(noPeliculasLabel);
                    return;
                }

                for (FuncionDetallada funcion : funcionesParaMostrar) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml"));
                        Node cardNode = loader.load();
                        CardMovieViewController controller = loader.getController();
                        controller.setFuncionData(funcion);
                        controller.initContext(enCarteleraPane, overlayPane, rootStack, dialogHelper);
                        enCarteleraPane.getChildren().add(cardNode);
                    } catch (IOException e) {
                        System.err.println("Error al cargar la tarjeta para la función '" + funcion.getTituloPelicula() + "': " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }).start();
    }

    private void loadProximamenteCards() {
        proximamentePane.getChildren().clear();
        // TODO: Implement actual data loading for "Próximamente" movies.
        // You would need a DAO method like:
        // List<FuncionDetallada> proximasFunciones = funcionDAO.findFuncionesProximamente(cineComboBox.getValue().getIdCine(), 4); // Example: 4 upcoming movies
        // Then apply similar logic as in loadFuncionesEnCartelera to show unique upcoming movies.

        List<FuncionDetallada> proximamenteFunciones = new ArrayList<>(); // Placeholder

        if (proximamenteFunciones.isEmpty()) {
            Label noProximamenteLabel = new Label("No hay estrenos confirmados próximamente.");
            noProximamenteLabel.getStyleClass().add("text-body");
            proximamentePane.getChildren().add(noProximamenteLabel);
            return;
        }

        // Similar loop to loadProximamenteCards, ensuring unique movies if necessary
        Set<Integer> idPeliculasProximasMostradas = new HashSet<>();
        final int MAX_PROXIMAS_PELICULAS_A_MOSTRAR = 4; // Or however many you want

        for (FuncionDetallada funcion : proximamenteFunciones) {
            if (!idPeliculasProximasMostradas.contains(funcion.getIdPelicula())) {
                if (idPeliculasProximasMostradas.size() < MAX_PROXIMAS_PELICULAS_A_MOSTRAR) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml"));
                        Node cardNode = loader.load();
                        CardMovieViewController controller = loader.getController();
                        controller.setFuncionData(funcion);
                        controller.initContext(proximamentePane, overlayPane, rootStack, dialogHelper);
                        proximamentePane.getChildren().add(cardNode);
                        idPeliculasProximasMostradas.add(funcion.getIdPelicula());
                    } catch (IOException e) {
                        System.err.println("Error al cargar la tarjeta 'Próximamente' para '" + funcion.getTituloPelicula() + "': " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }
}