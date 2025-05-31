// ProductoDTO.java
package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;

public class ProductoDTO {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stock;

    public ProductoDTO(int idProducto, String nombre, String descripcion, BigDecimal precio, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    public int getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
}
