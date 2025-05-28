package com.example.cinelinces.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Boleto {
    private int idBoleto;
    private BigDecimal precioFinal;
    private LocalDateTime fechaCompra;
    private String codigoQR;
    private int idFuncion;
    private int idCliente;
    private int idAsiento;
    private int idVenta;

    public Boleto() {}

    public Boleto(int idBoleto, BigDecimal precioFinal, LocalDateTime fechaCompra, String codigoQR,
                  int idFuncion, int idCliente, int idAsiento, int idVenta) {
        this.idBoleto = idBoleto;
        this.precioFinal = precioFinal;
        this.fechaCompra = fechaCompra;
        this.codigoQR = codigoQR;
        this.idFuncion = idFuncion;
        this.idCliente = idCliente;
        this.idAsiento = idAsiento;
        this.idVenta = idVenta;
    }

    // Getters y setters
    public int getIdBoleto() { return idBoleto; }
    public void setIdBoleto(int idBoleto) { this.idBoleto = idBoleto; }

    public BigDecimal getPrecioFinal() { return precioFinal; }
    public void setPrecioFinal(BigDecimal precioFinal) { this.precioFinal = precioFinal; }

    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }

    public String getCodigoQR() { return codigoQR; }
    public void setCodigoQR(String codigoQR) { this.codigoQR = codigoQR; }

    public int getIdFuncion() { return idFuncion; }
    public void setIdFuncion(int idFuncion) { this.idFuncion = idFuncion; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdAsiento() { return idAsiento; }
    public void setIdAsiento(int idAsiento) { this.idAsiento = idAsiento; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    @Override
    public String toString() {
        return "Boleto{" +
                "idBoleto=" + idBoleto +
                ", precioFinal=" + precioFinal +
                ", fechaCompra=" + fechaCompra +
                ", codigoQR='" + codigoQR + '\'' +
                ", idFuncion=" + idFuncion +
                ", idCliente=" + idCliente +
                ", idAsiento=" + idAsiento +
                ", idVenta=" + idVenta +
                '}';
    }
}
