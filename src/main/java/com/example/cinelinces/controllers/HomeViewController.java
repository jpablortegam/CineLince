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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
    @FXML
    private Button btnVerTodasProximamente;

    // Additional FXML components you might have but didn't include in the Java part
    // @FXML private Label featuredFormatBadge;
    // @FXML private Label featuredGenreBadge;

    private DialogPaneViewController dialogPaneController;
    private CineDAO cineDAO;
    private FuncionDAO funcionDAO;
    private DialogAnimationHelper dialogHelper;
    private Node dialogPanel;

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
        loadProximamenteCards(); // This method is incomplete in your provided code
        clearDetailedBanner(); // Initial clear of the banner

        // The listener below handles calling loadFuncionesEnCartelera
        // when a new cinema is selected, so the explicit call here is redundant.
        // cineComboBox.setOnAction(e -> {
        //     Cine seleccionado = cineComboBox.getSelectionModel().getSelectedItem();
        //     if (seleccionado != null) {
        //         loadFuncionesEnCartelera(seleccionado.getIdCine());
        //     }
        // });
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
            System.err.println("Error loading dialog-pane-view.fxml: " + e.getMessage());
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
                            return null; // Not needed for a read-only combobox
                        }
                    });
                    // Select the first cinema and trigger loading of functions
                    cineComboBox.getSelectionModel().selectFirst();
                } else {
                    cineComboBox.setPromptText("No hay cines disponibles.");
                }
            });
        }).start();

        cineComboBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                loadFuncionesEnCartelera(n.getIdCine());
            } else {
                // If no cinema is selected (e.g., list becomes empty or selection cleared)
                Platform.runLater(() -> {
                    enCarteleraPane.getChildren().clear();
                    enCarteleraPane.getChildren().add(new Label("Selecciona un cine para ver la cartelera."));
                    clearDetailedBanner();
                });
            }
        });
    }

    private void loadFuncionesEnCartelera(int idCine) {
        // Clear UI elements immediately to provide visual feedback
        Platform.runLater(() -> {
            enCarteleraPane.getChildren().clear();
            enCarteleraPane.getChildren().add(new Label("Cargando cartelera...")); // Placeholder
            clearDetailedBanner();
        });

        new Thread(() -> {
            List<FuncionDetallada> funciones = funcionDAO.findFuncionesDetalladasByCineId(idCine);

            Platform.runLater(() -> {
                enCarteleraPane.getChildren().clear(); // Clear placeholder

                if (funciones == null || funciones.isEmpty()) {
                    enCarteleraPane.getChildren().add(new Label("No hay funciones en cartelera para este cine."));
                    clearDetailedBanner();
                    return;
                }

                // 1. Group by movie (take one representative function per movie)
                Map<Integer, FuncionDetallada> moviesInCinema = new HashMap<>();
                for (FuncionDetallada f : funciones) {
                    moviesInCinema.putIfAbsent(f.getIdPelicula(), f);
                }

                // 2. Find the maximum rating among unique movies in this cinema
                double maxRating = moviesInCinema.values().stream()
                        .mapToDouble(FuncionDetallada::getCalificacionPromedioPelicula)
                        .max()
                        .orElse(-1.0); // Use -1.0 or another distinct value if no ratings exist

                // 3. Filter for movies with that exact maximum rating
                List<FuncionDetallada> topRatedCandidates = moviesInCinema.values().stream()
                        .filter(f -> Double.compare(f.getCalificacionPromedioPelicula(), maxRating) == 0 && maxRating >= 0)
                        .collect(Collectors.toList());

                // 4. Select a featured movie randomly from the top-rated candidates
                if (topRatedCandidates.isEmpty() && !moviesInCinema.isEmpty()) {
                    // Fallback: If no movie has a positive rating, pick any random movie
                    List<FuncionDetallada> allMovies = new ArrayList<>(moviesInCinema.values());
                    FuncionDetallada destacada = allMovies.get((int) (Math.random() * allMovies.size()));
                    updateDetailedBanner(destacada);
                } else if (!topRatedCandidates.isEmpty()) {
                    FuncionDetallada destacada = topRatedCandidates.get((int) (Math.random() * topRatedCandidates.size()));
                    updateDetailedBanner(destacada);
                } else {
                    // No movies at all for this cinema
                    clearDetailedBanner();
                }

                // Display up to 4 different movies in "En cartelera" section
                Set<Integer> seenMovieIds = new HashSet<>();
                int moviesDisplayed = 0;
                final int MAX_DISPLAY_MOVIES = 4;

                for (FuncionDetallada f : funciones) {
                    if (moviesDisplayed >= MAX_DISPLAY_MOVIES) {
                        break;
                    }
                    if (seenMovieIds.add(f.getIdPelicula())) { // Only add if it's a new movie
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml"));
                            Node card = loader.load();
                            CardMovieViewController cc = loader.getController();
                            cc.initContext(enCarteleraPane, overlayPane, rootStack, dialogHelper, dialogPaneController);
                            cc.setFuncionData(f);
                            enCarteleraPane.getChildren().add(card);
                            moviesDisplayed++;
                        } catch (IOException ex) {
                            System.err.println("Error loading cardMovie-view.fxml for movie ID " + f.getIdPelicula() + ": " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }).start();
    }

    private String formatRatingToStars(double averageRating, int totalReviews) {
        if (totalReviews == 0) {
            return "N/A";
        }
        // Round to the nearest half star
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
            System.err.println("HomeViewController: detailedFeaturedBannerPane is null. Check FXML.");
            return;
        }

        detailedFeaturedBannerPane.setVisible(true);

        if (featuredMovieTitleText != null) featuredMovieTitleText.setText(movie.getTituloPelicula());

        String originalSynopsis = movie.getSinopsisPelicula();
        // Limit synopsis length for banner to avoid overflow, adjust 20 words as needed
        String formattedSynopsisBanner = formatTextWithLineBreaks(originalSynopsis, 20);
        if (featuredSynopsisText != null) featuredSynopsisText.setText(formattedSynopsisBanner);

        if (featuredGenreText != null)
            featuredGenreText.setText("🏷️ " + (movie.getNombreTipoPelicula() != null ? movie.getNombreTipoPelicula() : "N/A"));
        if (featuredDurationText != null) featuredDurationText.setText("⏱️ " + movie.getDuracionMinutos() + " min");

        String year = "N/A";
        if (movie.getFechaEstrenoPelicula() != null) {
            year = String.valueOf(movie.getFechaEstrenoPelicula().getYear());
        }
        if (featuredYearText != null) featuredYearText.setText("📅 " + year);

        if (featuredRatingBadge != null) {
            String stars = formatRatingToStars(movie.getCalificacionPromedioPelicula(), movie.getTotalCalificacionesPelicula());
            featuredRatingBadge.setText(stars);
            // Only show rating badge if there are reviews or if it's explicitly set
            featuredRatingBadge.setVisible(movie.getTotalCalificacionesPelicula() > 0 || !"N/A".equals(stars));
        }

        String posterPath = movie.getFotografiaPelicula();
        if (featuredPosterImage != null) {
            loadImageIntoImageView(posterPath, featuredPosterImage, "Featured Banner Poster");
        }
        if (btnFeaturedVerHorarios != null) btnFeaturedVerHorarios.setUserData(movie);
    }

    private Image loadImage(String path, String imageTypeDesc) {
        if (path == null || path.trim().isEmpty()) {
            System.err.println(imageTypeDesc + ": Path is null or empty. Using default/no image.");
            return null; // Return null to indicate no image, or a default image
        }

        String fullPath = path;
        // Check if the path is an absolute URL or a classpath resource
        if (!fullPath.startsWith("http") && !fullPath.startsWith("file:") && !fullPath.startsWith("jar:") && !fullPath.startsWith("/")) {
            // Assume it's a relative path to /com/example/images/ if not absolute
            fullPath = "/com/example/images/" + fullPath;
        }

        try (InputStream stream = getClass().getResourceAsStream(fullPath)) {
            if (stream != null) {
                return new Image(stream);
            } else {
                System.err.println(imageTypeDesc + ": Image resource not found at: " + fullPath);
            }
        } catch (Exception e) {
            System.err.println(imageTypeDesc + ": Error loading image from stream: " + fullPath + " - " + e.getMessage());
            e.printStackTrace(); // Log the stack trace for debugging
        }
        return null;
    }

    private void loadImageIntoImageView(String path, ImageView imageView, String imageTypeDesc) {
        if (imageView == null) {
            System.err.println("ImageView is null for " + imageTypeDesc);
            return;
        }
        Image image = loadImage(path, imageTypeDesc);
        imageView.setImage(image);
    }

    private void clearDetailedBanner() {
        if (detailedFeaturedBannerPane != null) detailedFeaturedBannerPane.setVisible(false);
        if (featuredMovieTitleText != null) featuredMovieTitleText.setText("Película Destacada");
        if (featuredSynopsisText != null) featuredSynopsisText.setText("Selecciona un cine para ver los detalles de la película destacada.");
        if (featuredPosterImage != null) featuredPosterImage.setImage(null);
        if (featuredGenreText != null) featuredGenreText.setText("🏷️ Género");
        if (featuredDurationText != null) featuredDurationText.setText("⏱️ Duración");
        if (featuredYearText != null) featuredYearText.setText("📅 Año");
        if (featuredRatingBadge != null) {
            featuredRatingBadge.setText("★ N/A");
            featuredRatingBadge.setVisible(false);
        }
        if (btnFeaturedVerHorarios != null) {
            btnFeaturedVerHorarios.setUserData(null);
            // btnFeaturedVerHorarios.setDisable(true); // Optional: disable if no movie is selected
        }
        // Make sure to also clear any other dynamic elements if they exist, e.g., badges
        // if (featuredFormatBadge != null) featuredFormatBadge.setVisible(false);
        // if (featuredGenreBadge != null) featuredGenreBadge.setVisible(false);
    }

    private void loadProximamenteCards() {
        // This method was incomplete. You need to implement the logic to fetch "proximamente" movies.
        proximamentePane.getChildren().clear();
        // Placeholder for loading upcoming movies
        // List<FuncionDetallada> proximamenteFunciones = funcionDAO.findProximamenteMovies(); // You'd need a new DAO method for this
        List<FuncionDetallada> proximamenteFunciones = new ArrayList<>(); // Example: empty list for now

        if (proximamenteFunciones.isEmpty()) {
            Label noProximamenteLabel = new Label("No hay estrenos confirmados próximamente.");
            noProximamenteLabel.getStyleClass().add("text-body");
            proximamentePane.getChildren().add(noProximamenteLabel);
            if (btnVerTodasProximamente != null) btnVerTodasProximamente.setDisable(true);
            return;
        }
        // Logic to create cards for "proximamente" movies, similar to "En cartelera"
        // for (FuncionDetallada f : proximamenteFunciones) {
        //     try {
        //         FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml"));
        //         Node card = loader.load();
        //         CardMovieViewController cc = loader.getController();
        //         // You might need a different initContext or pass different params for "proximamente" cards
        //         cc.setFuncionData(f);
        //         proximamentePane.getChildren().add(card);
        //     } catch (IOException ex) {
        //         ex.printStackTrace();
        //     }
        // }
    }
}