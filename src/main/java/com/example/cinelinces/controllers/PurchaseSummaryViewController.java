package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.Producto;
import com.example.cinelinces.utils.SessionManager;
import com.example.cinelinces.utils.SummaryContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PurchaseSummaryViewController {
    @FXML private ListView<String> summaryList;
    @FXML private Label totalLabel;

    public static void showSummary() {
        // Similar a otros, cargar FXML en nuevo Stage...
    }

    @FXML
    public void initialize() {
        SummaryContext ctx = SummaryContext.getInstance();
        List<String> items = new ArrayList<>();

        // Película y hora
        items.add("Pelicula: " + ctx.getSelectedFunction().getTituloPelicula());
        items.add("Horario: " +
                ctx.getSelectedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm 'hrs'"))
        );

        // Asientos
        ctx.getSelectedSeats().forEach(a ->
                items.add("Asiento: " + a.getFila() + a.getNumero())
        );

        // Productos
        BigDecimal total = BigDecimal.ZERO;
        for (Producto p : ctx.getSelectedProducts()) {
            items.add("Producto: " + p.getNombre() + " x1 = $" + p.getPrecio());
            total = total.add(p.getPrecio());
        }

        // Calcular total asientos (ejemplo: precioBoleto * cantidad)
        BigDecimal seatSum = BigDecimal.ZERO;
        // ... recorre ctx.getSelectedSeats() y suma precioBoleto de ctx.getSelectedFunction()

        total = total.add(seatSum);
        summaryList.setItems(FXCollections.observableArrayList(items));
        totalLabel.setText("Total: $" + total);
    }

    @FXML
    private void handleConfirmPurchase() {
        if (SessionManager.getInstance().isLoggedIn()) {
            // Guardar en BD usando CompraDAO...
        } else {
            // Mostrar alerta: "Inicia sesión para completar la compra"
        }
        ((Stage) totalLabel.getScene().getWindow()).close();
    }
}