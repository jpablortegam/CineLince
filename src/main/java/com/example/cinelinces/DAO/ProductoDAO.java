// ProductoDAO.java
package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.ProductoDTO;
import java.util.List;

public interface ProductoDAO {
    /** Devuelve todos los productos activos (Estado = 'Activo') */
    List<ProductoDTO> findAllAvailable();

    void decrementStock(int idProducto, int cantidad);
}
