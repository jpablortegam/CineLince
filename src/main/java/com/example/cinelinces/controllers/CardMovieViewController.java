package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.ActorPeliculaDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.utils.Animations.CardAnimationHelper;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
import com.example.cinelinces.utils.Animations.OverlayHelper;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CardMovieViewController {

    @FXML private StackPane cardRoot;
    @FXML private VBox compactLayout;
    @FXML private HBox expandedLayout;
    @FXML private ImageView poster;
    @FXML private StackPane ratingBadge; // Este es el de la vista COMPACTA
    @FXML private Label ratingLabel;     // Este es el de la vista COMPACTA
    @FXML private VBox textContainer;
    @FXML private Label title;
    @FXML private Label subtitle;
    @FXML private HBox badgesContainer;
    @FXML private Label formatBadge;
    @FXML private Label genreBadge;
    @FXML private ImageView posterExpanded;
    @FXML private Label ratingLabelExpanded; // Este es el de la vista EXPANDIDA
    @FXML private Label titleExpanded;
    @FXML private Label iconDuration;
    @FXML private Label iconYear;
    @FXML private Label iconGenre;
    @FXML private Label synopsisExpanded;
    @FXML private Label castLabelExpanded;
    @FXML private Button btnShowTimes;
    @FXML private Label classificationLabel;
    @FXML private Label directorLabel;
    @FXML private Label studioLabel;
    @FXML private Label languageLabel;

    private Pane parentContainer;
    private Pane overlayPane;
    private OverlayHelper overlayHelper;
    private DialogAnimationHelper dialogAnimationHelper;
    private Region placeholder;
    private boolean isExpanded = false, isAnimating = false;
    private static CardMovieViewController currentlyExpanded = null;
    private Image moviePosterImage;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm 'hrs'", Locale.getDefault());
    private static final String FULL_STAR = "★";
    private static final String HALF_STAR = "✬";
    private static final String EMPTY_STAR = "☆";


    public void initContext(Pane parentContainer, Pane overlayPane, StackPane rootStack, DialogAnimationHelper dialogAnimationHelper) {
        this.parentContainer = parentContainer;
        this.overlayPane = overlayPane;
        this.overlayHelper = new OverlayHelper(this.overlayPane);
        this.dialogAnimationHelper = dialogAnimationHelper;

        if (this.overlayHelper != null && this.overlayHelper.getOverlay() != null) {
            this.overlayHelper.getOverlay().setMouseTransparent(true);
        }
        if (CardAnimationHelper.SUBTLE_SHADOW_EFFECT != null) {
            cardRoot.setEffect(CardAnimationHelper.SUBTLE_SHADOW_EFFECT);
        } else {
            System.err.println("Advertencia: CardMovieViewController - CardAnimationHelper.SUBTLE_SHADOW_EFFECT es null.");
        }
    }

    @FXML
    void onCardHover(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        Timeline hoverIn = CardAnimationHelper.createHoverInAnimation(cardRoot);
        if (hoverIn != null) hoverIn.play();
    }

    @FXML
    void onCardHoverExit(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        Timeline hoverOut = CardAnimationHelper.createHoverOutAnimation(cardRoot);
        if (hoverOut != null) hoverOut.play();
    }

    @FXML
    void onCardClick(MouseEvent e) {
        if (isAnimating) return;
        if (!isExpanded && currentlyExpanded != null && currentlyExpanded != this) {
            return;
        }
        isAnimating = true;
        if (!isExpanded) {
            expandCard();
        } else {
            collapseCard();
        }
    }

    private void expandCard() {
        currentlyExpanded = this;
        cardRoot.getStyleClass().add("expanded");

        Bounds cardBoundsInParent = cardRoot.getBoundsInParent();
        Point2D cardPositionInScene = cardRoot.localToScene(0, 0);

        placeholder = new Region();
        double pWidth = cardRoot.getWidth() > 0 ? cardRoot.getWidth() : cardRoot.getPrefWidth();
        double pHeight = cardRoot.getHeight() > 0 ? cardRoot.getHeight() : cardRoot.getPrefHeight();
        placeholder.setPrefSize(pWidth, pHeight);
        placeholder.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        placeholder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        if (cardRoot.getParent() instanceof Pane currentParentPane) {
            int idx = currentParentPane.getChildren().indexOf(cardRoot);
            if (idx != -1) {
                currentParentPane.getChildren().set(idx, placeholder);
            } else {
                currentParentPane.getChildren().add(placeholder); // Fallback, should not happen if card is already in parent
            }
        } else if (parentContainer != null) { // Should ideally not reach here if structure is consistent
            parentContainer.getChildren().remove(cardRoot);
        }


        Point2D cardPositionInOverlay = overlayPane.sceneToLocal(cardPositionInScene);
        cardRoot.setLayoutX(cardPositionInOverlay.getX());
        cardRoot.setLayoutY(cardPositionInOverlay.getY());

        cardRoot.setTranslateX(0);
        cardRoot.setTranslateY(0);
        cardRoot.setScaleX(1.0);
        cardRoot.setScaleY(1.0);
        cardRoot.setOpacity(0.0);

        if (!overlayPane.getChildren().contains(cardRoot)) {
            overlayPane.getChildren().add(cardRoot);
        }
        cardRoot.toFront();

        compactLayout.setVisible(false);
        compactLayout.setManaged(false);
        expandedLayout.setVisible(true);
        expandedLayout.setManaged(true);

        double cardOriginalWidth = pWidth > 0 ? pWidth : cardRoot.getPrefWidth();
        double cardOriginalHeight = pHeight > 0 ? pHeight : cardRoot.getPrefHeight();
        if (cardOriginalWidth <= 0) cardOriginalWidth = 340; // Fallback from FXML prefWidth
        if (cardOriginalHeight <= 0) cardOriginalHeight = 220; // Fallback from FXML prefHeight

        // --- MODIFICADO: Factores de escala para hacer la tarjeta más ancha ---
        double targetScaleXFactor = 3.4; // Aumentado de 2.9
        double targetScaleYFactor = 2.5; // Ligeramente aumentado de 2.4

        double finalTargetLayoutX = (overlayPane.getWidth() - cardOriginalWidth) / 2.0;
        double finalTargetLayoutY = (overlayPane.getHeight() - cardOriginalHeight) / 2.0;
        double targetTranslateX = finalTargetLayoutX - cardRoot.getLayoutX();
        double targetTranslateY = finalTargetLayoutY - cardRoot.getLayoutY();

        if (this.dialogAnimationHelper != null) {
            this.dialogAnimationHelper.blurBackgroundIn(CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 8).play();
        } else {
            System.err.println("CardMovieViewController: dialogAnimationHelper es null en expandCard. No se aplicará blur.");
        }

        cardRoot.setCache(true);
        cardRoot.setCacheHint(CacheHint.SPEED);

        ParallelTransition expandAnimation = CardAnimationHelper.createExpandAnimation(
                cardRoot, targetTranslateX, targetTranslateY, targetScaleXFactor, targetScaleYFactor
        );

        expandAnimation.setOnFinished(event -> {
            cardRoot.setCache(false);
            cardRoot.setOpacity(1.0);
            isAnimating = false;
            isExpanded = true;
            cardRoot.setOnMouseEntered(null);
            cardRoot.setOnMouseExited(null);
        });
        expandAnimation.play();
    }

    private void collapseCard() {
        cardRoot.getStyleClass().remove("expanded");

        if (this.dialogAnimationHelper != null) {
            this.dialogAnimationHelper.blurBackgroundOut(CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8)).play();
        }

        cardRoot.setCache(true);
        cardRoot.setCacheHint(CacheHint.SPEED);

        Runnable onAllOperationsCompleted = () -> {
            this.placeholder = null;
            CardMovieViewController.currentlyExpanded = null;
            this.isAnimating = false;
            this.isExpanded = false;

            expandedLayout.setVisible(false);
            expandedLayout.setManaged(false);
            compactLayout.setVisible(true);
            compactLayout.setManaged(true);

            cardRoot.setOnMouseEntered(this::onCardHover);
            cardRoot.setOnMouseExited(this::onCardHoverExit);
            if (CardAnimationHelper.SUBTLE_SHADOW_EFFECT != null) {
                cardRoot.setEffect(CardAnimationHelper.SUBTLE_SHADOW_EFFECT);
            }
        };

        ParallelTransition actualCollapseAnimation = CardAnimationHelper.createCollapseAnimation(
                cardRoot, overlayPane, parentContainer, placeholder,
                () -> {
                    cardRoot.setCache(false);
                    onAllOperationsCompleted.run();
                }
        );
        actualCollapseAnimation.play();
    }

    private String formatTextWithLineBreaks(String text, int wordsPerLine) {
        if (text == null || text.trim().isEmpty() || wordsPerLine <= 0) {
            return text == null ? "" : text;
        }
        String[] words = text.trim().split("\\s+");
        if (words.length <= wordsPerLine && !text.contains("\n")) { // Evita saltos innecesarios
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

    private String formatRatingToStars(double averageRating, int totalReviews) {
        if (totalReviews == 0) {
            return "N/A";
        }
        double rating = Math.round(averageRating * 2.0) / 2.0; // Redondea a 0.5 más cercano
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


    public void setFuncionData(FuncionDetallada funcion) {
        moviePosterImage = null; // Resetear por si acaso
        if (funcion.getFotografiaPelicula() != null && !funcion.getFotografiaPelicula().isEmpty()) {
            String imagePath = funcion.getFotografiaPelicula();
            // Normalizar la ruta si es relativa
            if (!imagePath.startsWith("http") && !imagePath.startsWith("file:") && !imagePath.startsWith("jar:") && !imagePath.startsWith("/")) {
                imagePath = "/com/example/images/" + imagePath; // Asume una ruta base en resources
            }
            try {
                if (imagePath.startsWith("http") || imagePath.startsWith("file:")) {
                    moviePosterImage = new Image(imagePath, true); // Carga en segundo plano para URLs
                } else { // Carga desde resources
                    InputStream imageStream = getClass().getResourceAsStream(imagePath);
                    if (imageStream != null) {
                        moviePosterImage = new Image(imageStream);
                    } else {
                        System.err.println("Image stream is null for: " + imagePath);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            }
        }

        // Manejo de imagen nula o con error
        if (moviePosterImage == null || moviePosterImage.isError()) {
            if (moviePosterImage != null && moviePosterImage.getException() != null) {
                System.err.println("Image loading exception for " + funcion.getTituloPelicula() + ": " + moviePosterImage.getException().getMessage());
            }
            System.err.println("Failed to load image or image path was null for: " + funcion.getTituloPelicula() + ". Loading placeholder.");
            loadPlaceholderPoster(); // Carga una imagen por defecto
        }

        poster.setImage(moviePosterImage);
        title.setText(funcion.getTituloPelicula());
        if (funcion.getFechaHoraFuncion() != null && funcion.getTipoSala() != null) {
            subtitle.setText(funcion.getFechaHoraFuncion().format(TIME_FORMATTER) + " • Sala " + funcion.getNumeroSala() + " (" + funcion.getTipoSala() + ")");
        } else {
            subtitle.setText("Función no disponible");
        }

        if (ratingBadge != null) {
            ratingBadge.setVisible(false);
            ratingBadge.setManaged(false);
        }

        String starsRatingExpanded = formatRatingToStars(funcion.getCalificacionPromedioPelicula(), funcion.getTotalCalificacionesPelicula());
        if (ratingLabelExpanded != null) {
            ratingLabelExpanded.setText(starsRatingExpanded);
        }

        posterExpanded.setImage(moviePosterImage);
        titleExpanded.setText(funcion.getTituloPelicula());
        iconDuration.setText("⏱️ " + funcion.getDuracionMinutos() + " min");

        String year = "N/A";
        if (funcion.getFechaEstrenoPelicula() != null) {
            year = String.valueOf(funcion.getFechaEstrenoPelicula().getYear());
        }
        iconYear.setText("📅 " + year);
        iconGenre.setText("🎭 " + (funcion.getNombreTipoPelicula() != null ? funcion.getNombreTipoPelicula() : "Desconocido"));

        if (classificationLabel != null) {
            classificationLabel.setText("📊 " + (funcion.getClasificacionPelicula() != null ? funcion.getClasificacionPelicula() : "N/A"));
        }

        String originalSynopsis = funcion.getSinopsisPelicula() != null ? funcion.getSinopsisPelicula() : "Sinopsis no disponible.";
        // --- MODIFICADO: Ajuste de palabras por línea ---
        String formattedSynopsis = formatTextWithLineBreaks(originalSynopsis, 13); // Aumentado de 10
        if (synopsisExpanded != null) {
            synopsisExpanded.setText(formattedSynopsis);
        }

        List<ActorPeliculaDTO> actores = funcion.getActores();
        if (actores != null && !actores.isEmpty()) {
            String rawRepartoStr = actores.stream()
                    .map(actor -> actor.getNombreActor() + " (" + actor.getPersonaje() + ")")
                    .collect(Collectors.joining(", "));
            // --- MODIFICADO: Ajuste de palabras por línea ---
            String formattedRepartoStr = formatTextWithLineBreaks(rawRepartoStr, 13); // Aumentado de 10
            if (castLabelExpanded != null) {
                castLabelExpanded.setText("⭐ Reparto: " + formattedRepartoStr);
            }
        } else {
            if (castLabelExpanded != null) {
                castLabelExpanded.setText("⭐ Reparto no disponible.");
            }
        }

        if (directorLabel != null) {
            directorLabel.setText("🎬 Director: " + (funcion.getNombreDirector() != null ? funcion.getNombreDirector() : "No disponible"));
        }

        if (studioLabel != null) {
            studioLabel.setText("🏢 Estudio: " + (funcion.getNombreEstudio() != null ? funcion.getNombreEstudio() : "No disponible"));
        }

        String idiomaTexto = "🗣️ Idioma: " + (funcion.getIdiomaPelicula() != null ? funcion.getIdiomaPelicula() : "No disponible");
        if (funcion.getIdiomaPelicula() != null) {
            idiomaTexto += (funcion.isSubtituladaPelicula() ? " (Subtitulada)" : " (Doblada)");
        }
        if (languageLabel != null) {
            languageLabel.setText(idiomaTexto);
        }
    }

    private void loadPlaceholderPoster() {
        String placeholderPath = "/com/example/images/placeholder_poster.png";
        try {
            InputStream placeholderStream = getClass().getResourceAsStream(placeholderPath);
            if (placeholderStream != null) {
                moviePosterImage = new Image(placeholderStream);
            } else {
                System.err.println("Placeholder image not found at: " + placeholderPath + ". Ensure it is in the correct resources path.");
                // Consider creating a simple colored rectangle image programmatically as an ultimate fallback if needed.
                moviePosterImage = null; // Explicitly null if not found
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image: " + e.getMessage());
            moviePosterImage = null;
        }
    }

    @FXML private void handleShowTimes() {
        System.out.println("Ver horarios para: " + (title != null ? title.getText() : "Película desconocida"));
        // Aquí iría la lógica para mostrar horarios, posiblemente usando el dialogAnimationHelper
    }
}