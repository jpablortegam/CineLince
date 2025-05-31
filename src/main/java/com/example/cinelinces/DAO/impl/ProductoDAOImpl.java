// ProductoDAOImpl.java
package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.ProductoDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.ProductoDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {
    @Override
    public List<ProductoDTO> findAllAvailable() {
        String sql = "SELECT IdProducto, Nombre, Descripcion, Precio, Stock " +
                "FROM Producto WHERE Estado = 'Activo'";
        List<ProductoDTO> lista = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new ProductoDTO(
                        rs.getInt("IdProducto"),
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getBigDecimal("Precio"),
                        rs.getInt("Stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
