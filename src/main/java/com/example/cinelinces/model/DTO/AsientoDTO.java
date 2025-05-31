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
     */
    public AsientoDTO(int idAsiento, String fila, int numero, String tipoAsiento) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.tipoAsiento = tipoAsiento;
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

    @Override
    public String toString() {
        // Ejemplo de representación: "C10 (VIP)" o "A5 (Normal)"
        return String.format("%s%d (%s)", fila, numero, tipoAsiento);
    }

    //
    //  NOTA: No implementamos aquí getTituloPelicula(), getFechaHoraFuncion(), getNumeroSala() ni getPrecioUnitario().
    //  Esa información permanece en FuncionDetallada, y el PurchaseSummaryViewController la obtendrá desde allí.
    //
}
