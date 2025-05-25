package com.example.cinelinces.DAO;

import com.example.cinelinces.model.Cine;

public interface CineDAO extends GenericDao<Cine, Integer> {
    // No se necesitan métodos adicionales más allá de los CRUD por ahora, findAll() es el clave.
}