package com.example.cinelinces.DAO;

import com.example.cinelinces.model.Funcion;
import com.example.cinelinces.model.DTO.FuncionDetallada; // Importa el DTO

import java.util.List;

public interface FuncionDAO extends GenericDao<Funcion, Integer> {

    /**
     * Encuentra todas las funciones detalladas (incluyendo información de película, sala y cine)
     * para un cine específico.
     *
     * @param idCine el ID del cine.
     * @return una lista de objetos FuncionDetallada.
     */
    List<FuncionDetallada> findFuncionesDetalladasByCineId(int idCine);
}