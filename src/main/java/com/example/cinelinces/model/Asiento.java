package com.example.cinelinces.model;

public class Asiento {
    private int idAsiento;
    private String fila; // CHAR(2) en BD
    private int numero;
    private String tipoAsiento;
    private String estado;
    private int idSala;

    public Asiento() {
    }

    public Asiento(int idAsiento, String fila, int numero, String tipoAsiento, String estado, int idSala) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.tipoAsiento = tipoAsiento;
        this.estado = estado;
        this.idSala = idSala;
    }

    // Getters y Setters
    public int getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTipoAsiento() {
        return tipoAsiento;
    }

    public void setTipoAsiento(String tipoAsiento) {
        this.tipoAsiento = tipoAsiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }
}