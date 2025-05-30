package com.example.cinelinces.controllers;


import com.example.cinelinces.DAO.AsientoDAO;
import com.example.cinelinces.DAO.impl.AsientoDAOImpl;
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.utils.SummaryContext;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionViewController {
    @FXML private Label titleLabel;
    @FXML private GridPane seatGrid;
    @FXML private Label selectedCountLabel;
    @FXML private Button btnConfirm;
    @FXML private Button btnCancel;

    private FuncionDetallada currentFunction;
    private LocalDateTime selectedDateTime;
    private List<AsientoDTO> seats;
    private List<AsientoDTO> selectedSeats = new ArrayList<>();

    private final AsientoDAO asientoDAO = new AsientoDAOImpl();

    /**
     * Inicializa la vista con la función y horario seleccionados.
     */
    public void initData(FuncionDetallada function, LocalDateTime dateTime) {
        this.currentFunction    = function;
        this.selectedDateTime   = dateTime;
        titleLabel.setText(
                function.getTituloPelicula() + "  |  " +
                        dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm 'hrs'"))
        );
        loadSeats();
    }

    /**
     * Carga todos los asientos de la sala y marca los ya ocupados.
     * Crea un ToggleButton por asiento para permitir seleccionarlos.
     */
    private void loadSeats() {
        seats = asientoDAO.findAsientosBySala(currentFunction.getIdSala());
        List<Integer> bookedIds =
                asientoDAO.findBookedSeatIdsByFuncion(currentFunction.getIdFuncion());

        int cols = 8; // o calcula dinámicamente según número de columnas
        int row = 0, col = 0;
        for (AsientoDTO asiento : seats) {
            ToggleButton btn = new ToggleButton(
                    asiento.getFila() + asiento.getNumero()
            );
            btn.setUserData(asiento);
            btn.setOnAction(e -> {
                AsientoDTO a = (AsientoDTO) btn.getUserData();
                if (btn.isSelected()) selectedSeats.add(a);
                else selectedSeats.remove(a);
                selectedCountLabel.setText(String.valueOf(selectedSeats.size()));
            });

            if (bookedIds.contains(asiento.getIdAsiento())) {
                btn.setDisable(true);
                btn.setStyle("-fx-background-color: lightgray;");
            }

            seatGrid.add(btn, col, row);
            if (++col >= cols) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Confirma la selección de asientos guardándolos en el contexto de resumen
     * y cierra la ventana.
     */
    @FXML
    private void handleConfirm() {
        // Almacenar en singleton para el resumen de compra
        SummaryContext ctx = SummaryContext.getInstance();
        ctx.setSelectedFunction(currentFunction);
        ctx.setSelectedDateTime(selectedDateTime);
        ctx.setSelectedSeats(new ArrayList<>(selectedSeats));

        // Cerrar ventana
        Stage stage = (Stage) btnConfirm.getScene().getWindow();
        stage.close();
    }

    /** Cancela y cierra la ventana sin guardar selección. */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
