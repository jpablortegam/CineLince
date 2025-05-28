package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.utils.Animations.CardAnimationHelper;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
import com.example.cinelinces.utils.Animations.OverlayHelper;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
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
import java.util.Locale;

public class CardMovieViewController {

    // --- Root and Layouts ---
    @FXML private StackPane cardRoot;
    @FXML private VBox compactLayout;
    @FXML private HBox expandedLayout; // Will have styleClass="expanded-content-area" from FXML

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
    @FXML private Button btnMyList;
    @FXML private Button btnMoreInfo;

    // --- Controller Logic Fields ---
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

    public void initContext(Pane parentContainer, Pane overlayPane, StackPane rootStack, DialogAnimationHelper dialogAnimationHelper) {
        this.parentContainer = parentContainer;
        this.overlayPane = overlayPane;
        this.overlayHelper = new OverlayHelper(overlayPane);
        this.dialogAnimationHelper = dialogAnimationHelper;

        this.overlayHelper.getOverlay().setOnMouseClicked(e -> {
            if (isExpanded && !isAnimating && currentlyExpanded == this) {
                onCardClick(null);
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
        // Only play hover out if not expanded to avoid conflict with expanded shadow
        if (!isExpanded) {
            CardAnimationHelper.createHoverOutAnimation(cardRoot).play();
        }
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
        cardRoot.getStyleClass().add("expanded"); // Apply expanded style

        Bounds cardBoundsInParent = cardRoot.getBoundsInParent(); // Get bounds before detaching
        Point2D cardPositionInScene = cardRoot.localToScene(0, 0);

        placeholder = new Region();
        // Use actual card dimensions for placeholder, not prefSize, if available
        double pWidth = cardRoot.getWidth() > 0 ? cardRoot.getWidth() : cardRoot.getPrefWidth();
        double pHeight = cardRoot.getHeight() > 0 ? cardRoot.getHeight() : cardRoot.getPrefHeight();
        placeholder.setPrefSize(pWidth, pHeight);
        placeholder.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE); // Or bind to cardRoot properties
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

        cardRoot.setTranslateX(0); // Resetting transforms before new animation
        cardRoot.setTranslateY(0);
        cardRoot.setScaleX(1.0);
        cardRoot.setScaleY(1.0);
        cardRoot.setOpacity(0.0);

        if (!overlayPane.getChildren().contains(cardRoot)) {
            overlayPane.getChildren().add(cardRoot);
        }
        overlayHelper.getOverlay().toFront();
        cardRoot.toFront();

        compactLayout.setVisible(false);
        compactLayout.setManaged(false);
        expandedLayout.setVisible(true);
        expandedLayout.setManaged(true);

        double cardOriginalWidth = pWidth; // Use the placeholder width (actual card width)
        double cardOriginalHeight = pHeight; // Use the placeholder height (actual card height)

        // Fallback if width/height were 0 (e.g. card not yet rendered)
        if (cardOriginalWidth <= 0) cardOriginalWidth = cardRoot.getPrefWidth();
        if (cardOriginalHeight <= 0) cardOriginalHeight = cardRoot.getPrefHeight();
        if (cardOriginalWidth <=0) cardOriginalWidth = 340; // Valor por defecto si todo falla
        if (cardOriginalHeight <=0) cardOriginalHeight = 220; // Valor por defecto si todo falla


        // --- MODIFICACIÓN CLAVE AQUÍ ---
        double targetScaleXFactor = 2.2; // Aumentado para más ancho
        double targetScaleYFactor = 1.7; // Ajustado para proporción
        // --- FIN DE MODIFICACIÓN CLAVE ---

        // Centering Logic: Calculate translation needed to move the *center* of the unscaled card
        // to the center of the overlayPane. The scaling will then occur around this center.
        double finalTargetLayoutX = (overlayPane.getWidth() - cardOriginalWidth) / 2.0;
        double finalTargetLayoutY = (overlayPane.getHeight() - cardOriginalHeight) / 2.0;

        double targetTranslateX = finalTargetLayoutX - cardRoot.getLayoutX();
        double targetTranslateY = finalTargetLayoutY - cardRoot.getLayoutY();

        dialogAnimationHelper.blurBackgroundIn(CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 8).play();
        overlayHelper.show(CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 0.4, CardAnimationHelper.EASE_OUT_INTERPOLATOR);

        ParallelTransition expandAnimation = CardAnimationHelper.createExpandAnimation(
                cardRoot, targetTranslateX, targetTranslateY, targetScaleXFactor, targetScaleYFactor
        );

        expandAnimation.setOnFinished(event -> {
            isAnimating = false;
            isExpanded = true;
            cardRoot.setOnMouseEntered(null);
            cardRoot.setOnMouseExited(null);
        });
        expandAnimation.play();
    }

    private void collapseCard() {
        cardRoot.getStyleClass().remove("expanded"); // Remove expanded style

        cardRoot.setOnMouseEntered(this::onCardHover);
        cardRoot.setOnMouseExited(this::onCardHoverExit);

        overlayHelper.hide(CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8), CardAnimationHelper.EASE_OUT_INTERPOLATOR, () -> {});
        dialogAnimationHelper.blurBackgroundOut(CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8)).play();

        ParallelTransition collapseAnimation = CardAnimationHelper.createCollapseAnimation(
                cardRoot, overlayPane, parentContainer, placeholder,
                () -> {
                    this.placeholder = null;
                    CardMovieViewController.currentlyExpanded = null;
                    this.isAnimating = false;
                    this.isExpanded = false;

                    expandedLayout.setVisible(false);
                    expandedLayout.setManaged(false);
                    compactLayout.setVisible(true);
                    compactLayout.setManaged(true);

                    // cardRoot opacity and effect are reset by createCollapseAnimation's onFinished
                }
        );
        collapseAnimation.play();
    }

