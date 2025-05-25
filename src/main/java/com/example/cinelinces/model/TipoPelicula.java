package com.example.cinelinces.model;

public class TipoPelicula {
    private int idTipoPelicula;
    private String nombre;
    private String descripcion;

    public TipoPelicula() {
    }

    public TipoPelicula(int idTipoPelicula, String nombre, String descripcion) {
        this.idTipoPelicula = idTipoPelicula;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdTipoPelicula() {
        return idTipoPelicula;
    }

    public void setIdTipoPelicula(int idTipoPelicula) {
        this.idTipoPelicula = idTipoPelicula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}