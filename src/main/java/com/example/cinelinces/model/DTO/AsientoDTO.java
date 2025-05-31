package com.example.cinelinces.model.DTO;

import java.io.Serializable;

/**
 * Data Transfer Object para representar la información de un asiento.
 */
public class AsientoDTO implements Serializable {
    private int idAsiento;
    private String fila;
    private int numero;
    private String tipoAsiento;
    private String estado; // NUEVO CAMPO

    /** Constructor vacío. */
    public AsientoDTO() {
    }

    /**
     * Constructor completo.
     *
     * @param idAsiento    Identificador único del asiento.
     * @param fila         Letra o código de la fila.
     * @param numero       Número del asiento en la fila.
     * @param tipoAsiento  Tipo de asiento (Normal, VIP, Preferente, etc.).
     * @param estado       Estado actual del asiento (Disponible, Ocupado, Mantenimiento, etc.)
     */
    public AsientoDTO(int idAsiento, String fila, int numero, String tipoAsiento, String estado) { // AÑADIDO estado
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.tipoAsiento = tipoAsiento;
        this.estado = estado; // AÑADIDO estado
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

    // MÉTODO getEstado() CORREGIDO Y SETTER AÑADIDO
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

    //
    //  NOTA: No implementamos aquí getTituloPelicula(), getFechaHoraFuncion(), getNumeroSala() ni getPrecioUnitario().
    //  Esa información permanece en FuncionDetallada, y el PurchaseSummaryViewController la obtendrá desde allí.
    //
}