package com.example.cinelinces.utils;

import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.Producto;

import java.time.LocalDateTime;
import java.util.List;

public class SummaryContext {
    private static final SummaryContext INSTANCE = new SummaryContext();

    private FuncionDetallada selectedFunction;
    private LocalDateTime selectedDateTime;
    private List<AsientoDTO> selectedSeats;
    private List<Producto> selectedProducts; // <-- NUEVO

    private SummaryContext() {}

    public static SummaryContext getInstance() { return INSTANCE; }

    public FuncionDetallada getSelectedFunction() { return selectedFunction; }
    public void setSelectedFunction(FuncionDetallada f) { this.selectedFunction = f; }

    public LocalDateTime getSelectedDateTime() { return selectedDateTime; }
    public void setSelectedDateTime(LocalDateTime dt) { this.selectedDateTime = dt; }

    public List<AsientoDTO> getSelectedSeats() { return selectedSeats; }
    public void setSelectedSeats(List<AsientoDTO> seats) { this.selectedSeats = seats; }

    public List<Producto> getSelectedProducts() { return selectedProducts; }
    public void setSelectedProducts(List<Producto> products) { this.selectedProducts = products; }
}