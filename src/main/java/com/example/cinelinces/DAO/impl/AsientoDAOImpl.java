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

    // Renombrado y modificado para obtener asientos con su estado para una función específica
    @Override
    public List<AsientoDTO> findAsientosByFuncion(int idFuncion) {
        List<AsientoDTO> lista = new ArrayList<>();
        // Unimos con funcionasiento para obtener el estado del asiento para esta función
        String sql = "SELECT a.IdAsiento, a.Fila, a.Numero, a.TipoAsiento, fa.EstadoAsiento " + // Obtenemos EstadoAsiento de funcionasiento
                "FROM Asiento a " +
                "JOIN FuncionAsiento fa ON a.IdAsiento = fa.IdAsiento " +
                "WHERE fa.IdFuncion = ? " + // Filtramos por la función
                "ORDER BY a.Fila, a.Numero";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFuncion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new AsientoDTO(
                            rs.getInt("IdAsiento"),
                            rs.getString("Fila"),
                            rs.getInt("Numero"),
                            rs.getString("TipoAsiento"),
                            rs.getString("EstadoAsiento") // Usamos EstadoAsiento de funcionasiento
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
        // Ahora consultamos directamente la tabla FuncionAsiento para los asientos ocupados
        String sql = "SELECT IdAsiento " +
                "FROM FuncionAsiento " +
                "WHERE IdFuncion = ? AND EstadoAsiento = 'Ocupado'";
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

    // Nuevo método para actualizar el estado de un asiento para una función específica
    @Override
    public boolean updateEstadoAsientoEnFuncion(int idFuncion, int idAsiento, String nuevoEstado) {
        String sql = "UPDATE FuncionAsiento SET EstadoAsiento = ? WHERE IdFuncion = ? AND IdAsiento = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idFuncion);
            ps.setInt(3, idAsiento);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}