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
    @FXML
    private VBox dialogPanel;
    @FXML
    private Button closeBtn;
    @FXML
    private Label titleLabel;
    @FXML
    private Label placeholderContent;
    @FXML
    private DatePicker datePicker;
    @FXML
    private VBox horariosContainer;

    private final FuncionDAO funcionDAO = new FuncionDAOImpl();
    private FuncionDetallada currentMovie; // Esta es la FuncionDetallada de la tarjeta de la película.
    // Se usa para obtener el IdCine e IdPelicula para buscar horarios.
    private List<LocalDate> availableDates;

    @FXML
    private void initialize() {
        closeBtn.setOnAction(e -> {
            dialogPanel.setVisible(false);
            dialogPanel.setOpacity(0);
        });
        datePicker.setOnAction(e -> loadHorarios());
    }

    public VBox getDialogPanel() {
        return dialogPanel;
    }

    public Button getCloseBtn() {
        return closeBtn;
    }

    public void setMovieContext(FuncionDetallada movie) {
        this.currentMovie = movie;
        titleLabel.setText("Horarios - " + movie.getTituloPelicula());

        availableDates = funcionDAO.findFechasDisponiblesByCinePelicula(movie.getIdCine(), movie.getIdPelicula());
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

    public void clearMovieContext() {
        this.currentMovie = null;
        titleLabel.setText("Horarios Disponibles");
        horariosContainer.getChildren().clear();
        placeholderContent.setText("Selecciona una fecha para ver horarios.");
        placeholderContent.setVisible(true);
    }

    private void loadHorarios() {
        if (currentMovie == null) return;

        LocalDate fecha = datePicker.getValue();
        // MODIFICADO: Ahora obtenemos una lista de FuncionDetallada para cada horario
        List<FuncionDetallada> funcionesEnFecha = funcionDAO.findFuncionesByCinePeliculaFecha(currentMovie.getIdCine(), currentMovie.getIdPelicula(), fecha);

        horariosContainer.getChildren().clear();

        if (funcionesEnFecha.isEmpty()) {
            placeholderContent.setText("No hay funciones para " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            placeholderContent.setVisible(true);
        } else {
            placeholderContent.setVisible(false);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm 'hrs'");
            for (FuncionDetallada funcionParaHorario : funcionesEnFecha) { // Itera sobre objetos FuncionDetallada
                Label lbl = new Label(funcionParaHorario.getFechaHoraFuncion().format(fmt)); // Usa el LocalDateTime del DTO
                lbl.getStyleClass().add("text-body");
                // cambia el cursor al pasar
                lbl.setCursor(Cursor.HAND);
                // MODIFICADO: Al hacer clic, pasa el objeto FuncionDetallada COMPLETO
                lbl.setOnMouseClicked(e -> openSeatSelection(funcionParaHorario)); // Pasa el DTO completo
                horariosContainer.getChildren().add(lbl);
            }
        }
    }

    // MODIFICADO: Recibe el objeto FuncionDetallada completo
    private void openSeatSelection(FuncionDetallada selectedFuncion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/seatSelection-view.fxml"));
            Pane pane = loader.load();
            SeatSelectionViewController ctrl = loader.getController();
            // Pasa el objeto FuncionDetallada completo y su fecha/hora a SeatSelectionViewController
            ctrl.initData(selectedFuncion, selectedFuncion.getFechaHoraFuncion()); // Pasamos el DTO correcto

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