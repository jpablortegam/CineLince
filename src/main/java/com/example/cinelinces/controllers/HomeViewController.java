package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.CineDAO;
import com.example.cinelinces.DAO.FuncionDAO;
import com.example.cinelinces.DAO.impl.CineDAOImpl;
import com.example.cinelinces.DAO.impl.FuncionDAOImpl;
import com.example.cinelinces.model.Cine;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.utils.Animations.ButtonHoverAnimator;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper; // Importante que esté
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class HomeViewController implements Initializable {
    @FXML private StackPane rootStack;
    @FXML private Pane overlayPane;
    @FXML private FlowPane enCarteleraPane;
    @FXML private FlowPane proximamentePane;
    @FXML private ComboBox<Cine> cineComboBox;

    @FXML private StackPane detailedFeaturedBannerPane;
    @FXML private ImageView featuredBackgroundImage;
    @FXML private ImageView featuredPosterImage;
    @FXML private Label featuredRatingBadge;
    @FXML private Label featuredMovieTitleText;
    @FXML private Label featuredGenreText;
    @FXML private Label featuredDurationText;
    @FXML private Label featuredYearText;
    @FXML private Label featuredSynopsisText;
    @FXML private Button btnFeaturedVerHorarios;
    @FXML private Button btnFeaturedMiLista;
    @FXML private Button btnFeaturedMasInfo;
    @FXML private Button btnVerTodasProximamente;

    private CineDAO cineDAO;
    private FuncionDAO funcionDAO;
    private DialogAnimationHelper dialogHelper; // Esta es la instancia que se pasará
    private Node dialogPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cineDAO = new CineDAOImpl();
        funcionDAO = new FuncionDAOImpl();

        dialogHelper = new DialogAnimationHelper(rootStack, overlayPane);

        setupCineComboBox();
        loadProximamenteCards();
        setupDialogPane();
        clearDetailedBanner();
    }

    private void setupDialogPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/dialogPane-view.fxml"));
            Node dialogRootNode = loader.load();
            DialogPaneViewController dialogController = loader.getController();
            this.dialogPanel = dialogController.getDialogPanel();

            if (this.dialogPanel == null) {
                System.err.println("Error: HomeViewController - dialogPanel no se pudo cargar desde dialogPane-view.fxml");
                return;
            }

            this.dialogPanel.setVisible(false);
            this.dialogPanel.setOpacity(0);

            if (!overlayPane.getChildren().contains(this.dialogPanel)) {
                overlayPane.getChildren().add(this.dialogPanel);
            }

            Button closeBtn = dialogController.getCloseBtn();
            if (closeBtn != null) {
                ButtonHoverAnimator.applyHoverEffect(closeBtn);
                closeBtn.setOnAction(e -> {
                    if (dialogHelper != null) { // Comprobar si dialogHelper está inicializado
                        dialogHelper.hideDialog(this.dialogPanel, btnFeaturedVerHorarios);
                    }
                });
            }

            if (btnFeaturedVerHorarios != null) {
                ButtonHoverAnimator.applyHoverEffect(btnFeaturedVerHorarios);
                btnFeaturedVerHorarios.setOnAction(e -> {
                    if (this.dialogPanel != null && dialogHelper != null) {
                        Object movieData = btnFeaturedVerHorarios.getUserData();
                        if (movieData instanceof FuncionDetallada) {
                            dialogController.setMovieContext((FuncionDetallada) movieData);
                        } else {
                            dialogController.clearMovieContext();
                        }
                        dialogHelper.showDialog(this.dialogPanel, btnFeaturedVerHorarios);
                    } else {
                        System.err.println("Error: HomeViewController - dialogPanel o dialogHelper no inicializados.");
                    }
                });
            } else {
                System.err.println("HomeViewController: FXML @FXML Injected Button 'btnFeaturedVerHorarios' is null.");
            }

            if (btnFeaturedMiLista != null) {
                ButtonHoverAnimator.applyHoverEffect(btnFeaturedMiLista);
                btnFeaturedMiLista.setOnAction(e -> System.out.println("Botón 'Mi Lista' clickeado"));
            }
            if (btnFeaturedMasInfo != null) {
                ButtonHoverAnimator.applyHoverEffect(btnFeaturedMasInfo);
                btnFeaturedMasInfo.setOnAction(e -> System.out.println("Botón 'Más Info' clickeado"));
            }

        } catch (IOException | NullPointerException e) {
            System.err.println("Error crítico durante la configuración del diálogo en HomeViewController: " + e.getMessage());
            e.printStackTrace();
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
                    }
                } else {
                    cineComboBox.setPromptText("No hay cines disponibles");
                    clearDetailedBanner();
                    enCarteleraPane.getChildren().clear();
                    Label noCinesLabel = new Label("No hay cines disponibles en este momento.");
                    noCinesLabel.getStyleClass().add("text-body");
                    enCarteleraPane.getChildren().add(noCinesLabel);
                }
            });
        }).start();

        cineComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadFuncionesEnCartelera(newVal.getIdCine());
            } else {
                clearDetailedBanner();
                enCarteleraPane.getChildren().clear();
                Label seleccioneCineLabel = new Label("Por favor, seleccione un cine.");
                seleccioneCineLabel.getStyleClass().add("text-body");
                enCarteleraPane.getChildren().add(seleccioneCineLabel);
            }
        });
    }

    private void loadFuncionesEnCartelera(int idCine) {
        new Thread(() -> {
            List<FuncionDetallada> todasLasFuncionesEnCartelera = funcionDAO.findFuncionesDetalladasByCineId(idCine);

            Platform.runLater(() -> {
                enCarteleraPane.getChildren().clear();

                if (todasLasFuncionesEnCartelera == null || todasLasFuncionesEnCartelera.isEmpty()) {
                    Label noFuncionesLabel = new Label("No hay funciones en cartelera para este cine.");
                    noFuncionesLabel.getStyleClass().add("text-body");
                    enCarteleraPane.getChildren().add(noFuncionesLabel);
                    clearDetailedBanner();
                    return;
                }

                if (!todasLasFuncionesEnCartelera.isEmpty()) {
                    FuncionDetallada peliculaDestacada = todasLasFuncionesEnCartelera.get(0);
                    updateDetailedBanner(peliculaDestacada);
                } else {
                    clearDetailedBanner();
                }

                List<FuncionDetallada> funcionesParaMostrar = new ArrayList<>();
                Set<Integer> idPeliculasMostradas = new HashSet<>();
                final int MAX_PELICULAS_A_MOSTRAR = 4;

                for (FuncionDetallada funcion : todasLasFuncionesEnCartelera) {
                    if (!idPeliculasMostradas.contains(funcion.getIdPelicula())) {
                        if (funcionesParaMostrar.size() < MAX_PELICULAS_A_MOSTRAR) {
                            funcionesParaMostrar.add(funcion);
                            idPeliculasMostradas.add(funcion.getIdPelicula());
                        } else {
                            break;
                        }
                    }
                }

                if (funcionesParaMostrar.isEmpty() && !todasLasFuncionesEnCartelera.isEmpty()){
                    // No se hace nada específico aquí por ahora
                } else if (funcionesParaMostrar.isEmpty()){
                    Label noPeliculasLabel = new Label("No se encontraron películas únicas para mostrar en tarjetas.");
                    noPeliculasLabel.getStyleClass().add("text-body");
                    enCarteleraPane.getChildren().add(noPeliculasLabel);
                }

                for (FuncionDetallada funcion : funcionesParaMostrar) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml"));
                        Node cardNode = loader.load();
                        CardMovieViewController controller = loader.getController();
                        controller.setFuncionData(funcion);

                        // ---- LLAMADA A initContext CORREGIDA ----
                        // Pasa this.dialogHelper (la instancia de DialogAnimationHelper de HomeViewController)
                        controller.initContext(enCarteleraPane, overlayPane, rootStack, this.dialogHelper);

                        enCarteleraPane.getChildren().add(cardNode);
                    } catch (IOException e) {
                        System.err.println("Error al cargar la tarjeta para la función '" + funcion.getTituloPelicula() + "': " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }).start();
    }

    private String formatTextWithLineBreaks(String text, int wordsPerLine) {
        if (text == null || text.trim().isEmpty() || wordsPerLine <= 0) {
            return text == null ? "" : text.trim();
        }
        String[] words = text.trim().split("\\s+");
        if (words.length <= wordsPerLine && !text.contains("\n")) {
            return text.trim();
        }
        StringBuilder formattedText = new StringBuilder();
        int wordCountOnCurrentLine = 0;
        for (int i = 0; i < words.length; i++) {
            formattedText.append(words[i]);
            wordCountOnCurrentLine++;
            if (wordCountOnCurrentLine >= wordsPerLine && i < words.length - 1) {
                formattedText.append("\n");
                wordCountOnCurrentLine = 0;
            } else if (i < words.length - 1) {
                formattedText.append(" ");
            }
        }
        return formattedText.toString();
    }

    private void updateDetailedBanner(FuncionDetallada movie) {
        if (movie == null) {
            clearDetailedBanner();
            return;
        }
        if (detailedFeaturedBannerPane == null) {
            System.err.println("HomeViewController: detailedFeaturedBannerPane es null. Revisar FXML.");
            return;
        }

        detailedFeaturedBannerPane.setVisible(true);

        if (featuredMovieTitleText != null) featuredMovieTitleText.setText(movie.getTituloPelicula());

        String originalSynopsis = movie.getSinopsisPelicula();
        String formattedSynopsisBanner = formatTextWithLineBreaks(originalSynopsis, 15);
        if (featuredSynopsisText != null) featuredSynopsisText.setText(formattedSynopsisBanner);

        if (featuredGenreText != null) featuredGenreText.setText("🏷️ " + (movie.getNombreTipoPelicula() != null ? movie.getNombreTipoPelicula() : "N/A"));
        if (featuredDurationText != null) featuredDurationText.setText("⏱️ " + movie.getDuracionMinutos() + " min");

        String year = "N/A";
        if (movie.getFechaEstrenoPelicula() != null) {
            year = String.valueOf(movie.getFechaEstrenoPelicula().getYear());
        }
        if (featuredYearText != null) featuredYearText.setText("📅 " + year);

        if (featuredRatingBadge != null) featuredRatingBadge.setText("★ " + (movie.getClasificacionPelicula() != null ? movie.getClasificacionPelicula() : "N/A"));

        String posterPath = movie.getFotografiaPelicula();
        if (featuredPosterImage != null) {
            if (posterPath != null && !posterPath.isEmpty()) {
                if (!posterPath.startsWith("http") && !posterPath.startsWith("file:") && !posterPath.startsWith("jar:") && !posterPath.startsWith("/")) {
                    posterPath = "/com/example/images/" + posterPath;
                }
                try (InputStream posterStream = getClass().getResourceAsStream(posterPath)) {
                    if (posterStream != null) {
                        featuredPosterImage.setImage(new Image(posterStream));
                    } else {
                        featuredPosterImage.setImage(null);
                        System.err.println("Banner: No se encontró el poster: " + posterPath);
                    }
                } catch (Exception e) {
                    featuredPosterImage.setImage(null);
                    System.err.println("Banner: Error al leer poster stream: " + posterPath + " - " + e.getMessage());
                }
            } else {
                featuredPosterImage.setImage(null);
            }
        }

        String backgroundPath = movie.getFotografiaPelicula();
        if (featuredBackgroundImage != null) {
            if (backgroundPath != null && !backgroundPath.isEmpty()) {
                if (!backgroundPath.startsWith("http") && !backgroundPath.startsWith("file:") && !backgroundPath.startsWith("jar:") && !backgroundPath.startsWith("/")) {
                    backgroundPath = "/com/example/images/" + backgroundPath;
                }
                try (InputStream bgStream = getClass().getResourceAsStream(backgroundPath)) {
                    if (bgStream != null) {
                        featuredBackgroundImage.setImage(new Image(bgStream));
                    } else {
                        featuredBackgroundImage.setImage(null);
                        System.err.println("Banner: No se encontró imagen de fondo: " + backgroundPath);
                    }
                } catch (Exception e) {
                    featuredBackgroundImage.setImage(null);
                    System.err.println("Banner: Error al leer imagen de fondo stream: " + backgroundPath + " - " + e.getMessage());
                }
            } else {
                featuredBackgroundImage.setImage(null);
            }
        }

        if (btnFeaturedVerHorarios != null) btnFeaturedVerHorarios.setUserData(movie);
    }

    private void clearDetailedBanner() {
        if (detailedFeaturedBannerPane != null) detailedFeaturedBannerPane.setVisible(false);
        if (featuredMovieTitleText != null) featuredMovieTitleText.setText("Película Destacada");
        if (featuredSynopsisText != null) featuredSynopsisText.setText("Selecciona un cine para ver detalles.");
        if (featuredPosterImage != null) featuredPosterImage.setImage(null);
        if (featuredBackgroundImage != null) featuredBackgroundImage.setImage(null);
        if (featuredGenreText != null) featuredGenreText.setText("🏷️ --");
        if (featuredDurationText != null) featuredDurationText.setText("⏱️ -- min");
        if (featuredYearText != null) featuredYearText.setText("📅 ----");
        if (featuredRatingBadge != null) featuredRatingBadge.setText("★ N/A");
        if (btnFeaturedVerHorarios != null) btnFeaturedVerHorarios.setUserData(null);
    }

    private void loadProximamenteCards() {
        proximamentePane.getChildren().clear();
        List<FuncionDetallada> proximamenteFunciones = new ArrayList<>();

        if (proximamenteFunciones.isEmpty()) {
            Label noProximamenteLabel = new Label("No hay estrenos confirmados próximamente.");
            noProximamenteLabel.getStyleClass().add("text-body");
            proximamentePane.getChildren().add(noProximamenteLabel);
            if(btnVerTodasProximamente != null) btnVerTodasProximamente.setDisable(true);
            return;
        }
    }
}