    public void setFuncionData(FuncionDetallada funcion) {
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
                System.err.println("Image loading exception: " + moviePosterImage.getException().getMessage());
            }
            System.err.println("Failed to load image or image path was null for: " + funcion.getTituloPelicula() + ". Loading placeholder.");
            loadPlaceholderPoster();
        }

        poster.setImage(moviePosterImage);
        title.setText(funcion.getTituloPelicula());
        subtitle.setText(funcion.getFechaHoraFuncion().format(TIME_FORMATTER) + " • Sala " + funcion.getNumeroSala() + " (" + funcion.getTipoSala() + ")");

        // --- Populate Expanded View ---
        posterExpanded.setImage(moviePosterImage);
        ratingLabelExpanded.setText("★ N/A"); // Placeholder, update with actual rating if available
        titleExpanded.setText(funcion.getTituloPelicula());
        iconDuration.setText("⏱️ " + funcion.getDuracionMinutos() + " min");
        String year = "N/A";
        if (funcion.getFechaEstrenoPelicula() != null) {
            year = String.valueOf(funcion.getFechaEstrenoPelicula().getYear());
        }
        iconYear.setText("📅 " + year);
        iconGenre.setText("🎭 " + (funcion.getNombreTipoPelicula() != null ? funcion.getNombreTipoPelicula() : "No especificado"));
        synopsisExpanded.setText(funcion.getSinopsisPelicula() != null ? funcion.getSinopsisPelicula() : "Sinopsis no disponible.");
        castLabelExpanded.setText("⭐ Reparto no disponible en esta vista.");
    }

    private void loadPlaceholderPoster() {
        String placeholderPath = "/com/example/images/placeholder_poster.png";
        try {
            InputStream placeholderStream = getClass().getResourceAsStream(placeholderPath);
            if (placeholderStream != null) {
                moviePosterImage = new Image(placeholderStream);
            } else {
                System.err.println("Placeholder image not found at: " + placeholderPath);
                moviePosterImage = null; // Ensure it's null if placeholder fails
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image: " + e.getMessage());
            moviePosterImage = null; // Ensure it's null on exception
        }
    }

    @FXML private void handleShowTimes() { System.out.println("Ver horarios: " + title.getText()); }
    @FXML private void handleMyList() { System.out.println("+ Mi lista: " + title.getText()); }
    @FXML private void handleMoreInfo() { System.out.println("Más info: " + title.getText()); }
}