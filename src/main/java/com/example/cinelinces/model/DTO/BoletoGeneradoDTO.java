package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BoletoGeneradoDTO {
    private int idBoleto;
    private String codigoQR;
    private int idAsiento;
    private String filaAsiento; // Fila del asiento (ej. A)
    private int numeroAsiento; // Número del asiento (ej. 5)
    private BigDecimal precioFinal; // Precio final de este boleto
    private LocalDateTime fechaCompra; // Fecha y hora de compra de este boleto
    private int idFuncion; // ID de la función a la que pertenece este boleto

    // Constructor
    public BoletoGeneradoDTO(int idBoleto, String codigoQR, int idAsiento, String filaAsiento, int numeroAsiento, BigDecimal precioFinal, LocalDateTime fechaCompra, int idFuncion) {
        this.idBoleto = idBoleto;
        this.codigoQR = codigoQR;
        this.idAsiento = idAsiento;
        this.filaAsiento = filaAsiento;
        this.numeroAsiento = numeroAsiento;
        this.precioFinal = precioFinal;
        this.fechaCompra = fechaCompra;
        this.idFuncion = idFuncion;
    }

    // Getters
    public int getIdBoleto() {
        return idBoleto;
    }

    public String getCodigoQR() {
        return codigoQR;
    }

    public int getIdAsiento() {
        return idAsiento;
    }

    public String getFilaAsiento() {
        return filaAsiento;
    }

    public int getNumeroAsiento() {
        return numeroAsiento;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public int getIdFuncion() {
        return idFuncion;
    }

    // Setters (si necesitas modificarlos después de la creación)
    public void setIdBoleto(int idBoleto) {
        this.idBoleto = idBoleto;
    }

    public void setCodigoQR(String codigoQR) {
        this.codigoQR = codigoQR;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public void setFilaAsiento(String filaAsiento) {
        this.filaAsiento = filaAsiento;
    }

    public void setNumeroAsiento(int numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }
}