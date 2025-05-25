package com.example.cinelinces.model;

import java.math.BigDecimal;

public class Membresia {
    private int idMembresia;
    private String tipo;
    private String descripcion;
    private BigDecimal costo;
    private int duracionMeses;
    private String beneficiosDescripcion;

    public Membresia() {
    }

    public Membresia(int idMembresia, String tipo, String descripcion, BigDecimal costo, int duracionMeses, String beneficiosDescripcion) {
        this.idMembresia = idMembresia;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.costo = costo;
        this.duracionMeses = duracionMeses;
        this.beneficiosDescripcion = beneficiosDescripcion;
    }

    // Getters y Setters
    public int getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(int idMembresia) {
        this.idMembresia = idMembresia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public int getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(int duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public String getBeneficiosDescripcion() {
        return beneficiosDescripcion;
    }

    public void setBeneficiosDescripcion(String beneficiosDescripcion) {
        this.beneficiosDescripcion = beneficiosDescripcion;
    }
}