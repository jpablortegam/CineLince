package com.example.cinelinces.utils;

import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SummaryContext {
    private static SummaryContext instance;


    private List<AsientoDTO> selectedSeats = new ArrayList<>();

    private FuncionDetallada selectedFunction;

    private LocalDateTime selectedDateTime;

    private List<ProductoSelectionDTO> selectedProducts = new ArrayList<>();

    private String metodoPago;

    private String codigoPromocion;

    private CompraDetalladaDTO ultimaCompraDetallada;

    private SummaryContext() {
    }

    public static synchronized SummaryContext getInstance() {
        if (instance == null) {
            instance = new SummaryContext();
        }
        return instance;
    }

    public List<AsientoDTO> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<AsientoDTO> seats) {
        this.selectedSeats = seats;
    }

    public FuncionDetallada getSelectedFunction() {
        return selectedFunction;
    }

    public void setSelectedFunction(FuncionDetallada func) {
        this.selectedFunction = func;
    }

    public LocalDateTime getSelectedDateTime() {
        return selectedDateTime;
    }

    public void setSelectedDateTime(LocalDateTime selectedDateTime) {
        this.selectedDateTime = selectedDateTime;
    }

    public List<ProductoSelectionDTO> getSelectedProducts() {
        return selectedProducts;
    }

    public void setSelectedProducts(List<ProductoSelectionDTO> prods) {
        this.selectedProducts = prods;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodo) {
        this.metodoPago = metodo;
    }

    public String getCodigoPromocion() {
        return codigoPromocion;
    }

    public void setCodigoPromocion(String codigoPromo) {
        this.codigoPromocion = codigoPromo;
    }

    public CompraDetalladaDTO getUltimaCompraDetallada() {
        return ultimaCompraDetallada;
    }

    public void setUltimaCompraDetallada(CompraDetalladaDTO dto) {
        this.ultimaCompraDetallada = dto;
    }
}
