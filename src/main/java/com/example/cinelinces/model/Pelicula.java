package com.example.cinelinces.model;

import java.time.LocalDate;

public class Pelicula {
    private int idPelicula;
    private String titulo;
    private int duracion; // en minutos
    private String sinopsis;
    private LocalDate fechaEstreno;
    private String clasificacion;
    private String idioma;
    private boolean subtitulada;
    private String fotografia; // Corresponde a tu posterUrl
    private String formato;
    private String estado;
    private Integer idEstudio; // Puede ser Integer si es nullable
    private Integer idDirector; // Puede ser Integer si es nullable
    private Integer idTipoPelicula; // Puede ser Integer si es nullable

    // Campos adicionales que podrías llenar con el DAO (opcional aquí)
    // private String nombreEstudio;
    // private String nombreDirector;
    // private String nombreTipoPelicula;
    // private List<Actor> actores; // o List<String> nombresActores

    public Pelicula() {
    }

    public Pelicula(int idPelicula, String titulo, int duracion, String sinopsis, LocalDate fechaEstreno, String clasificacion, String idioma, boolean subtitulada, String fotografia, String formato, String estado, Integer idEstudio, Integer idDirector, Integer idTipoPelicula) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.duracion = duracion;
        this.sinopsis = sinopsis;
        this.fechaEstreno = fechaEstreno;
        this.clasificacion = clasificacion;
        this.idioma = idioma;
        this.subtitulada = subtitulada;
        this.fotografia = fotografia;
        this.formato = formato;
        this.estado = estado;
        this.idEstudio = idEstudio;
        this.idDirector = idDirector;
        this.idTipoPelicula = idTipoPelicula;
    }

    // Getters y Setters
    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public LocalDate getFechaEstreno() {
        return fechaEstreno;
    }

    public void setFechaEstreno(LocalDate fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public boolean isSubtitulada() {
        return subtitulada;
    }

    public void setSubtitulada(boolean subtitulada) {
        this.subtitulada = subtitulada;
    }

    public String getFotografia() {
        return fotografia;
    }

    public void setFotografia(String fotografia) {
        this.fotografia = fotografia;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getIdEstudio() {
        return idEstudio;
    }

    public void setIdEstudio(Integer idEstudio) {
        this.idEstudio = idEstudio;
    }

    public Integer getIdDirector() {
        return idDirector;
    }

    public void setIdDirector(Integer idDirector) {
        this.idDirector = idDirector;
    }

    public Integer getIdTipoPelicula() {
        return idTipoPelicula;
    }

    public void setIdTipoPelicula(Integer idTipoPelicula) {
        this.idTipoPelicula = idTipoPelicula;
    }

    // Método para obtener el año del título, si está presente
    public String getYearFromTitle() {
        if (titulo != null) {
            int open = titulo.lastIndexOf('(');
            int close = titulo.lastIndexOf(')');
            if (open > 0 && close > open && (close == titulo.length() - 1) ) { // Asegura que esté al final
                try {
                    // Verifica si es un año de 4 dígitos
                    String yearStr = titulo.substring(open + 1, close);
                    if(yearStr.matches("\\d{4}")) {
                        return yearStr;
                    }
                } catch (NumberFormatException e) {
                    // No es un número, o no es el formato esperado
                }
            }
        }
        return ""; // O podrías devolver null o lanzar una excepción
    }
}