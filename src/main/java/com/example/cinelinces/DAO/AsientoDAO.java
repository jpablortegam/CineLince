package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.AsientoDTO;

import java.util.List;

public interface AsientoDAO {

    List<AsientoDTO> findAsientosBySala(int idSala);

    List<Integer> findBookedSeatIdsByFuncion(int idFuncion);
}