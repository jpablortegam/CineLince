package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.ActorPeliculaDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.utils.Animations.CardAnimationHelper;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
import com.example.cinelinces.utils.Animations.OverlayHelper;
import javafx.animation.ParallelTransition;
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
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CardMovieViewController {

    // --- Root and Layouts ---
    @FXML private StackPane cardRoot;
    @FXML private VBox compactLayout;
    @FXML private HBox expandedLayout;

    // --- Compact View Elements ---
    @FXML private ImageView poster;
    @FXML private StackPane ratingBadge;
    @FXML private Label ratingLabel;
    @FXML private VBox textContainer;
    @FXML private Label title;
    @FXML private Label subtitle;
    @FXML private HBox badgesContainer;
    @FXML private Label formatBadge;
    @FXML private Label genreBadge;

    // --- Expanded View Elements ---
    @FXML private ImageView posterExpanded;
    @FXML private Label ratingLabelExpanded;
    @FXML private Label titleExpanded;
    @FXML private Label iconDuration;
    @FXML private Label iconYear;
    @FXML private Label iconGenre;
    @FXML private Label synopsisExpanded;
    @FXML private Label castLabelExpanded;
    @FXML private Button btnShowTimes;

    // --- NUEVAS LABELS FXML ---
    @FXML private Label classificationLabel;
    @FXML private Label directorLabel;
    @FXML private Label studioLabel;
    @FXML private Label languageLabel;
    // --- FIN NUEVAS LABELS FXML ---


    // --- Controller Logic Fields (resto del código sin cambios) ---
    private Pane parentContainer;
    private Pane overlayPane;
    private OverlayHelper overlayHelper;
    private DialogAnimationHelper dialogAnimationHelper;
    private Region placeholder;
    private boolean isExpanded = false, isAnimating = false;
    private static CardMovieViewController currentlyExpanded = null;
    private Image moviePosterImage;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm 'hrs'", Locale.getDefault());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault());

    // initContext, onCardHover, onCardHoverExit, onCardClick, expandCard, collapseCard (sin cambios)
    public void initContext(Pane parentContainer, Pane overlayPane, StackPane rootStack, DialogAnimationHelper dialogAnimationHelper) {
        this.parentContainer = parentContainer;
        this.overlayPane = overlayPane;
        this.overlayHelper = new OverlayHelper(overlayPane);
        this.dialogAnimationHelper = dialogAnimationHelper;

        this.overlayHelper.getOverlay().setOnMouseClicked(e -> {
            if (isExpanded && !isAnimating && currentlyExpanded == this) {
                if (e.getTarget() == overlayHelper.getOverlay()) {
                    onCardClick(null);
                }
            }
        });
        cardRoot.setEffect(CardAnimationHelper.SUBTLE_SHADOW_EFFECT);
    }

    @FXML
    void onCardHover(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        CardAnimationHelper.createHoverInAnimation(cardRoot).play();
    }

    @FXML
    void onCardHoverExit(MouseEvent e) {
        if (isExpanded || isAnimating || (currentlyExpanded != null && currentlyExpanded != this)) return;
        CardAnimationHelper.createHoverOutAnimation(cardRoot).play();
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
                currentParentPane.getChildren().add(placeholder);
            }
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

        compactLayout.setVisible(false);
        compactLayout.setManaged(false);
        expandedLayout.setVisible(true);
        expandedLayout.setManaged(true);

        double cardOriginalWidth = pWidth;
        double cardOriginalHeight = pHeight;
        if (cardOriginalWidth <= 0) cardOriginalWidth = cardRoot.getPrefWidth();
        if (cardOriginalHeight <= 0) cardOriginalHeight = cardRoot.getPrefHeight();
        if (cardOriginalWidth <= 0) cardOriginalWidth = 340;
        if (cardOriginalHeight <= 0) cardOriginalHeight = 220;

        double targetScaleXFactor = 3.7;
        double targetScaleYFactor = 2.7;

        double finalTargetLayoutX = (overlayPane.getWidth() - cardOriginalWidth) / 2.0;
        double finalTargetLayoutY = (overlayPane.getHeight() - cardOriginalHeight) / 2.0;
        double targetTranslateX = finalTargetLayoutX - cardRoot.getLayoutX();
        double targetTranslateY = finalTargetLayoutY - cardRoot.getLayoutY();

        dialogAnimationHelper.blurBackgroundIn(CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 8).play();
        overlayHelper.show(CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 0.4, CardAnimationHelper.EASE_OUT_INTERPOLATOR);

        cardRoot.toFront();
        cardRoot.setCache(true);
        cardRoot.setCacheHint(CacheHint.SPEED);

        ParallelTransition expandAnimation = CardAnimationHelper.createExpandAnimation(
                cardRoot, targetTranslateX, targetTranslateY, targetScaleXFactor, targetScaleYFactor
        );

        expandAnimation.setOnFinished(event -> {
            cardRoot.setCache(false);
            isAnimating = false;
            isExpanded = true;
            cardRoot.setOnMouseEntered(null);
            cardRoot.setOnMouseExited(null);
        });
        expandAnimation.play();
    }

    private void collapseCard() {
        cardRoot.getStyleClass().remove("expanded");
        cardRoot.setCache(true);
        cardRoot.setCacheHint(CacheHint.SPEED);

        overlayHelper.hide(CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8), CardAnimationHelper.EASE_OUT_INTERPOLATOR, () -> {});
        dialogAnimationHelper.blurBackgroundOut(CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8)).play();

        Runnable originalOnCompleted = () -> {
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
        };

        ParallelTransition actualCollapseAnimation = CardAnimationHelper.createCollapseAnimation(
                cardRoot, overlayPane, parentContainer, placeholder,
                () -> {
                    cardRoot.setCache(false);
                    originalOnCompleted.run();
                }
        );
        actualCollapseAnimation.play();
    }


    public void setFuncionData(FuncionDetallada funcion) {
        // --- Carga de imagen (sin cambios) ---
        moviePosterImage = null;
        if (funcion.getFotografiaPelicula() != null && !funcion.getFotografiaPelicula().isEmpty()) {
            String imagePath = funcion.getFotografiaPelicula();
            if (!imagePath.startsWith("http") && !imagePath.startsWith("file:") && !imagePath.startsWith("jar:") && !imagePath.startsWith("/")) {
                imagePath = "/com/example/images/" + imagePath;
            }
            try {
                if (imagePath.startsWith("http") || imagePath.startsWith("file:")) {
                    moviePosterImage = new Image(imagePath, true);
                } else {
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

        if (moviePosterImage == null || moviePosterImage.isError()) {
            if (moviePosterImage != null && moviePosterImage.getException() != null) {
                System.err.println("Image loading exception for " + funcion.getTituloPelicula() + ": " + moviePosterImage.getException().getMessage());
            }
            System.err.println("Failed to load image or image path was null for: " + funcion.getTituloPelicula() + ". Loading placeholder.");
            loadPlaceholderPoster();
        }

        // --- Setear datos compactos (sin cambios) ---
        poster.setImage(moviePosterImage);
        title.setText(funcion.getTituloPelicula());
        subtitle.setText(funcion.getFechaHoraFuncion().format(TIME_FORMATTER) + " • Sala " + funcion.getNumeroSala() + " (" + funcion.getTipoSala() + ")");

        // --- Setear datos expandidos (MODIFICADO) ---
        posterExpanded.setImage(moviePosterImage);
        ratingLabelExpanded.setText("★ N/A"); // O el rating real si lo tienes
        titleExpanded.setText(funcion.getTituloPelicula());
        iconDuration.setText("⏱️ " + funcion.getDuracionMinutos() + " min");

        String year = "N/A";
        if (funcion.getFechaEstrenoPelicula() != null) {
            year = String.valueOf(funcion.getFechaEstrenoPelicula().getYear());
        }
        iconYear.setText("📅 " + year);
        iconGenre.setText("🎭 " + (funcion.getNombreTipoPelicula() != null ? funcion.getNombreTipoPelicula() : "Desconocido"));

        // Clasificación
        classificationLabel.setText("📊 " + (funcion.getClasificacionPelicula() != null ? funcion.getClasificacionPelicula() : "N/A"));

        synopsisExpanded.setText(funcion.getSinopsisPelicula() != null ? funcion.getSinopsisPelicula() : "Sinopsis no disponible.");

        // Reparto
        List<ActorPeliculaDTO> actores = funcion.getActores();
        if (actores != null && !actores.isEmpty()) {
            String repartoStr = actores.stream()
                    .map(actor -> actor.getNombreActor() + " (" + actor.getPersonaje() + ")")
                    .collect(Collectors.joining(", "));
            castLabelExpanded.setText("⭐ Reparto: " + repartoStr);
        } else {
            castLabelExpanded.setText("⭐ Reparto no disponible.");
        }

        // Director
        directorLabel.setText("🎬 Director: " + (funcion.getNombreDirector() != null ? funcion.getNombreDirector() : "No disponible"));

        // Estudio
        studioLabel.setText("🏢 Estudio: " + (funcion.getNombreEstudio() != null ? funcion.getNombreEstudio() : "No disponible"));

        // Idioma y Subtítulos
        String idiomaTexto = "🗣️ Idioma: " + (funcion.getIdiomaPelicula() != null ? funcion.getIdiomaPelicula() : "No disponible");
        if (funcion.getIdiomaPelicula() != null) { // Solo mostrar info de subtítulos si hay idioma
            idiomaTexto += (funcion.isSubtituladaPelicula() ? " (Subtitulada)" : " (Doblada)");
        }
        languageLabel.setText(idiomaTexto);
    }

    private void loadPlaceholderPoster() {
        String placeholderPath = "/com/example/images/placeholder_poster.png";
        try {
            InputStream placeholderStream = getClass().getResourceAsStream(placeholderPath);
            if (placeholderStream != null) {
                moviePosterImage = new Image(placeholderStream);
            } else {
                System.err.println("Placeholder image not found at: " + placeholderPath);
                moviePosterImage = null;
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image: " + e.getMessage());
            moviePosterImage = null;
        }
    }

    @FXML private void handleShowTimes() {
        System.out.println("Ver horarios para: " + title.getText());
    }
}