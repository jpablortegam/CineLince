package com.example.cinelinces.DAO;

import com.example.cinelinces.model.Producto;
import java.util.List;

public interface ProductoDAO {
    List<Producto> findAll();
    List<Producto> findAvailableByCategoria(int idCategoria);
    Producto findById(int idProducto);
}