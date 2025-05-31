package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.AsientoDAO;
import com.example.cinelinces.database.MySQLConnection; // Asumo que esta es tu clase de conexión
import com.example.cinelinces.model.DTO.AsientoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsientoDAOImpl implements AsientoDAO {

    // private final MySQLConnection conexionBD = new MySQLConnection();
    // Considera si MySQLConnection debe ser un singleton o si getConnection() es estático.
    // Si MySQLConnection.getConnection() es estático, no necesitas la instancia de campo.
    // Si no es estático, crear una instancia aquí está bien, pero asegúrate que maneje bien las conexiones.

    @Override
    public List<AsientoDTO> findAsientosBySala(int idSala) {
        List<AsientoDTO> lista = new ArrayList<>();
        // CORREGIDO: Se añade la columna "Estado" a la consulta SQL
        String sql = "SELECT IdAsiento, Fila, Numero, TipoAsiento, Estado " +
                "FROM Asiento " +
                "WHERE IdSala = ? " +
                "ORDER BY Fila, Numero";
        try (Connection conn = MySQLConnection.getConnection(); // Usar try-with-resources para la conexión
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSala);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // CORREGIDO: Se utiliza el constructor de AsientoDTO que incluye el estado
                    // y se pasa el valor de la columna "Estado" del ResultSet.
                    lista.add(new AsientoDTO(
                            rs.getInt("IdAsiento"),
                            rs.getString("Fila"),
                            rs.getInt("Numero"),
                            rs.getString("TipoAsiento"),
                            rs.getString("Estado") // <-- VALOR DE ESTADO AÑADIDO
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Considera un mejor manejo de excepciones
        }
        return lista;
    }

    @Override
    public List<Integer> findBookedSeatIdsByFuncion(int idFuncion) {
        List<Integer> ids = new ArrayList<>();
        // Esta consulta parece correcta para obtener los IDs de asientos ya vendidos para una función.
        // No necesita cambios en el contexto de añadir el "estado" al AsientoDTO.
        String sql = "SELECT DISTINCT IdAsiento " +
                "FROM Boleto " +
                "WHERE IdFuncion = ? " +
                "AND IdAsiento IS NOT NULL"; // Es bueno asegurarse que IdAsiento no sea nulo
        try (Connection conn = MySQLConnection.getConnection(); // Usar try-with-resources
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFuncion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("IdAsiento"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Considera un mejor manejo de excepciones
        }
        return ids;
    }
}