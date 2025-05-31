// PromocionDAO.java
package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.PromocionDTO;

import java.time.LocalDate;
import java.util.List;

public interface PromocionDAO {
    /**
     * Devuelve todas las promociones cuyo rango de validez incluya la fecha dada.
     */
    List<PromocionDTO> findActiveByDate(LocalDate fecha);
}
