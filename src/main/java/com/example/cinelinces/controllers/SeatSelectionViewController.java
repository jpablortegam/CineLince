package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.AsientoDAO;
import com.example.cinelinces.DAO.impl.AsientoDAOImpl;
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.utils.SummaryContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la ventana de selección de asientos.
 */
public class SeatSelectionViewController {
    @FXML private Label titleLabel;
    @FXML private GridPane seatGrid;
    @FXML private Label selectedCountLabel;
    @FXML private Button btnConfirm;
    @FXML private Button btnCancel;

    private FuncionDetallada currentFunction;
    private LocalDateTime selectedDateTime;
    private List<AsientoDTO> seats;
    private final List<AsientoDTO> selectedSeats = new ArrayList<>();

    private final AsientoDAO asientoDAO = new AsientoDAOImpl();

    /**
     * Inicializa la vista con función y horario.
     */
    public void initData(FuncionDetallada function, LocalDateTime dateTime) {
        this.currentFunction  = function;
        this.selectedDateTime = dateTime;
        titleLabel.setText(
                function.getTituloPelicula() + "  |  " +
                        dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm 'hrs'"))
        );
        loadSeats();
    }

    private void loadSeats() {
        seats = asientoDAO.findAsientosBySala(currentFunction.getIdSala());
        List<Integer> bookedIds =
                asientoDAO.findBookedSeatIdsByFuncion(currentFunction.getIdFuncion());

        int cols = 8, row = 0, col = 0;
        for (AsientoDTO asiento : seats) {
            ToggleButton btn = new ToggleButton(asiento.getFila() + asiento.getNumero());
            btn.getStyleClass().add("seat-button");
            btn.setUserData(asiento);

            if (bookedIds.contains(asiento.getIdAsiento())) {
                btn.setDisable(true);
                btn.getStyleClass().add("seat-occupied");
            } else {
                btn.getStyleClass().add("seat-available");
                btn.selectedProperty().addListener((obs, was, isSel) -> {
                    if (isSel) {
                        btn.getStyleClass().remove("seat-available");
                        btn.getStyleClass().add("seat-selected");
                        selectedSeats.add(asiento);
                    } else {
                        btn.getStyleClass().remove("seat-selected");
                        btn.getStyleClass().add("seat-available");
                        selectedSeats.remove(asiento);
                    }
                    selectedCountLabel.setText(String.valueOf(selectedSeats.size()));
                });
            }

            seatGrid.add(btn, col, row);
            if (++col >= cols) { col = 0; row++; }
        }
    }

    /**
     * 1) Guarda la selección de asientos en el contexto.
     * 2) Cierra esta ventana.
     * 3) Abre la ventana de selección de productos.
     */
    @FXML
    private void handleConfirm() {
        // 1) Guardar en el contexto
        SummaryContext ctx = SummaryContext.getInstance();
        ctx.setSelectedFunction(currentFunction);
        ctx.setSelectedDateTime(selectedDateTime);
        ctx.setSelectedSeats(new ArrayList<>(selectedSeats));

        // 2) Cerrar esta ventana
        Stage thisStage = (Stage) btnConfirm.getScene().getWindow();
        thisStage.close();

        // 3) Abrir selección de productos (modal)
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/product-selection-view.fxml")
            );
            Parent root = loader.load();
            Stage prodStage = new Stage();
            prodStage.initModality(Modality.APPLICATION_MODAL);
            prodStage.setTitle("Agregar Productos (Opcional)");
            prodStage.setScene(new Scene(root));
            prodStage.showAndWait();
            // al cerrar esa ventana, el flujo seguirá al PurchaseSummaryController.showSummary()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Cancela y cierra sin guardar nada. */
    @FXML
    private void handleCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
