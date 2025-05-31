package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.ProductoDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {
    private final MySQLConnection connUtil = new MySQLConnection();

    @Override
    public List<Producto> findAll() {
        List<Producto> list = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Estado = 'Disponible'";
        try (Connection c = connUtil.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Producto> findAvailableByCategoria(int idCategoria) {
        List<Producto> list = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Estado='Disponible' AND IdCategoria=?";
        try (Connection c = connUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, idCategoria);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Producto findById(int id) {
        String sql = "SELECT * FROM Producto WHERE IdProducto = ?";
        try (Connection c = connUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Producto map(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("IdProducto"),
                rs.getString("Nombre"),
                rs.getString("Descripcion"),
                rs.getBigDecimal("Precio"),
                rs.getInt("Stock"),
                rs.getString("Estado"),
                rs.getInt("IdCategoria")
        );
    }
}