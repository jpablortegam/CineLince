package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FuncionDetallada {
    // Campos de la Función base
    private int idFuncion;
    private LocalDateTime fechaHoraFuncion;
    private BigDecimal precioBoleto;
    private String estadoFuncion;

    // Campos de la Película
    private int idPelicula;
    private String tituloPelicula;
    private int duracionMinutos;
    private String clasificacionPelicula; // Already exists, good for "Clasificación"
    private String sinopsisPelicula;
    private String fotografiaPelicula; // URL o ruta del póster
    private LocalDate fechaEstrenoPelicula;
    private String nombreTipoPelicula; // Género

    // Campos de la Sala
    private int idSala;
    private int numeroSala;
    private String tipoSala;

    // Campo del Cine
    private String nombreCine;

    // Campo para los actores
    private List<ActorPeliculaDTO> actores;

    // --- NUEVOS CAMPOS ---
    private String nombreEstudio;
    private String nombreDirector;
    private String idiomaPelicula;
    private boolean subtituladaPelicula;
    // --- FIN DE NUEVOS CAMPOS ---


    public FuncionDetallada() {
        this.actores = new ArrayList<>();
    }

    // --- Getters y Setters existentes ---
    public int getIdFuncion() { return idFuncion; }
    public void setIdFuncion(int idFuncion) { this.idFuncion = idFuncion; }
    public LocalDateTime getFechaHoraFuncion() { return fechaHoraFuncion; }
    public void setFechaHoraFuncion(LocalDateTime fechaHoraFuncion) { this.fechaHoraFuncion = fechaHoraFuncion; }
    public BigDecimal getPrecioBoleto() { return precioBoleto; }
    public void setPrecioBoleto(BigDecimal precioBoleto) { this.precioBoleto = precioBoleto; }
    public String getEstadoFuncion() { return estadoFuncion; }
    public void setEstadoFuncion(String estadoFuncion) { this.estadoFuncion = estadoFuncion; }
    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }
    public String getTituloPelicula() { return tituloPelicula; }
    public void setTituloPelicula(String tituloPelicula) { this.tituloPelicula = tituloPelicula; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public String getClasificacionPelicula() { return clasificacionPelicula; }
    public void setClasificacionPelicula(String clasificacionPelicula) { this.clasificacionPelicula = clasificacionPelicula; }
    public String getSinopsisPelicula() { return sinopsisPelicula; }
    public void setSinopsisPelicula(String sinopsisPelicula) { this.sinopsisPelicula = sinopsisPelicula; }
    public String getFotografiaPelicula() { return fotografiaPelicula; }
    public void setFotografiaPelicula(String fotografiaPelicula) { this.fotografiaPelicula = fotografiaPelicula; }
    public LocalDate getFechaEstrenoPelicula() { return fechaEstrenoPelicula; }
    public void setFechaEstrenoPelicula(LocalDate fechaEstrenoPelicula) { this.fechaEstrenoPelicula = fechaEstrenoPelicula; }
    public String getNombreTipoPelicula() { return nombreTipoPelicula; }
    public void setNombreTipoPelicula(String nombreTipoPelicula) { this.nombreTipoPelicula = nombreTipoPelicula; }
    public int getIdSala() { return idSala; }
    public void setIdSala(int idSala) { this.idSala = idSala; }
    public int getNumeroSala() { return numeroSala; }
    public void setNumeroSala(int numeroSala) { this.numeroSala = numeroSala; }
    public String getTipoSala() { return tipoSala; }
    public void setTipoSala(String tipoSala) { this.tipoSala = tipoSala; }
    public String getNombreCine() { return nombreCine; }
    public void setNombreCine(String nombreCine) { this.nombreCine = nombreCine; }
    public List<ActorPeliculaDTO> getActores() { return actores; }
    public void setActores(List<ActorPeliculaDTO> actores) { this.actores = actores; }

    // --- Getters y Setters para NUEVOS CAMPOS ---
    public String getNombreEstudio() { return nombreEstudio; }
    public void setNombreEstudio(String nombreEstudio) { this.nombreEstudio = nombreEstudio; }
    public String getNombreDirector() { return nombreDirector; }
    public void setNombreDirector(String nombreDirector) { this.nombreDirector = nombreDirector; }
    public String getIdiomaPelicula() { return idiomaPelicula; }
    public void setIdiomaPelicula(String idiomaPelicula) { this.idiomaPelicula = idiomaPelicula; }
    public boolean isSubtituladaPelicula() { return subtituladaPelicula; }
    public void setSubtituladaPelicula(boolean subtituladaPelicula) { this.subtituladaPelicula = subtituladaPelicula; }
}