package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.AsientoDTO;

import java.util.List;

public interface AsientoDAO {

    // Cambia el nombre de este método para reflejar que obtiene asientos POR FUNCIÓN, no solo por sala
    List<AsientoDTO> findAsientosByFuncion(int idFuncion); // Nuevo método o renombrado del anterior

    // Este método ya no es necesario si findAsientosByFuncion devuelve el estado real.
    // O si lo conservas, se usaría para el estado inicial.
    // List<Integer> findBookedSeatIdsByFuncion(int idFuncion);

    // Este método ya no sería estrictamente necesario si findAsientosByFuncion es exhaustivo,
    // pero lo puedes mantener si lo usas para otros fines o para comparar con el estado físico.
    // Si no lo necesitas, puedes borrarlo y adaptarlo si es que se usa en otra parte.
    List<Integer> findBookedSeatIdsByFuncion(int idFuncion);

    // Nuevo método para actualizar el estado de un asiento para una función específica
    boolean updateEstadoAsientoEnFuncion(int idFuncion, int idAsiento, String nuevoEstado);
}