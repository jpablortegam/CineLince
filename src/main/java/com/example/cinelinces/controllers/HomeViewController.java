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
// Ya no necesitas Rectangle2D aquí si configureFeaturedBannerImage() se elimina o cambia drásticamente
// import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox; // Asegúrate de tener esta importación
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox; // Asegúrate de tener esta importación
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

    // --- CAMPOS @FXML PARA EL NUEVO BANNER DETALLADO (Estilo Dune) ---
    @FXML private StackPane detailedFeaturedBannerPane;
    @FXML private ImageView featuredBackgroundImage;
    @FXML private ImageView featuredPosterImage;
    @FXML private Label featuredRatingBadge;
    @FXML private Label featuredMovieTitleText;
    @FXML private Label featuredGenreText;
    @FXML private Label featuredDurationText;
    @FXML private Label featuredYearText;
    @FXML private Label featuredSynopsisText;
    @FXML private Button btnFeaturedVerHorarios; // Este es el botón "Ver horarios" DEL NUEVO BANNER
    @FXML private Button btnFeaturedMiLista;
    @FXML private Button btnFeaturedMasInfo;
    @FXML private Button btnVerTodasProximamente; // Si le diste este fx:id al botón "Ver todas"

    // --- CAMPOS @FXML ANTIGUOS (Comentados o eliminados si ya no se usan para un banner simple) ---
    // @FXML private StackPane featuredBannerPane; // Este fx:id ya no es el banner principal
    // @FXML private ImageView featuredImage;    // Este fx:id ya no es la imagen del banner principal
    // @FXML private Label featuredTitle;        // Este fx:id ya no es el título del banner principal
    // @FXML private Label featuredSubtitle;     // Este fx:id ya no es el subtítulo del banner principal
    // @FXML private Button btnVerHorarios; // Este ID ahora se usa para btnFeaturedVerHorarios si es el mismo botón

    private CineDAO cineDAO;
    private FuncionDAO funcionDAO;
    private DialogAnimationHelper dialogHelper;
    private Node dialogPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cineDAO = new CineDAOImpl();
        funcionDAO = new FuncionDAOImpl();

        // Ya NO llames a configureFeaturedBannerImage() aquí, o necesitará ser reescrito
        // para el nuevo banner. Por ahora, lo comentamos para evitar el NullPointerException.
        // configureFeaturedBannerImage();

        dialogHelper = new DialogAnimationHelper(rootStack, overlayPane);
        setupCineComboBox();
        loadProximamenteCards();
        setupDialogPane(); // Este método necesitará usar btnFeaturedVerHorarios

        // TODO: Crear un nuevo método para inicializar/limpiar el detailedFeaturedBannerPane
        // por ejemplo: setDefaultDetailedBannerState();
    }

    // El método configureFeaturedBannerImage ESTABA DISEÑADO PARA EL BANNER ANTIGUO.
    // Necesitarás un NUEVO método para configurar el detailedFeaturedBannerPane
    // o eliminar este si ya no configuras un banner estático al inicio de esa forma.
    /*
    private void configureFeaturedBannerImage() {
        // Este código causaba NullPointerException porque this.featuredImage y
        // this.featuredBannerPane eran null, ya que esos fx:id ya no existen
        // en el FXML principal como antes.
        try {
            String imagePath = "/com/example/images/dune_banner.jpg"; // Ruta de ejemplo
            InputStream featuredStream = getClass().getResourceAsStream(imagePath);

            if (featuredStream != null) {
                // if (this.featuredImage != null) { // Comprobación adicional
                //     Image image = new Image(featuredStream);
                //     this.featuredImage.setImage(image);
                //     // ... resto de la configuración de la imagen ...
                // }
            } else {
                // System.err.println("Error: No se pudo cargar la imagen del banner. Verifica la ruta: " + imagePath);
                // if (this.featuredBannerPane != null) this.featuredBannerPane.setStyle("-fx-background-color: #2c3e50;");
                // if (this.featuredTitle != null) this.featuredTitle.setText("Banner no disponible");
            }
        } catch (Exception e) {
            // System.err.println("Excepción al cargar o configurar la imagen del banner: " + e.getMessage());
            // e.printStackTrace();
            // if (this.featuredBannerPane != null) this.featuredBannerPane.setStyle("-fx-background-color: #2c3e50;");
            // if (this.featuredTitle != null) this.featuredTitle.setText("Error al cargar banner");
        }
    }
    */

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
                // Asegúrate que el botón de cerrar se refiere al botón correcto que abrió el diálogo
                closeBtn.setOnAction(e -> dialogHelper.hideDialog(this.dialogPanel, btnFeaturedVerHorarios));
            }

            // Ahora el botón de "Ver horarios" principal es btnFeaturedVerHorarios
            if (btnFeaturedVerHorarios != null) {
                ButtonHoverAnimator.applyHoverEffect(btnFeaturedVerHorarios);
                btnFeaturedVerHorarios.setOnAction(e -> {
                    if (this.dialogPanel != null) {
                        // Aquí puedes pasar información de la película destacada al diálogo si es necesario
                        // Object movieData = btnFeaturedVerHorarios.getUserData();
                        // if (movieData instanceof FuncionDetallada) {
                        //    dialogController.setMovieContext((FuncionDetallada) movieData);
                        // }
                        dialogHelper.showDialog(this.dialogPanel, btnFeaturedVerHorarios);
                    } else {
                        System.err.println("Error: El panel del diálogo (dialogPanel) no ha sido inicializado.");
                    }
                });
            } else {
                System.err.println("FXML @FXML Injected Button 'btnFeaturedVerHorarios' is null. Check FXML fx:id.");
            }

            // Configurar acciones para los otros botones del nuevo banner si es necesario
            if (btnFeaturedMiLista != null) {
                ButtonHoverAnimator.applyHoverEffect(btnFeaturedMiLista);
                btnFeaturedMiLista.setOnAction(e -> {
                    System.out.println("Botón 'Mi Lista' clickeado");
                    // Lógica para añadir a "Mi Lista"
                });
            }
            if (btnFeaturedMasInfo != null) {
                ButtonHoverAnimator.applyHoverEffect(btnFeaturedMasInfo);
                btnFeaturedMasInfo.setOnAction(e -> {
                    System.out.println("Botón 'Más Info' clickeado");
                    // Lógica para mostrar más información
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
                        // loadFuncionesEnCartelera será llamado por el listener
                    }
                } else {
                    cineComboBox.setPromptText("No hay cines disponibles");
                    // TODO: Limpiar o poner estado por defecto en el banner detallado si no hay cines
                    // clearDetailedBanner();
                }
            });
        }).start();

        cineComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadFuncionesEnCartelera(newVal.getIdCine());
            } else {
                // TODO: Limpiar o poner estado por defecto en el banner detallado
                // clearDetailedBanner();
                enCarteleraPane.getChildren().clear();
                // Podrías mostrar un mensaje en enCarteleraPane
            }
        });
    }

    private void loadFuncionesEnCartelera(int idCine) {
        new Thread(() -> {
            List<FuncionDetallada> todasLasFuncionesEnCartelera = funcionDAO.findFuncionesDetalladasByCineId(idCine);

            Platform.runLater(() -> {
                enCarteleraPane.getChildren().clear();

                if (todasLasFuncionesEnCartelera == null || todasLasFuncionesEnCartelera.isEmpty()) {
                    Label noFuncionesLabel = new Label("No hay funciones en cartelera para este cine en este momento.");
                    noFuncionesLabel.getStyleClass().add("text-body");
                    enCarteleraPane.getChildren().add(noFuncionesLabel);
                    // TODO: Limpiar el banner detallado
                    // clearDetailedBanner();
                    return;
                }

                // TODO: Implementar lógica para actualizar el NUEVO BANNER DETALLADO
                // con la primera película (o la mejor puntuada).
                // Ejemplo:
                // if (!todasLasFuncionesEnCartelera.isEmpty()) {
                //    FuncionDetallada peliculaDestacada = todasLasFuncionesEnCartelera.get(0);
                //    updateDetailedBanner(peliculaDestacada);
                // } else {
                //    clearDetailedBanner();
                // }


                // Lógica para las tarjetas de "En cartelera" (sin cambios importantes aquí)
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

                if (funcionesParaMostrar.isEmpty()) {
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

    // TODO: Implementar estos métodos para el NUEVO BANNER DETALLADO
    /*
    private void updateDetailedBanner(FuncionDetallada movie) {
        if (movie == null) {
            clearDetailedBanner();
            return;
        }
        detailedFeaturedBannerPane.setVisible(true);
        featuredMovieTitleText.setText(movie.getTituloPelicula());
        featuredSynopsisText.setText(movie.getSinopsis()); // Asegúrate que FuncionDetallada tiene getSinopsis()
        featuredGenreText.setText("🏷️ " + movie.getGeneroPelicula());
        featuredDurationText.setText("⏱️ " + movie.getDuracionMinutos() + " min");
        featuredYearText.setText("📅 " + movie.getAnioEstreno());
        // featuredRatingBadge.setText("★ " + movie.getCalificacion()); // Si tienes calificación

        // Cargar imagen del poster
        try {
            String posterPath = movie.getPathImagenPoster(); // Ejemplo: "/com/example/images/posters/dune.jpg"
            if (posterPath != null && !posterPath.isEmpty()) {
                InputStream posterStream = getClass().getResourceAsStream(posterPath);
                if (posterStream != null) {
                    featuredPosterImage.setImage(new Image(posterStream));
                } else {
                    featuredPosterImage.setImage(null); // O imagen placeholder
                    System.err.println("No se encontró el poster: " + posterPath);
                }
            } else {
                featuredPosterImage.setImage(null);
            }
        } catch (Exception e) {
            featuredPosterImage.setImage(null);
            e.printStackTrace();
        }

        // Cargar imagen de fondo (opcional, podría ser el mismo poster difuminado o una imagen ancha)
        try {
            String backgroundPath = movie.getPathImagenBanner(); // O usa posterPath si no hay banner específico
             if (backgroundPath == null || backgroundPath.isEmpty()) backgroundPath = movie.getPathImagenPoster();

            if (backgroundPath != null && !backgroundPath.isEmpty()) {
                InputStream bgStream = getClass().getResourceAsStream(backgroundPath);
                if (bgStream != null) {
                    featuredBackgroundImage.setImage(new Image(bgStream));
                } else {
                    featuredBackgroundImage.setImage(null);
                }
            } else {
                featuredBackgroundImage.setImage(null);
            }
        } catch (Exception e) {
            featuredBackgroundImage.setImage(null);
            e.printStackTrace();
        }


        btnFeaturedVerHorarios.setUserData(movie); // Para pasar datos al diálogo
    }

    private void clearDetailedBanner() {
        detailedFeaturedBannerPane.setVisible(false); // O poner placeholders
        // featuredMovieTitleText.setText("Película Destacada");
        // featuredSynopsisText.setText("Selecciona un cine para ver detalles.");
        // featuredPosterImage.setImage(null);
        // featuredBackgroundImage.setImage(null);
        // ... y limpiar otros campos ...
        // btnFeaturedVerHorarios.setUserData(null);
    }
    */

    private void loadProximamenteCards() {
        proximamentePane.getChildren().clear();
        List<FuncionDetallada> proximamenteFunciones = new ArrayList<>(); // Placeholder
        if (proximamenteFunciones.isEmpty()) {
            Label noProximamenteLabel = new Label("No hay estrenos confirmados próximamente.");
            noProximamenteLabel.getStyleClass().add("text-body");
            proximamentePane.getChildren().add(noProximamenteLabel);
            return;
        }
        // ... (Lógica para cargar tarjetas "Próximamente") ...
    }
}