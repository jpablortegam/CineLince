package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.Funcion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FuncionDAO extends GenericDao<Funcion, Integer> {

    /**
     * Encuentra todas las funciones detalladas (incluyendo información de película, sala y cine)
     * para un cine específico.
     */
    List<FuncionDetallada> findFuncionesDetalladasByCineId(int idCine);

    /**
     * Obtiene la lista de horarios (FechaHora) para una película en un cine y fecha dados.
     */
    List<LocalDateTime> findHorariosByCinePeliculaFecha(int idCine, int idPelicula, LocalDate fecha);
}