package com.example.cinelinces.model;

import java.math.BigDecimal;

public class CartItem {
    private CartItemType type;
    private int idFuncion;
    private int idProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    // Constructor para boletos
    public CartItem(int idFuncion, int cantidad, BigDecimal precioUnitario) {
        this.type = CartItemType.TICKET;
        this.idFuncion = idFuncion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    // Constructor para productos
    public CartItem(int idProducto, int cantidad, BigDecimal precioUnitario, boolean isProduct) {
        this.type = CartItemType.PRODUCT;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    // Getters y setters
    public CartItemType getType() { return type; }
    public int getIdFuncion() { return idFuncion; }
    public int getIdProducto() { return idProducto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}