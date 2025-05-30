// com/example/cinelinces/controllers/DialogPaneViewController.java
package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.FuncionDAO;
import com.example.cinelinces.DAO.impl.FuncionDAOImpl;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DialogPaneViewController {
    @FXML private VBox dialogPanel;
    @FXML private Button closeBtn;
    @FXML private Label titleLabel;
    @FXML private Label placeholderContent;
    @FXML private DatePicker datePicker;
    @FXML private VBox horariosContainer;

    private final FuncionDAO funcionDAO = new FuncionDAOImpl();
    private FuncionDetallada currentMovie;
    private List<LocalDate> availableDates;

    @FXML
    private void initialize() {
        // Cerrar diálogo
        closeBtn.setOnAction(e -> {
            dialogPanel.setVisible(false);
            dialogPanel.setOpacity(0);
        });

        // Cuando se seleccione otra fecha, recargar horarios
        datePicker.setOnAction(e -> loadHorarios());
    }

    public VBox getDialogPanel() { return dialogPanel; }
    public Button getCloseBtn()     { return closeBtn;     }

    /**
     * Inicializa el diálogo con la película seleccionada.
     * Carga la lista de fechas disponibles y aplica el DayCellFactory.
     */
    public void setMovieContext(FuncionDetallada movie) {
        this.currentMovie = movie;
        titleLabel.setText("Horarios - " + movie.getTituloPelicula());

        // Obtener fechas con funciones
        availableDates = funcionDAO.findFechasDisponiblesByCinePelicula(
                movie.getIdCine(), movie.getIdPelicula()
        );

        // Highlight y disable en el DatePicker
        datePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) return;
                if (availableDates.contains(date)) {
                    if (date.isBefore(LocalDate.now())) {
                        setStyle("-fx-background-color: lightgray;");
                    } else {
                        setStyle("-fx-background-color: lightgreen;");
                    }
                } else {
                    setDisable(true);
                    setStyle("-fx-background-color: #f0f0f0;");
                }
            }
        });

        // Seleccionar hoy o la primera fecha disponible
        LocalDate start = LocalDate.now();
        if (availableDates.contains(start)) {
            datePicker.setValue(start);
        } else if (!availableDates.isEmpty()) {
            datePicker.setValue(availableDates.get(0));
        }

        dialogPanel.setVisible(true);
        dialogPanel.setOpacity(1);

        loadHorarios();
    }

    /**
     * Limpia el estado del diálogo.
     */
    public void clearMovieContext() {
        this.currentMovie = null;
        titleLabel.setText("Horarios Disponibles");
        horariosContainer.getChildren().clear();
        placeholderContent.setText("Selecciona una fecha para ver horarios.");
        placeholderContent.setVisible(true);
    }

    /**
     * Carga y muestra los horarios disponibles en el VBox.
     * Cada Label de horario es clicable y abre la vista de selección de asientos.
     */
    private void loadHorarios() {
        if (currentMovie == null) return;

        LocalDate fecha = datePicker.getValue();
        List<LocalDateTime> horas = funcionDAO.findHorariosByCinePeliculaFecha(
                currentMovie.getIdCine(),
                currentMovie.getIdPelicula(),
                fecha
        );

        horariosContainer.getChildren().clear();

        if (horas.isEmpty()) {
            placeholderContent.setText("No hay funciones para " +
                    fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            placeholderContent.setVisible(true);
        } else {
            placeholderContent.setVisible(false);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm 'hrs'");
            for (LocalDateTime dt : horas) {
                Label lbl = new Label(dt.format(fmt));
                lbl.getStyleClass().add("text-body");
                // cambia el cursor al pasar
                lbl.setCursor(Cursor.HAND);
                // al hacer clic abre selección de asientos
                lbl.setOnMouseClicked(e -> openSeatSelection(dt));
                horariosContainer.getChildren().add(lbl);
            }
        }
    }


    /**
     * Abre una nueva ventana con la vista de selección de asientos
     * para la función, cine y horario indicados.
     */
    private void openSeatSelection(LocalDateTime dateTime) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/seatSelection-view.fxml")
            );
            Pane pane = loader.load();
            SeatSelectionViewController ctrl = loader.getController();
            // Le pasas la función y el horario
            ctrl.initData(currentMovie, dateTime);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Seleccionar Asientos");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
