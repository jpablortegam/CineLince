package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.PromocionDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones sobre promociones.
 */
public interface PromocionDAO {

    /**
     * Devuelve todas las promociones cuyo rango de validez incluya la fecha dada.
     */
    List<PromocionDTO> findActiveByDate(LocalDate fecha);

    /**
     * Devuelve todas las promociones que actualmente están marcadas como "Activa"
     * (sin importar la fecha de inicio o fin). Útil para buscar un código exacto.
     */
    List<PromocionDTO> findAllActivePromos();

    /**
     * (Opcional) Devuelve una promoción cuyo código coincida exactamente con el pasado como parámetro,
     * **solo** si está activa. Usar Optional para indicar que puede no existir.
     *
     * Ejemplo de uso:
     *   Optional<PromocionDTO> op = promocionDAO.findByCodigo("COMBOESTRENO10");
     */
    Optional<PromocionDTO> findByCodigo(String codigo);
}
