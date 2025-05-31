package com.example.cinelinces.utils;

import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SummaryContext es un singleton que conserva el estado temporal de la compra
 * mientras el usuario recorre el flujo de seleccionar asientos → seleccionar productos → resumen.
 */
public class SummaryContext {
    private static SummaryContext instance;

    // 1) Lista de AsientoDTO seleccionados
    private List<AsientoDTO> selectedSeats = new ArrayList<>();

    // 2) Información de la función elegida (incluye película, sala, precio boleto, etc.)
    private FuncionDetallada selectedFunction;

    // 3) Fecha y hora de la función seleccionada
    private LocalDateTime selectedDateTime;

    // 4) Lista de productos elegidos (ProductoSelectionDTO incluye cantidad, subtotal, etc.)
    private List<ProductoSelectionDTO> selectedProducts = new ArrayList<>();

    // 5) Método de pago elegido (por ejemplo: "Tarjeta", "Efectivo", ...
    private String metodoPago;

    // 6) Código de promoción ingresado (opcional)
    private String codigoPromocion;

    // 7) Después de insertar la compra en BD, se puede almacenar aquí el DTO con todos los detalles
    private CompraDetalladaDTO ultimaCompraDetallada;

    // Constructor privado: patrón singleton
    private SummaryContext() {}

    /**
     * Devuelve la única instancia de SummaryContext.
     */
    public static synchronized SummaryContext getInstance() {
        if (instance == null) {
            instance = new SummaryContext();
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1) Asientos seleccionados
    // ─────────────────────────────────────────────────────────────────────────
    public List<AsientoDTO> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<AsientoDTO> seats) {
        this.selectedSeats = seats;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2) Función detallada (película, sala, precio, etc.)
    // ─────────────────────────────────────────────────────────────────────────
    public FuncionDetallada getSelectedFunction() {
        return selectedFunction;
    }

    public void setSelectedFunction(FuncionDetallada func) {
        this.selectedFunction = func;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3) Fecha y hora de la función seleccionada
    // ─────────────────────────────────────────────────────────────────────────
    public LocalDateTime getSelectedDateTime() {
        return selectedDateTime;
    }

    public void setSelectedDateTime(LocalDateTime selectedDateTime) {
        this.selectedDateTime = selectedDateTime;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4) Productos seleccionados (dulcería)
    // ─────────────────────────────────────────────────────────────────────────
    public List<ProductoSelectionDTO> getSelectedProducts() {
        return selectedProducts;
    }

    public void setSelectedProducts(List<ProductoSelectionDTO> prods) {
        this.selectedProducts = prods;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5) Método de pago elegido
    // ─────────────────────────────────────────────────────────────────────────
    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodo) {
        this.metodoPago = metodo;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6) Código de promoción (opcional)
    // ─────────────────────────────────────────────────────────────────────────
    public String getCodigoPromocion() {
        return codigoPromocion;
    }

    public void setCodigoPromocion(String codigoPromo) {
        this.codigoPromocion = codigoPromo;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 7) DTO con toda la compra guardada (boleto, productos, QR, etc.)
    // ─────────────────────────────────────────────────────────────────────────
    public CompraDetalladaDTO getUltimaCompraDetallada() {
        return ultimaCompraDetallada;
    }

    public void setUltimaCompraDetallada(CompraDetalladaDTO dto) {
        this.ultimaCompraDetallada = dto;
    }
}
