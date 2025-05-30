package com.example.cinelinces.utils;

import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Singleton que almacena de manera global los datos seleccionados
 * (función, fecha/hora y asientos) para mostrarlos en el resumen de compra.
 */
public class SummaryContext {

    // Única instancia del singleton
    private static final SummaryContext INSTANCE = new SummaryContext();

    private FuncionDetallada selectedFunction;
    private LocalDateTime selectedDateTime;
    private List<AsientoDTO> selectedSeats;

    /**
     * Constructor privado para evitar instancias externas.
     */
    private SummaryContext() {
    }

    /**
     * Obtiene la instancia global de SummaryContext.
     * @return instancia única.
     */
    public static SummaryContext getInstance() {
        return INSTANCE;
    }

    /**
     * Obtiene la función seleccionada.
     */
    public FuncionDetallada getSelectedFunction() {
        return selectedFunction;
    }

    /**
     * Establece la función seleccionada.
     */
    public void setSelectedFunction(FuncionDetallada selectedFunction) {
        this.selectedFunction = selectedFunction;
    }

    /**
     * Obtiene la fecha y hora seleccionadas.
     */
    public LocalDateTime getSelectedDateTime() {
        return selectedDateTime;
    }

    /**
     * Establece la fecha y hora seleccionadas.
     */
    public void setSelectedDateTime(LocalDateTime selectedDateTime) {
        this.selectedDateTime = selectedDateTime;
    }

    /**
     * Obtiene la lista de asientos seleccionados.
     */
    public List<AsientoDTO> getSelectedSeats() {
        return selectedSeats;
    }

    /**
     * Establece la lista de asientos seleccionados.
     */
    public void setSelectedSeats(List<AsientoDTO> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }
}
