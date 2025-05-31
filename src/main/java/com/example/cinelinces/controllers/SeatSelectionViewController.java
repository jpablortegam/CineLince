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
import javafx.scene.control.Alert;
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

public class SeatSelectionViewController {

    @FXML
    private Label titleLabel;
    @FXML
    private GridPane seatGrid;
    @FXML
    private Label selectedCountLabel;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;

    private FuncionDetallada currentFunction;
    private LocalDateTime selectedDateTime;
    private List<AsientoDTO> seats;
    private final List<AsientoDTO> selectedSeats = new ArrayList<>();

    private final AsientoDAO asientoDAO = new AsientoDAOImpl();

    public void initData(FuncionDetallada function, LocalDateTime dateTime) {
        this.currentFunction = function;
        this.selectedDateTime = dateTime;

        titleLabel.setText(
                function.getTituloPelicula() + "  |  " +
                        dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm 'hrs'"))
        );
        loadSeats();
        updateSelectedCount();
    }

    private void loadSeats() {
        if (currentFunction == null) return;
        seatGrid.getChildren().clear();
        selectedSeats.clear();

        seats = asientoDAO.findAsientosBySala(currentFunction.getIdSala());
        List<Integer> bookedIds =
                asientoDAO.findBookedSeatIdsByFuncion(currentFunction.getIdFuncion());

        int cols = 10;
        int row = 0, col = 0;
        if (seats != null) {
            for (AsientoDTO asiento : seats) {
                ToggleButton btn = new ToggleButton(asiento.getFila() + asiento.getNumero());
                btn.setUserData(asiento);
                btn.getStyleClass().add("seat-button");
                if (asiento.getEstado() != null && asiento.getEstado().equalsIgnoreCase("Mantenimiento")) {
                    btn.setDisable(true);
                    btn.getStyleClass().add("seat-unavailable");
                } else if (bookedIds.contains(asiento.getIdAsiento())) {
                    btn.setDisable(true);
                    btn.getStyleClass().add("seat-occupied");
                } else {
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
                        updateSelectedCount();
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
    }

    private void updateSelectedCount() {
        selectedCountLabel.setText(String.valueOf(selectedSeats.size()));
    }

    @FXML
    private void handleConfirm() {
        if (selectedSeats.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selección Vacía");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona al menos un asiento para continuar.");
            alert.showAndWait();
            return;
        }

        SummaryContext ctx = SummaryContext.getInstance();
        ctx.setSelectedFunction(currentFunction);
        ctx.setSelectedDateTime(selectedDateTime);
        ctx.setSelectedSeats(new ArrayList<>(selectedSeats));
        Stage thisStage = (Stage) btnConfirm.getScene().getWindow();
        thisStage.close();
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

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Navegación");
            alert.setHeaderText("No se pudo abrir la siguiente ventana.");
            alert.setContentText("Detalle: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        selectedSeats.clear();
        updateSelectedCount();
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}