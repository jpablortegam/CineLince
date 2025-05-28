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
    // Getters y setters omitidos
}