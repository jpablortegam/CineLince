package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.ProductoDTO;

import java.util.List;

public interface ProductoDAO {

    List<ProductoDTO> findAllAvailable();

    void decrementStock(int idProducto, int cantidad);
}
