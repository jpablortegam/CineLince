package com.example.cinelinces.model;

public class Estudio {
    private int idEstudio;
    private String nombre;
    private String pais;
    private String descripcion;

    public Estudio() {
    }

    public Estudio(int idEstudio, String nombre, String pais, String descripcion) {
        this.idEstudio = idEstudio;
        this.nombre = nombre;
        this.pais = pais;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdEstudio() {
        return idEstudio;
    }

    public void setIdEstudio(int idEstudio) {
        this.idEstudio = idEstudio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}