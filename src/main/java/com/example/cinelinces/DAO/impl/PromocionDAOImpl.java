// PromocionDAOImpl.java
package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.PromocionDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromocionDAOImpl implements PromocionDAO {
    @Override
    public List<PromocionDTO> findActiveByDate(LocalDate fecha) {
        String sql = "SELECT IdPromocion, Nombre, Descuento, Codigo " +
                "FROM Promocion " +
                "WHERE FechaInicio <= ? AND FechaFin >= ? " +
                "  AND Activa = 1";
        List<PromocionDTO> promos = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Date sqlDate = Date.valueOf(fecha);
            ps.setDate(1, sqlDate);
            ps.setDate(2, sqlDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    promos.add(new PromocionDTO(
                            rs.getInt("IdPromocion"),
                            rs.getString("Nombre"),
                            rs.getBigDecimal("Descuento"),
                            rs.getString("Codigo")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promos;
    }
}
