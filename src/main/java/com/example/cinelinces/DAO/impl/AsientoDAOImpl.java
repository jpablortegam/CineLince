package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.AsientoDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.AsientoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsientoDAOImpl implements AsientoDAO {
    @Override
    public List<AsientoDTO> findAsientosBySala(int idSala) {
        List<AsientoDTO> lista = new ArrayList<>();
        String sql = "SELECT IdAsiento, Fila, Numero, TipoAsiento, Estado " +
                "FROM Asiento " +
                "WHERE IdSala = ? " +
                "ORDER BY Fila, Numero";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSala);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new AsientoDTO(
                            rs.getInt("IdAsiento"),
                            rs.getString("Fila"),
                            rs.getInt("Numero"),
                            rs.getString("TipoAsiento"),
                            rs.getString("Estado")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Integer> findBookedSeatIdsByFuncion(int idFuncion) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT DISTINCT IdAsiento " +
                "FROM Boleto " +
                "WHERE IdFuncion = ? " +
                "AND IdAsiento IS NOT NULL";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFuncion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("IdAsiento"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}