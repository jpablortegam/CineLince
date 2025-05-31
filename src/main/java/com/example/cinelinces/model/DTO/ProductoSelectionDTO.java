// ProductoSelectionDTO.java
package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;

public class ProductoSelectionDTO {
    private final int idProducto;
    private final String nombre;
    private final BigDecimal precioUnitario;
    private int cantidad;

    public ProductoSelectionDTO(int idProducto, String nombre, BigDecimal precioUnitario, int cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
