package com.example.cinelinces.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Funcion {
    private int idFuncion;
    private LocalDateTime fechaHora;
    private BigDecimal precio;
    private String estado;
    private int idPelicula;
    private int idSala;

    public Funcion() {
    }

    public Funcion(int idFuncion, LocalDateTime fechaHora, BigDecimal precio, String estado, int idPelicula, int idSala) {
        this.idFuncion = idFuncion;
        this.fechaHora = fechaHora;
        this.precio = precio;
        this.estado = estado;
        this.idPelicula = idPelicula;
        this.idSala = idSala;
    }

    // Getters y Setters
    public int getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }
}