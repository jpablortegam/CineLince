package com.example.cinelinces.model;

public class TipoPelicula {
    private Integer idTipoPelicula;
    private String nombre; // Corresponde a la columna 'Nombre' en tu BD
    private String descripcion; // Corresponde a la columna 'Descripcion' en tu BD

    public TipoPelicula() {
    }

    public TipoPelicula(Integer idTipoPelicula, String nombre, String descripcion) {
        this.idTipoPelicula = idTipoPelicula;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Integer getIdTipoPelicula() {
        return idTipoPelicula;
    }

    public void setIdTipoPelicula(Integer idTipoPelicula) {
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

    @Override
    public String toString() {
        return nombre; // Esto es útil para mostrar el nombre del género en el ComboBox
    }
}