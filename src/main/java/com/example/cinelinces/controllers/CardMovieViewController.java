package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.utils.Animations.CardAnimationHelper;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;
import com.example.cinelinces.utils.Animations.OverlayHelper;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint; // Importar CacheHint
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
import javafx.application.Platform; // Asegurarse que Platform está importado

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

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
    // @FXML private Button btnMyList; // No usado en el FXML proporcionado
    // @FXML private Button btnMoreInfo; // No usado en el FXML proporcionado

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
        this.overlayHelper = new OverlayHelper(overlayPane); // Asumo que OverlayHelper es tuyo y gestiona el overlayPane
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
        // No es necesario chequear !isExpanded aquí porque la condición de arriba ya lo cubre
        CardAnimationHelper.createHoverOutAnimation(cardRoot).play();
    }

    @FXML
    void onCardClick(MouseEvent e) {
        if (isAnimating) return;
        if (!isExpanded && currentlyExpanded != null && currentlyExpanded != this) {
            // Si otra tarjeta está expandida, no hacer nada con esta.
            // Opcionalmente, podrías colapsar la otra primero.
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
            } else { // Si no estaba, algo raro, pero intentamos añadir el placeholder
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
        cardRoot.setOpacity(0.0); // Iniciar transparente para el fadeIn

        if (!overlayPane.getChildren().contains(cardRoot)) {
            overlayPane.getChildren().add(cardRoot);
        }

        compactLayout.setVisible(false);
        compactLayout.setManaged(false);
        expandedLayout.setVisible(true);
        expandedLayout.setManaged(true);

        double cardOriginalWidth = pWidth;
        double cardOriginalHeight = pHeight;
        // Fallbacks si las dimensiones no son > 0
        if (cardOriginalWidth <= 0) cardOriginalWidth = cardRoot.getPrefWidth();
        if (cardOriginalHeight <= 0) cardOriginalHeight = cardRoot.getPrefHeight();
        if (cardOriginalWidth <= 0) cardOriginalWidth = 340; // Hardcoded fallback
        if (cardOriginalHeight <= 0) cardOriginalHeight = 220; // Hardcoded fallback

        // Ajusta estos factores según el tamaño deseado de la tarjeta expandida
        double targetScaleXFactor = 3.7;
        double targetScaleYFactor = 2.7;

        // Calcular la posición para centrar la tarjeta escalada en el overlayPane
        double finalTargetLayoutX = (overlayPane.getWidth() - cardOriginalWidth) / 2.0;
        double finalTargetLayoutY = (overlayPane.getHeight() - cardOriginalHeight) / 2.0;
        double targetTranslateX = finalTargetLayoutX - cardRoot.getLayoutX();
        double targetTranslateY = finalTargetLayoutY - cardRoot.getLayoutY();

        dialogAnimationHelper.blurBackgroundIn(CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 8).play();
        overlayHelper.show(CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 0.4, CardAnimationHelper.EASE_OUT_INTERPOLATOR);

        cardRoot.toFront();

        // Habilitar cache antes de la animación
        cardRoot.setCache(true);
        cardRoot.setCacheHint(CacheHint.SPEED);

        ParallelTransition expandAnimation = CardAnimationHelper.createExpandAnimation(
                cardRoot, targetTranslateX, targetTranslateY, targetScaleXFactor, targetScaleYFactor
        );

        expandAnimation.setOnFinished(event -> {
            cardRoot.setCache(false); // Deshabilitar cache después de la animación
            isAnimating = false;
            isExpanded = true;
            cardRoot.setOnMouseEntered(null); // Deshabilitar hover en modo expandido
            cardRoot.setOnMouseExited(null);
        });
        expandAnimation.play();
    }

    private void collapseCard() {
        cardRoot.getStyleClass().remove("expanded");

        // Habilitar cache antes de la animación
        cardRoot.setCache(true);
        cardRoot.setCacheHint(CacheHint.SPEED);

        overlayHelper.hide(CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8), CardAnimationHelper.EASE_OUT_INTERPOLATOR, () -> {});
        dialogAnimationHelper.blurBackgroundOut(CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8)).play();

        ParallelTransition collapseAnimation = CardAnimationHelper.createCollapseAnimation(
                cardRoot, overlayPane, parentContainer, placeholder,
                () -> {
                    // Este Runnable se ejecuta después de que la tarjeta se reinserta en el originalParent
                    // y se restauran sus propiedades (dentro del Platform.runLater de createCollapseAnimation).
                    this.placeholder = null;
                    CardMovieViewController.currentlyExpanded = null;
                    this.isAnimating = false;
                    this.isExpanded = false;

                    // Restaurar visibilidad y gestión de layouts
                    expandedLayout.setVisible(false);
                    expandedLayout.setManaged(false);
                    compactLayout.setVisible(true);
                    compactLayout.setManaged(true);

                    // Restaurar handlers de hover
                    cardRoot.setOnMouseEntered(this::onCardHover);
                    cardRoot.setOnMouseExited(this::onCardHoverExit);
                }
        );

        // Mover setCache(false) al onFinished de la ParallelTransition, pero asegurándose que ocurra
        // en el hilo de la UI si es necesario y antes de cualquier operación que pudiera
        // beneficiarse de no tener el cache activo.
        // El onFinished de createCollapseAnimation ya usa Platform.runLater para sus operaciones.
        // Vamos a añadir la desactivación del cache ahí mismo.
        // Para hacer esto, necesitamos que el onAllOperationsCompleted se ejecute *después* de desactivar el cache.
        // La forma más limpia es poner el setCache(false) dentro del onFinished de la PT que se crea.

        collapseAnimation.statusProperty().addListener((obs, oldStatus, newStatus) -> {
            if (newStatus == javafx.animation.Animation.Status.STOPPED) {
                // Se ejecuta después del Platform.runLater interno de createCollapseAnimation
                // ya que el onAllOperationsCompleted se llama al final de ese Platform.runLater.
                // Para asegurar el orden, es mejor ponerlo directamente en el runLater de createCollapseAnimation
                // o asegurar que este runnable se ejecuta después de todo.
                // La modificación más segura es pasar el cardRoot al helper o manejar el cache
                // directamente en el setOnFinished del helper, antes del onAllOperationsCompleted.

                // Por ahora, la forma más fácil es desactivarlo aquí, asumiendo que las
                // operaciones principales del setOnFinished del helper ya finalizaron.
                // Pero el onAllOperationsCompleted se llama *dentro* del Platform.runLater
                // del setOnFinished de la PT.
                // Así que el estado de la tarjeta ya ha sido restaurado.

                // Una solución más robusta: modificar createCollapseAnimation para aceptar un Consumer<Node>
                // para operaciones pre-finalización y otro para post-finalización.
                // O, más simple, el usuario de CardAnimationHelper se encarga del cache.
                // Aquí, lo pondremos en el onAllOperationsCompleted que ya es un Runnable.
            }
        });
        // El `onAllOperationsCompleted` se ejecutará al final del Platform.runLater.
        // Modificaremos el callback para incluir la desactivación del cache.

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
                    cardRoot.setCache(false); // Deshabilitar cache aquí
                    originalOnCompleted.run();
                }
        );
        actualCollapseAnimation.play();
    }


    public void setFuncionData(FuncionDetallada funcion) {
        moviePosterImage = null;
        if (funcion.getFotografiaPelicula() != null && !funcion.getFotografiaPelicula().isEmpty()) {
            String imagePath = funcion.getFotografiaPelicula();
            // Asumiendo que las imágenes están en resources/com/example/images/ si no son rutas absolutas/HTTP
            if (!imagePath.startsWith("http") && !imagePath.startsWith("file:") && !imagePath.startsWith("jar:") && !imagePath.startsWith("/")) {
                imagePath = "/com/example/images/" + imagePath;
            }
            try {
                if (imagePath.startsWith("http") || imagePath.startsWith("file:")) {
                    // Cargar con backgroundLoading=true y manejo de errores
                    moviePosterImage = new Image(imagePath, true); // true para background loading
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
                // Imprimir la excepción si existe, puede dar más detalles
                System.err.println("Image loading exception for " + funcion.getTituloPelicula() + ": " + moviePosterImage.getException().getMessage());
            }
            System.err.println("Failed to load image or image path was null for: " + funcion.getTituloPelicula() + ". Loading placeholder.");
            loadPlaceholderPoster();
        }

        poster.setImage(moviePosterImage);
        title.setText(funcion.getTituloPelicula());
        subtitle.setText(funcion.getFechaHoraFuncion().format(TIME_FORMATTER) + " • Sala " + funcion.getNumeroSala() + " (" + funcion.getTipoSala() + ")");

        // Datos para la vista expandida
        posterExpanded.setImage(moviePosterImage);
        ratingLabelExpanded.setText("★ N/A"); // O el rating real si lo tienes
        titleExpanded.setText(funcion.getTituloPelicula());
        iconDuration.setText("⏱️ " + funcion.getDuracionMinutos() + " min");
        String year = "N/A";
        if (funcion.getFechaEstrenoPelicula() != null) {
            year = String.valueOf(funcion.getFechaEstrenoPelicula().getYear());
        }
        iconYear.setText("📅 " + year);
        iconGenre.setText("🎭 " + (funcion.getNombreTipoPelicula() != null ? funcion.getNombreTipoPelicula() : "No especificado"));
        synopsisExpanded.setText(funcion.getSinopsisPelicula() != null ? funcion.getSinopsisPelicula() : "Sinopsis no disponible.");
        castLabelExpanded.setText("⭐ Reparto no disponible en esta vista."); // O el reparto real
    }

    private void loadPlaceholderPoster() {
        String placeholderPath = "/com/example/images/placeholder_poster.png"; // Ajusta la ruta si es necesario
        try {
            InputStream placeholderStream = getClass().getResourceAsStream(placeholderPath);
            if (placeholderStream != null) {
                moviePosterImage = new Image(placeholderStream);
            } else {
                System.err.println("Placeholder image not found at: " + placeholderPath);
                // Considera crear una imagen simple programáticamente como fallback extremo
                moviePosterImage = null;
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image: " + e.getMessage());
            moviePosterImage = null;
        }
    }

    @FXML private void handleShowTimes() {
        System.out.println("Ver horarios para: " + title.getText());
        // Aquí iría la lógica para mostrar horarios, posiblemente usando dialogAnimationHelper
    }
}