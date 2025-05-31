package com.example.cinelinces.model.DTO;

import java.io.Serializable;

public class AsientoDTO implements Serializable {
    private int idAsiento;
    private String fila;
    private int numero;
    private String tipoAsiento;
    private String estado;

    public AsientoDTO() {
    }

    public AsientoDTO(int idAsiento, String fila, int numero, String tipoAsiento, String estado) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.tipoAsiento = tipoAsiento;
        this.estado = estado;
    }

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

    @Override
    public String toString() {
        return String.format("%s%d (%s) - %s", fila, numero, tipoAsiento, estado != null ? estado : "Estado no definido");
    }
}