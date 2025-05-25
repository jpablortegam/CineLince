package com.example.cinelinces.model;

public class Sala {
    private int idSala;
    private int numero;
    private int capacidad;
    private String tipoSala;
    private String estado;
    private int idCine;

    public Sala() {
    }

    public Sala(int idSala, int numero, int capacidad, String tipoSala, String estado, int idCine) {
        this.idSala = idSala;
        this.numero = numero;
        this.capacidad = capacidad;
        this.tipoSala = tipoSala;
        this.estado = estado;
        this.idCine = idCine;
    }

    // Getters y Setters
    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipoSala() {
        return tipoSala;
    }

    public void setTipoSala(String tipoSala) {
        this.tipoSala = tipoSala;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdCine() {
        return idCine;
    }

    public void setIdCine(int idCine) {
        this.idCine = idCine;
    }
}