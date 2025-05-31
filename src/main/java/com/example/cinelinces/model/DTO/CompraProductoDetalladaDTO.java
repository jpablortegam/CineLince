package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;

public class CompraProductoDetalladaDTO {
    private int idDetalleVenta;
    private int idVenta;
    private int idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public CompraProductoDetalladaDTO(int idDetalleVenta,
                                      int idVenta,
                                      int idProducto,
                                      String nombreProducto,
                                      String descripcionProducto,
                                      int cantidad,
                                      BigDecimal precioUnitario,
                                      BigDecimal subtotal) {
        this.idDetalleVenta = idDetalleVenta;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public int getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(int idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
