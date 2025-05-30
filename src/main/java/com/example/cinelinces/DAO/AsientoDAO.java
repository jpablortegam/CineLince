// com/example/cinelinces/DAO/AsientoDAO.java
package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.AsientoDTO;
import java.util.List;

/**
 * DAO para gestionar asientos de sala y consultas de asientos reservados.
 */
public interface AsientoDAO {

    /**
     * Devuelve la lista completa de asientos de una sala dada.
     * @param idSala el ID de la sala.
     * @return lista de AsientoDTO.
     */
    List<AsientoDTO> findAsientosBySala(int idSala);

    /**
     * Devuelve los IDs de asientos que ya están reservados para una función.
     * @param idFuncion el ID de la función.
     * @return lista de IDs de asiento reservados.
     */
    List<Integer> findBookedSeatIdsByFuncion(int idFuncion);
}