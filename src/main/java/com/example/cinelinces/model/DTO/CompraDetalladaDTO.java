package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompraDetalladaDTO {
    // —— Datos de Boleto ——
    private int    idBoleto;
    private BigDecimal precioFinal;
    private LocalDateTime fechaCompra;
    private String codigoQR;
    private int    idAsiento;

    // —— Datos de Venta ——
    private int    idVenta;
    private BigDecimal totalVenta;
    private String metodoPago;
    private String estadoVenta;
    private boolean facturado;
    private Integer idPromocion;

    // —— Datos de Función + Película + Sala + Cine ——
    private FuncionDetallada funcion;

    public CompraDetalladaDTO() {}

    public CompraDetalladaDTO(
            int idBoleto,
            BigDecimal precioFinal,
            LocalDateTime fechaCompra,
            String codigoQR,
            int idAsiento,
            int idVenta,
            BigDecimal totalVenta,
            String metodoPago,
            String estadoVenta,
            boolean facturado,
            Integer idPromocion,
            FuncionDetallada funcion
    ) {
        this.idBoleto     = idBoleto;
        this.precioFinal  = precioFinal;
        this.fechaCompra  = fechaCompra;
        this.codigoQR     = codigoQR;
        this.idAsiento    = idAsiento;
        this.idVenta      = idVenta;
        this.totalVenta   = totalVenta;
        this.metodoPago   = metodoPago;
        this.estadoVenta  = estadoVenta;
        this.facturado    = facturado;
        this.idPromocion  = idPromocion;
        this.funcion      = funcion;
    }

    // — Getters & Setters —

    public int getIdBoleto() { return idBoleto; }
    public void setIdBoleto(int idBoleto) { this.idBoleto = idBoleto; }

    public BigDecimal getPrecioFinal() { return precioFinal; }
    public void setPrecioFinal(BigDecimal precioFinal) { this.precioFinal = precioFinal; }

    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }

    public String getCodigoQR() { return codigoQR; }
    public void setCodigoQR(String codigoQR) { this.codigoQR = codigoQR; }

    public int getIdAsiento() { return idAsiento; }
    public void setIdAsiento(int idAsiento) { this.idAsiento = idAsiento; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public BigDecimal getTotalVenta() { return totalVenta; }
    public void setTotalVenta(BigDecimal totalVenta) { this.totalVenta = totalVenta; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstadoVenta() { return estadoVenta; }
    public void setEstadoVenta(String estadoVenta) { this.estadoVenta = estadoVenta; }

    public boolean isFacturado() { return facturado; }
    public void setFacturado(boolean facturado) { this.facturado = facturado; }

    public Integer getIdPromocion() { return idPromocion; }
    public void setIdPromocion(Integer idPromocion) { this.idPromocion = idPromocion; }

    public FuncionDetallada getFuncion() { return funcion; }
    public void setFuncion(FuncionDetallada funcion) { this.funcion = funcion; }
}
