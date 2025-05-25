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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeViewController implements Initializable {
    @FXML private StackPane rootStack;
    @FXML private Pane overlayPane;
    @FXML private Button btnVerHorarios; // Este botón podría necesitar contexto de qué función ver
    @FXML private FlowPane enCarteleraPane; // Renombrado desde upcomingPane1
    @FXML private FlowPane proximamentePane; // Renombrado desde upcomingPane
    @FXML private ComboBox<Cine> cineComboBox;

    // DAOs
    private CineDAO cineDAO;
    private FuncionDAO funcionDAO;

    // private final MovieService movieService = new MovieService(); // Para "Próximamente" si aún se usa
    private List<CardMovieViewController> cardControllers = new ArrayList<>();
    private DialogAnimationHelper dialogHelper;
    private Node dialogPanel;

    // Para la imagen destacada (ejemplo)
    @FXML private ImageView featuredImage;
    @FXML private ImageView ratingIcon;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cineDAO = new CineDAOImpl();
        funcionDAO = new FuncionDAOImpl();

        // Cargar imagen destacada (ejemplo)
        try {
            InputStream featuredStream = getClass().getResourceAsStream("/images/dune_banner.jpg");
            if (featuredStream != null) {
                featuredImage.setImage(new Image(featuredStream));
            } else {
                System.err.println("No se pudo cargar /images/dune_banner.jpg");
            }
            InputStream starStream = getClass().getResourceAsStream("/icons/star_filled.png");
            if (starStream != null) {
                ratingIcon.setImage(new Image(starStream));
            } else {
                System.err.println("No se pudo cargar /icons/star_filled.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        dialogHelper = new DialogAnimationHelper(rootStack, overlayPane);
        ButtonHoverAnimator.applyHoverEffect(btnVerHorarios);

        setupCineComboBox();
        loadProximamenteCards(); // Cargar "Próximamente" como antes si es necesario

        // Cargar diálogo FXML (sin cambios)
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/dialogPane-view.fxml")
            );
            AnchorPane root = loader.load();
            DialogPaneViewController dlgCtrl = loader.getController();
            VBox panel = dlgCtrl.getDialogPanel();
            dialogPanel = panel;
            panel.setVisible(false);
            panel.setOpacity(0);
            overlayPane.getChildren().add(panel);

            Button closeBtn = dlgCtrl.getCloseBtn();
            ButtonHoverAnimator.applyHoverEffect(closeBtn);
            closeBtn.setOnAction(e -> dialogHelper.hideDialog(panel, btnVerHorarios));
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar dialogPane-view.fxml", e);
        }

        btnVerHorarios.setOnAction(e -> {
            // Aquí necesitarías saber qué película/función está destacada para pasar datos al diálogo
            System.out.println("Botón 'Ver Horarios' de destacada presionado.");
            // dialogHelper.showDialog(dialogPanel, btnVerHorarios);
        });
    }

    private void setupCineComboBox() {
        // Cargar cines en un hilo separado para no bloquear la UI
        new Thread(() -> {
            List<Cine> cines = cineDAO.findAll();
            Platform.runLater(() -> {
                if (cines != null && !cines.isEmpty()) {
                    cineComboBox.setItems(FXCollections.observableArrayList(cines));

                    // Convertidor para mostrar el nombre del cine en el ComboBox
                    cineComboBox.setConverter(new StringConverter<Cine>() {
                        @Override
                        public String toString(Cine cine) {
                            return cine != null ? cine.getNombre() : "";
                        }

                        @Override
                        public Cine fromString(String string) {
                            // No se necesita para un ComboBox no editable que solo muestra
                            return cineComboBox.getItems().stream()
                                    .filter(cine -> cine.getNombre().equals(string))
                                    .findFirst().orElse(null);
                        }
                    });
                    // Para personalizar la apariencia de cada celda en el desplegable
                    cineComboBox.setCellFactory(lv -> new ListCell<Cine>() {
                        @Override
                        protected void updateItem(Cine cine, boolean empty) {
                            super.updateItem(cine, empty);
                            setText(empty || cine == null ? null : cine.getNombre());
                        }
                    });


                    // Seleccionar el primero por defecto y cargar funciones
                    cineComboBox.getSelectionModel().selectFirst();
                    Cine selectedCine = cineComboBox.getSelectionModel().getSelectedItem();
                    if (selectedCine != null) {
                        loadFuncionesEnCartelera(selectedCine.getIdCine());
                    }
                } else {
                    cineComboBox.setPromptText("No hay cines disponibles");
                }
            });
        }).start();


        // Listener para cambios en la selección del ComboBox
        cineComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null && (oldValue == null || newValue.getIdCine() != oldValue.getIdCine())) {
                loadFuncionesEnCartelera(newValue.getIdCine());
            }
        });
    }

    private void loadFuncionesEnCartelera(int idCine) {
        // Cargar funciones en un hilo separado
        new Thread(() -> {
            List<FuncionDetallada> funciones = funcionDAO.findFuncionesDetalladasByCineId(idCine);
            Platform.runLater(() -> {
                enCarteleraPane.getChildren().clear();
                // cardControllers.clear(); // Considera si necesitas limpiar esto o manejarlo por panel

                if (funciones.isEmpty()) {
                    // Opcional: Mostrar un mensaje si no hay funciones
                    // Label noFuncionesLabel = new Label("No hay funciones disponibles para este cine.");
                    // enCarteleraPane.getChildren().add(noFuncionesLabel);
                    System.out.println("No hay funciones para el cine ID: " + idCine);
                    return;
                }

                for (FuncionDetallada funcion : funciones) {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml")
                        );
                        Node cardNode = loader.load();

                        CardMovieViewController ctrl = loader.getController();
                        // Necesitas adaptar CardMovieViewController para tomar FuncionDetallada
                        ctrl.setFuncionData(funcion);
                        ctrl.initContext(enCarteleraPane, overlayPane, rootStack, dialogHelper);
                        enCarteleraPane.getChildren().add(cardNode);
                        // cardControllers.add(ctrl); // Considera cómo manejar esta lista
                    } catch (IOException e) {
                        System.err.println("Error al cargar card para función: " + funcion.getTituloPelicula());
                        e.printStackTrace();
                    }
                }
            });
        }).start();
    }

    private void loadProximamenteCards() {
        // Esta lógica puede permanecer si "Próximamente" no depende del cine seleccionado
        // Si depende, necesitarás adaptarla similar a loadFuncionesEnCartelera
        // Por ahora, la dejaré como estaba, asumiendo que usa MovieService
        // MovieService movieService = new MovieService(); // Instancia local o miembro de clase
        // List<Movie> proximamente = movieService.fetchUpcoming(); // Asumiendo que MovieService tiene este método

        // Ejemplo de datos dummy para "Próximamente" si MovieService no está listo
        List<FuncionDetallada> proximamente = new ArrayList<>(); // O List<Movie> si usas el modelo original

        proximamentePane.getChildren().clear();
        for (FuncionDetallada funcion : proximamente) { // O Movie movie : proximamente
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml")
                );
                Node cardNode = loader.load();
                CardMovieViewController ctrl = loader.getController();
                ctrl.setFuncionData(funcion); // O ctrl.setMovieData(movie);
                ctrl.initContext(proximamentePane, overlayPane, rootStack, dialogHelper);
                proximamentePane.getChildren().add(cardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
