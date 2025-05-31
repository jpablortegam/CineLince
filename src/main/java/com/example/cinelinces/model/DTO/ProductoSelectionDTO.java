package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;

public class ProductoSelectionDTO {
    private final int idProducto;
    private final String nombre;
    private final BigDecimal precioUnitario;
    private int cantidad;

    /**
     * Constructor principal para cuando se crea un ProductoSelectionDTO manualmente
     * (por ejemplo, desde la UI antes de guardar en BD).
     */
    public ProductoSelectionDTO(int idProducto, String nombre, BigDecimal precioUnitario, int cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    /**
     * Constructor que usa el DAO al leer de la BD. Recibe también el subtotal,
     * pero como el DTO puede calcularlo usando precioUnitario * cantidad, ignoramos
     * directamente el BigDecimal subtotal que viene de BD.
     */
    public ProductoSelectionDTO(int idProducto,
                                String nombreProducto,
                                int cantidad,
                                BigDecimal precioUnitario,
                                BigDecimal subtotal /* <- este valor se ignora porque getSubtotal() lo calcula */) {
        this.idProducto = idProducto;
        this.nombre = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        // Nota: no almacenamos `subtotal`, ya que getSubtotal() hará precioUnitario * cantidad.
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

    /**
     * Calcula el subtotal en base a precioUnitario * cantidad.
     */
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
