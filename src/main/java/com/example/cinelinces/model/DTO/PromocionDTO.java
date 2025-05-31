package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;

public class PromocionDTO {
    private final int id;
    private final String nombre;
    private final BigDecimal descuento; // e.g. 0.10 = 10%
    private final String codigo;

    public PromocionDTO(int id, String nombre, BigDecimal descuento, String codigo) {
        this.id = id;
        this.nombre = nombre;
        this.descuento = descuento;
        this.codigo = codigo;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }
}
