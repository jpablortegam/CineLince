package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.Funcion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FuncionDAO extends GenericDao<Funcion, Integer> {
    List<FuncionDetallada> findFuncionesDetalladasByCineId(int idCine);

    // MODIFICADO: Ahora devuelve List<FuncionDetallada>
    List<FuncionDetallada> findFuncionesByCinePeliculaFecha(int idCine, int idPelicula, LocalDate fecha);

    List<LocalDate> findFechasDisponiblesByCinePelicula(int idCine, int idPelicula);
}