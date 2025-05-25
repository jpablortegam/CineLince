package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String clasificacionPelicula;
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

    public FuncionDetallada() {
    }

    // Getters y Setters para todos los campos...
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
}
