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
 *
 * Flujo:
 *   1) El usuario elige asientos → guarda datos en SummaryContext
 *   2) Cierra esta ventana
 *   3) Abre product-selection-view.fxml (modal). [Optional]
 *   4) Al cerrar la ventana de productos, abre purchase-summary-view.fxml (modal)
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
     * Este método debe llamarse desde el controlador padre justo después
     * de cargar el FXML, para pasar la función y la fecha/hora.
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

    /**
     * Consulta todos los asientos de la sala (currentFunction.getIdSala()),
     * marca como “ocupados” los que ya están reservados para esta función,
     * y permite seleccionar/des-seleccionar el resto.
     */
    private void loadSeats() {
        // 1) Obtener todos los asientos de la sala de la función
        seats = asientoDAO.findAsientosBySala(currentFunction.getIdSala());

        // 2) Obtener IDs de asientos ya reservados para esta función
        List<Integer> bookedIds =
                asientoDAO.findBookedSeatIdsByFuncion(currentFunction.getIdFuncion());

        // 3) Construir la grilla con ToggleButtons
        int cols = 8;
        int row = 0, col = 0;
        for (AsientoDTO asiento : seats) {
            ToggleButton btn = new ToggleButton(asiento.getFila() + asiento.getNumero());
            btn.getStyleClass().add("seat-button");
            btn.setUserData(asiento);

            // Si está reservado, deshabilitar y marcar como “ocupado”
            if (bookedIds.contains(asiento.getIdAsiento())) {
                btn.setDisable(true);
                btn.getStyleClass().add("seat-occupied");
            } else {
                // asiento libre: aplicar estilo “available” y listener
                btn.getStyleClass().add("seat-available");
                btn.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                    if (isNowSelected) {
                        btn.getStyleClass().remove("seat-available");
                        btn.getStyleClass().add("seat-selected");
                        selectedSeats.add(asiento);
                    } else {
                        btn.getStyleClass().remove("seat-selected");
                        btn.getStyleClass().add("seat-available");
                        selectedSeats.remove(asiento);
                    }
                    // Mostrar cuántos asientos lleva seleccionados
                    selectedCountLabel.setText(String.valueOf(selectedSeats.size()));
                });
            }

            seatGrid.add(btn, col, row);
            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Cuando el usuario hace clic en “Confirmar”:
     * 1) Guarda currentFunction, selectedDateTime y selectedSeats en SummaryContext.
     * 2) Cierra la ventana de asientos.
     * 3) Abre product-selection-view.fxml como modal.
     * 4) Al cerrar la ventana de productos, abre purchase-summary-view.fxml como modal.
     */
    @FXML
    private void handleConfirm() {
        // 1) Guardar en el contexto global
        SummaryContext ctx = SummaryContext.getInstance();
        ctx.setSelectedFunction(currentFunction);
        ctx.setSelectedDateTime(selectedDateTime);
        ctx.setSelectedSeats(new ArrayList<>(selectedSeats));

        // 2) Cerrar esta ventana de selección de asientos
        Stage thisStage = (Stage) btnConfirm.getScene().getWindow();
        thisStage.close();

        // 3) Abrir la ventana de selección de productos (modal)
        try {
            FXMLLoader loaderProd = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/product-selection-view.fxml")
            );
            Parent rootProd = loaderProd.load();
            Stage prodStage = new Stage();
            prodStage.initModality(Modality.APPLICATION_MODAL);
            prodStage.setTitle("Agregar Productos (Opcional)");
            prodStage.setScene(new Scene(rootProd));
            prodStage.setResizable(false);
            prodStage.showAndWait();
            // Al cerrar product-selection-view.fxml, el flujo sigue aquí

            // 4) Abrir la ventana de Resumen de compra (modal)
            FXMLLoader loaderSummary = new FXMLLoader(
                    getClass().getResource("/com/example/cinelinces/purchase-summary-view.fxml")
            );
            Parent rootSummary = loaderSummary.load();
            Stage summaryStage = new Stage();
            summaryStage.initModality(Modality.APPLICATION_MODAL);
            summaryStage.setTitle("Resumen de compra");
            summaryStage.setScene(new Scene(rootSummary));
            summaryStage.setResizable(false);
            summaryStage.showAndWait();
            // Al cerrar la ventana de resumen, finaliza el flujo

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Si el usuario hace clic en “Cancelar”, solo cierra esta ventana
     * (sin guardar nada en el contexto).
     */
    @FXML
    private void handleCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
