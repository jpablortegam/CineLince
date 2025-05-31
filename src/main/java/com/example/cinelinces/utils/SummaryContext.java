// SummaryContext.java
package com.example.cinelinces.utils;

import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SummaryContext {
    private static SummaryContext instance;

    private FuncionDetallada selectedFunction;
    private LocalDateTime selectedDateTime;
    private List<AsientoDTO> selectedSeats = new ArrayList<>();
    private List<ProductoSelectionDTO> selectedProducts = new ArrayList<>();

    private SummaryContext() {}

    public static SummaryContext getInstance() {
        if (instance == null) instance = new SummaryContext();
        return instance;
    }

    public FuncionDetallada getSelectedFunction() {
        return selectedFunction;
    }

    public void setSelectedFunction(FuncionDetallada f) {
        this.selectedFunction = f;
    }

    public LocalDateTime getSelectedDateTime() {
        return selectedDateTime;
    }

    public void setSelectedDateTime(LocalDateTime dt) {
        this.selectedDateTime = dt;
    }

    public List<AsientoDTO> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<AsientoDTO> seats) {
        this.selectedSeats = seats;
    }

    public List<ProductoSelectionDTO> getSelectedProducts() {
        return selectedProducts;
    }

    public void setSelectedProducts(List<ProductoSelectionDTO> prods) {
        this.selectedProducts = prods;
    }
}
