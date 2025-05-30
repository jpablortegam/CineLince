package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.CineDAO;
import com.example.cinelinces.DAO.FuncionDAO;
import com.example.cinelinces.DAO.impl.CineDAOImpl;
import com.example.cinelinces.DAO.impl.FuncionDAOImpl;
import com.example.cinelinces.model.Cine;
import com.example.cinelinces.model.DTO.FuncionDetallada;
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
import java.util.Comparator; // Importar Comparator
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class HomeViewController implements Initializable {
    @FXML
    private StackPane rootStack;
    @FXML
    private Pane overlayPane;
    @FXML
    private FlowPane enCarteleraPane;
    @FXML
    private FlowPane proximamentePane;
    @FXML
    private ComboBox<Cine> cineComboBox;

    @FXML
    private StackPane detailedFeaturedBannerPane;

    @FXML
    private ImageView featuredPosterImage;
    @FXML
    private Label featuredRatingBadge;
    @FXML
    private Label featuredMovieTitleText;
    @FXML
    private Label featuredGenreText;
    @FXML
    private Label featuredDurationText;
    @FXML
    private Label featuredYearText;
    @FXML
    private Label featuredSynopsisText;
    @FXML
    private Button btnFeaturedVerHorarios;
    private DialogPaneViewController dialogPaneController;
    @FXML
    private Button btnVerTodasProximamente;

    private CineDAO cineDAO;
    private FuncionDAO funcionDAO;
    private DialogAnimationHelper dialogHelper;
    private Node dialogPanel;

    // Constantes para las estrellas (igual que en CardMovieViewController)
    private static final String FULL_STAR = "★";
    private static final String HALF_STAR = "✬";
    private static final String EMPTY_STAR = "☆";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cineDAO = new CineDAOImpl();
        funcionDAO = new FuncionDAOImpl();
        dialogHelper = new DialogAnimationHelper(rootStack, overlayPane);

        setupDialogPane();
        setupCineComboBox();
        loadProximamenteCards();
        clearDetailedBanner();
    }

    private void setupDialogPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/dialogPane-view.fxml"));
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
                closeBtn.setOnAction(e -> dialogHelper.hideDialog(dialogPanel, btnFeaturedVerHorarios));
            }

            if (btnFeaturedVerHorarios != null) {
                ButtonHoverAnimator.applyHoverEffect(btnFeaturedVerHorarios);
                btnFeaturedVerHorarios.setOnAction(e -> {
                    Object data = btnFeaturedVerHorarios.getUserData();
                    if (data instanceof FuncionDetallada) {
                        dialogPaneController.setMovieContext((FuncionDetallada) data);
                        dialogHelper.showDialog(dialogPanel, btnFeaturedVerHorarios);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                }
            });
        }).start();

        cineComboBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) loadFuncionesEnCartelera(n.getIdCine());
        });
    }

    private void loadFuncionesEnCartelera(int idCine) {
        new Thread(() -> {
            List<FuncionDetallada> funciones = funcionDAO.findFuncionesDetalladasByCineId(idCine);
            Platform.runLater(() -> {
                enCarteleraPane.getChildren().clear();
                if (funciones == null || funciones.isEmpty()) {
                    enCarteleraPane.getChildren().add(new Label("No hay funciones en cartelera."));
                    clearDetailedBanner();
                    return;
                }
                // Film destacada
                FuncionDetallada destacada = funciones.stream()
                        .max(Comparator.comparing(FuncionDetallada::getCalificacionPromedioPelicula)
                                .thenComparing(FuncionDetallada::getTotalCalificacionesPelicula))
                        .orElse(funciones.get(0));
                updateDetailedBanner(destacada);

                // Crear tarjetas
                Set<Integer> seen = new HashSet<>();
                int max = 4;
                for (FuncionDetallada f : funciones) {
                    if (seen.size() >= max) break;
                    if (seen.add(f.getIdPelicula())) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml"));
                            Node card = loader.load();
                            CardMovieViewController cc = loader.getController();
                            cc.initContext(enCarteleraPane, overlayPane, rootStack, dialogHelper, dialogPaneController);
                            cc.setFuncionData(f);
                            enCarteleraPane.getChildren().add(card);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }).start();
    }

    // Método para formatear la calificación a estrellas (similar al de CardMovieViewController)
    private String formatRatingToStars(double averageRating, int totalReviews) {
        if (totalReviews == 0) {
            return "N/A";
        }
        double rating = Math.round(averageRating * 2.0) / 2.0;
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (rating >= i) {
                stars.append(FULL_STAR);
            } else if (rating >= (i - 0.5)) {
                stars.append(HALF_STAR);
            } else {
                stars.append(EMPTY_STAR);
            }
        }
        return stars.toString();
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
        String formattedSynopsisBanner = formatTextWithLineBreaks(originalSynopsis, 15); // Ajusta wordsPerLine según necesites
        if (featuredSynopsisText != null) featuredSynopsisText.setText(formattedSynopsisBanner);

        if (featuredGenreText != null)
            featuredGenreText.setText("🏷️ " + (movie.getNombreTipoPelicula() != null ? movie.getNombreTipoPelicula() : "N/A"));
        if (featuredDurationText != null) featuredDurationText.setText("⏱️ " + movie.getDuracionMinutos() + " min");

        String year = "N/A";
        if (movie.getFechaEstrenoPelicula() != null) {
            year = String.valueOf(movie.getFechaEstrenoPelicula().getYear());
        }
        if (featuredYearText != null) featuredYearText.setText("📅 " + year);

        // --- ACTUALIZAR BADGE DE CALIFICACIÓN CON ESTRELLAS ---
        if (featuredRatingBadge != null) {
            String stars = formatRatingToStars(movie.getCalificacionPromedioPelicula(), movie.getTotalCalificacionesPelicula());
            featuredRatingBadge.setText(stars);
            featuredRatingBadge.setVisible(movie.getTotalCalificacionesPelicula() > 0 || !"N/A".equals(stars));
        }

        // Cargar imagen del póster
        String posterPath = movie.getFotografiaPelicula();
        if (featuredPosterImage != null) {
            loadImageIntoImageView(posterPath, featuredPosterImage, "Banner Poster");
        }


        if (btnFeaturedVerHorarios != null) btnFeaturedVerHorarios.setUserData(movie);
    }

    // Método auxiliar para cargar imágenes
    private Image loadImage(String path, String imageTypeDesc) {
        if (path != null && !path.isEmpty()) {
            String fullPath = path;
            if (!fullPath.startsWith("http") && !fullPath.startsWith("file:") && !fullPath.startsWith("jar:") && !fullPath.startsWith("/")) {
                fullPath = "/com/example/images/" + fullPath;
            }
            try (InputStream stream = getClass().getResourceAsStream(fullPath)) {
                if (stream != null) {
                    return new Image(stream);
                } else {
                    System.err.println(imageTypeDesc + ": No se encontró recurso de imagen en: " + fullPath);
                }
            } catch (Exception e) {
                System.err.println(imageTypeDesc + ": Error al cargar imagen desde stream: " + fullPath + " - " + e.getMessage());
            }
        }
        return null; // Retorna null si no se puede cargar
    }

    // Método auxiliar para cargar imagen en ImageView específico
    private void loadImageIntoImageView(String path, ImageView imageView, String imageTypeDesc) {
        if (imageView == null) return;
        Image image = loadImage(path, imageTypeDesc);
        imageView.setImage(image); // Asigna la imagen (o null si falló la carga)
    }


    private void clearDetailedBanner() {
        if (detailedFeaturedBannerPane != null) detailedFeaturedBannerPane.setVisible(false);
        if (featuredMovieTitleText != null) featuredMovieTitleText.setText("Película Destacada");
        if (featuredSynopsisText != null) featuredSynopsisText.setText("Selecciona un cine para ver detalles.");
        if (featuredPosterImage != null) featuredPosterImage.setImage(null);
        if (featuredGenreText != null) featuredGenreText.setText("🏷️ --");
        if (featuredDurationText != null) featuredDurationText.setText("⏱️ -- min");
        if (featuredYearText != null) featuredYearText.setText("📅 ----");
        if (featuredRatingBadge != null) {
            featuredRatingBadge.setText("N/A");
            featuredRatingBadge.setVisible(false);
        }
        if (btnFeaturedVerHorarios != null) btnFeaturedVerHorarios.setUserData(null);
    }

    private void loadProximamenteCards() {
        proximamentePane.getChildren().clear();
        // Aquí iría la lógica para cargar películas "Próximamente", actualmente está vacía.
        // List<FuncionDetallada> proximamenteFunciones = funcionDAO.findProximamente...(); // Ejemplo
        List<FuncionDetallada> proximamenteFunciones = new ArrayList<>(); // Placeholder

        if (proximamenteFunciones.isEmpty()) {
            Label noProximamenteLabel = new Label("No hay estrenos confirmados próximamente.");
            noProximamenteLabel.getStyleClass().add("text-body");
            proximamentePane.getChildren().add(noProximamenteLabel);
            if (btnVerTodasProximamente != null) btnVerTodasProximamente.setDisable(true);
            return;
        }
        // Lógica para crear tarjetas...
    }
}