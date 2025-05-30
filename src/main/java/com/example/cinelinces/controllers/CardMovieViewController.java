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
    @FXML private StackPane ratingBadge;      // Compact view badge
    @FXML private Label ratingLabel;          // Compact view label
    @FXML private VBox textContainer;
    @FXML private Label title;
    @FXML private Label subtitle;
    @FXML private HBox badgesContainer;
    @FXML private Label formatBadge;
    @FXML private Label genreBadge;
    @FXML private ImageView posterExpanded;
    @FXML private Label ratingLabelExpanded;  // Expanded view badge
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
    private DialogPaneViewController dialogPaneController;  // New: to open schedule dialog
    private Region placeholder;
    private boolean isExpanded = false, isAnimating = false;
    private static CardMovieViewController currentlyExpanded = null;
    private Image moviePosterImage;
    private FuncionDetallada funcion;                  // New: current function data

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm 'hrs'", Locale.getDefault());
    private static final String FULL_STAR  = "★";
    private static final String HALF_STAR  = "✬";
    private static final String EMPTY_STAR = "☆";

    /**
     * Initializes the controller context.
     */
    public void initContext(Pane parentContainer,
                            Pane overlayPane,
                            StackPane rootStack,
                            DialogAnimationHelper dialogAnimationHelper,
                            DialogPaneViewController dialogPaneController) {
        this.parentContainer       = parentContainer;
        this.overlayPane           = overlayPane;
        this.overlayHelper         = new OverlayHelper(this.overlayPane);
        this.dialogAnimationHelper = dialogAnimationHelper;
        this.dialogPaneController  = dialogPaneController;

        // Make overlay clicks pass through
        if (this.overlayHelper.getOverlay() != null) {
            this.overlayHelper.getOverlay().setMouseTransparent(true);
        }
        // Apply initial shadow
        if (CardAnimationHelper.SUBTLE_SHADOW_EFFECT != null) {
            cardRoot.setEffect(CardAnimationHelper.SUBTLE_SHADOW_EFFECT);
        } else {
            System.err.println("Advertencia: CardAnimationHelper.SUBTLE_SHADOW_EFFECT es null.");
        }
    }

    @FXML
    void onCardHover(MouseEvent e) {
        if (isExpanded || isAnimating ||
                (currentlyExpanded != null && currentlyExpanded != this)) return;
        Timeline hoverIn = CardAnimationHelper.createHoverInAnimation(cardRoot);
        if (hoverIn != null) hoverIn.play();
    }

    @FXML
    void onCardHoverExit(MouseEvent e) {
        if (isExpanded || isAnimating ||
                (currentlyExpanded != null && currentlyExpanded != this)) return;
        Timeline hoverOut = CardAnimationHelper.createHoverOutAnimation(cardRoot);
        if (hoverOut != null) hoverOut.play();
    }

    @FXML
    void onCardClick(MouseEvent e) {
        if (isAnimating) return;
        if (!isExpanded && currentlyExpanded != null && currentlyExpanded != this) return;
        isAnimating = true;
        if (!isExpanded) expandCard();
        else collapseCard();
    }

    private void expandCard() {
        currentlyExpanded = this;
        cardRoot.getStyleClass().add("expanded");
        Bounds bounds = cardRoot.getBoundsInParent();
        Point2D posInScene = cardRoot.localToScene(0,0);

        // Placeholder to keep layout
        placeholder = new Region();
        double w = cardRoot.getWidth()>0?cardRoot.getWidth():cardRoot.getPrefWidth();
        double h = cardRoot.getHeight()>0?cardRoot.getHeight():cardRoot.getPrefHeight();
        placeholder.setPrefSize(w,h);
        placeholder.setMinSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
        placeholder.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);

        if (cardRoot.getParent() instanceof Pane p) {
            int idx = p.getChildren().indexOf(cardRoot);
            if (idx!=-1) p.getChildren().set(idx, placeholder);
            else p.getChildren().add(placeholder);
        }

        // Move card into overlay
        Point2D posInOverlay = overlayPane.sceneToLocal(posInScene);
        cardRoot.setLayoutX(posInOverlay.getX());
        cardRoot.setLayoutY(posInOverlay.getY());
        cardRoot.setTranslateX(0); cardRoot.setTranslateY(0);
        cardRoot.setScaleX(1);    cardRoot.setScaleY(1);
        cardRoot.setOpacity(0);
        if (!overlayPane.getChildren().contains(cardRoot)) overlayPane.getChildren().add(cardRoot);
        cardRoot.toFront();

        // Swap layouts
        compactLayout.setVisible(false);
        compactLayout.setManaged(false);
        expandedLayout.setVisible(true);
        expandedLayout.setManaged(true);

        // Compute scale and translation
        double origW = w>0?w:cardRoot.getPrefWidth();
        double origH = h>0?h:cardRoot.getPrefHeight();
        double scaleX = 3.4, scaleY = 2.5;
        double targetX = (overlayPane.getWidth()-origW)/2 - cardRoot.getLayoutX();
        double targetY = (overlayPane.getHeight()-origH)/2 - cardRoot.getLayoutY();

        // Blur background
        dialogAnimationHelper.blurBackgroundIn(
                CardAnimationHelper.EXPAND_ANIM_DURATION.multiply(0.8), 8
        ).play();

        cardRoot.setCache(true);
        cardRoot.setCacheHint(CacheHint.SPEED);

        ParallelTransition anim = CardAnimationHelper.createExpandAnimation(
                cardRoot, targetX, targetY, scaleX, scaleY
        );
        anim.setOnFinished(evt->{
            cardRoot.setCache(false);
            cardRoot.setOpacity(1);
            isAnimating=false;
            isExpanded=true;
            cardRoot.setOnMouseEntered(null);
            cardRoot.setOnMouseExited(null);
        });
        anim.play();
    }

    private void collapseCard() {
        cardRoot.getStyleClass().remove("expanded");
        dialogAnimationHelper.blurBackgroundOut(
                CardAnimationHelper.COLLAPSE_ANIM_DURATION.multiply(0.8)
        ).play();

        cardRoot.setCache(true);
        cardRoot.setCacheHint(CacheHint.SPEED);

        ParallelTransition anim = CardAnimationHelper.createCollapseAnimation(
                cardRoot, overlayPane, parentContainer, placeholder,
                () -> {
                    cardRoot.setCache(false);
                    placeholder = null;
                    currentlyExpanded = null;
                    isAnimating = false;
                    isExpanded = false;
                    expandedLayout.setVisible(false);
                    expandedLayout.setManaged(false);
                    compactLayout.setVisible(true);
                    compactLayout.setManaged(true);
                    cardRoot.setOnMouseEntered(this::onCardHover);
                    cardRoot.setOnMouseExited(this::onCardHoverExit);
                    if (CardAnimationHelper.SUBTLE_SHADOW_EFFECT!=null)
                        cardRoot.setEffect(CardAnimationHelper.SUBTLE_SHADOW_EFFECT);
                }
        );
        anim.play();
    }

    private String formatTextWithLineBreaks(String text, int wordsPerLine) {
        if (text==null||text.trim().isEmpty()||wordsPerLine<=0) return text==null?"":text.trim();
        String[] words = text.trim().split("\\s+");
        if (words.length<=wordsPerLine && !text.contains("\n")) return text.trim();
        StringBuilder sb = new StringBuilder();
        int count=0;
        for (int i=0;i<words.length;i++){
            sb.append(words[i]); count++;
            if (count>=wordsPerLine && i<words.length-1){
                sb.append("\n"); count=0;
            } else if (i<words.length-1){
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String formatRatingToStars(double avg, int total) {
        if (total==0) return "N/A";
        double rating = Math.round(avg*2)/2.0;
        StringBuilder stars = new StringBuilder();
        for (int i=1;i<=5;i++){
            if (rating>=i) stars.append(FULL_STAR);
            else if (rating>=i-0.5) stars.append(HALF_STAR);
            else stars.append(EMPTY_STAR);
        }
        return stars.toString();
    }

    /**
     * Populates the card with the detailed function data.
     */
    public void setFuncionData(FuncionDetallada f) {
        this.funcion = f;
        // Load poster image
        moviePosterImage = null;
        if (f.getFotografiaPelicula()!=null && !f.getFotografiaPelicula().isEmpty()) {
            String path = f.getFotografiaPelicula();
            if (!path.startsWith("http") && !path.startsWith("/") && !path.startsWith("file:")) {
                path = "/com/example/images/"+path;
            }
            try {
                if (path.startsWith("http")||path.startsWith("file:")) {
                    moviePosterImage = new Image(path, true);
                } else {
                    InputStream stream = getClass().getResourceAsStream(path);
                    if (stream!=null) moviePosterImage = new Image(stream);
                }
            } catch (Exception e) {
                System.err.println("Error loading image "+path+": "+e.getMessage());
            }
        }
        if (moviePosterImage==null || moviePosterImage.isError()) {
            loadPlaceholderPoster();
        }

        poster.setImage(moviePosterImage);
        title.setText(f.getTituloPelicula());
        if (f.getFechaHoraFuncion()!=null && f.getTipoSala()!=null) {
            subtitle.setText(
                    f.getFechaHoraFuncion().format(TIME_FORMATTER)
                            + " • Sala " + f.getNumeroSala()
                            + " (" + f.getTipoSala() + ")"
            );
        } else {
            subtitle.setText("Función no disponible");
        }

        // Hide compact badge
        ratingBadge.setVisible(false);
        ratingBadge.setManaged(false);

        // Expanded badge
        String stars = formatRatingToStars(
                f.getCalificacionPromedioPelicula(),
                f.getTotalCalificacionesPelicula()
        );
        ratingLabelExpanded.setText(stars);

        posterExpanded.setImage(moviePosterImage);
        titleExpanded.setText(f.getTituloPelicula());
        iconDuration.setText("⏱️ "+f.getDuracionMinutos()+" min");
        iconYear.setText("📅 "+(f.getFechaEstrenoPelicula()!=null
                ? f.getFechaEstrenoPelicula().getYear() : "----"));
        iconGenre.setText("🎭 "+(f.getNombreTipoPelicula()!=null
                ? f.getNombreTipoPelicula() : "Desconocido"));
        classificationLabel.setText("📊 "+(f.getClasificacionPelicula()!=null
                ? f.getClasificacionPelicula() : "N/A"));

        String syn = f.getSinopsisPelicula()!=null
                ? f.getSinopsisPelicula() : "Sinopsis no disponible.";
        synopsisExpanded.setText(formatTextWithLineBreaks(syn,13));

        List<ActorPeliculaDTO> actors = f.getActores();
        if (actors!=null && !actors.isEmpty()) {
            String castStr = actors.stream()
                    .map(a->a.getNombreActor()+" ("+a.getPersonaje()+")")
                    .collect(Collectors.joining(", "));
            castLabelExpanded.setText(
                    "⭐ Reparto: "+formatTextWithLineBreaks(castStr,13)
            );
        } else {
            castLabelExpanded.setText("⭐ Reparto no disponible.");
        }

        directorLabel.setText("🎬 Director: "+
                (f.getNombreDirector()!=null?f.getNombreDirector():"No disponible")
        );
        studioLabel.setText("🏢 Estudio: "+
                (f.getNombreEstudio()!=null?f.getNombreEstudio():"No disponible")
        );
        String lang = "🗣️ Idioma: "+(
                f.getIdiomaPelicula()!=null?f.getIdiomaPelicula():"No disponible"
        ) + (f.isSubtituladaPelicula()?" (Subtitulada)":" (Doblada)");
        languageLabel.setText(lang);

        // Store DTO for schedule dialog
        btnShowTimes.setUserData(f);
    }

    private void loadPlaceholderPoster() {
        String placeholderPath = "/com/example/images/placeholder_poster.png";
        try (InputStream stream = getClass().getResourceAsStream(placeholderPath)) {
            if (stream!=null) moviePosterImage = new Image(stream);
        } catch (Exception e) {
            System.err.println("Error loading placeholder: "+e.getMessage());
        }
    }

    /**
     * Opens the schedule dialog for this function.
     */
    @FXML
    private void handleShowTimes() {
        if (funcion!=null && dialogPaneController!=null) {
            dialogPaneController.setMovieContext(funcion);
            dialogAnimationHelper.showDialog(
                    dialogPaneController.getDialogPanel(),
                    btnShowTimes
            );
        }
    }
}
