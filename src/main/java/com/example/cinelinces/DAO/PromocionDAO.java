package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.PromocionDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface PromocionDAO {

    List<PromocionDTO> findActiveByDate(LocalDate fecha);


    List<PromocionDTO> findAllActivePromos();


    Optional<PromocionDTO> findByCodigo(String codigo);

    Optional<PromocionDTO> findActiveByCodigoAndDate(String codigo, LocalDate fecha);
}
