package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.FuncionDAO;
import com.example.cinelinces.DAO.impl.FuncionDAOImpl;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

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

    private FuncionDAO funcionDAO = new FuncionDAOImpl();
    private FuncionDetallada currentMovie;

    @FXML
    private void initialize() {
        // Listener para el selector de fecha
        datePicker.setOnAction(e -> loadHorarios());

        // Cerrar diálogo
        closeBtn.setOnAction(e -> {
            dialogPanel.setVisible(false);
            dialogPanel.setOpacity(0);
        });
    }

    public VBox getDialogPanel() {
        return dialogPanel;
    }

    public Button getCloseBtn() {
        return closeBtn;
    }

    /**
     * Inicializa el diálogo con la película seleccionada.
     */
    public void setMovieContext(FuncionDetallada movie) {
        this.currentMovie = movie;
        titleLabel.setText("Horarios - " + movie.getTituloPelicula());
        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        loadHorarios();
        dialogPanel.setVisible(true);
        dialogPanel.setOpacity(1);
    }

    /**
     * Limpia el contexto del diálogo.
     */
    public void clearMovieContext() {
        this.currentMovie = null;
        titleLabel.setText("Horarios Disponibles");
        horariosContainer.getChildren().clear();
        placeholderContent.setText("Selecciona una fecha para ver horarios.");
        placeholderContent.setVisible(true);
    }

    /**
     * Carga y muestra los horarios de función para la película, cine y fecha seleccionados.
     */
    private void loadHorarios() {
        if (currentMovie == null) return;

        LocalDate fecha = datePicker.getValue();
        int idCine     = currentMovie.getIdCine();
        int idPelicula = currentMovie.getIdPelicula();

        List<LocalDateTime> horas = funcionDAO.findHorariosByCinePeliculaFecha(idCine, idPelicula, fecha);
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
                horariosContainer.getChildren().add(lbl);
            }
        }
    }
}
