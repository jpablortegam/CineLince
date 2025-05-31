package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.ClienteDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.Cliente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    MySQLConnection conexionBD = new MySQLConnection();

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("IdCliente"));
        cliente.setNombre(rs.getString("Nombre"));
        cliente.setApellido(rs.getString("Apellido"));
        cliente.setEmail(rs.getString("Email"));
        cliente.setContrasenaHash(rs.getString("ContrasenaHash"));
        cliente.setTelefono(rs.getString("Telefono"));
        Date fechaNacSQL = rs.getDate("FechaNacimiento");
        if (fechaNacSQL != null) {
            cliente.setFechaNacimiento(fechaNacSQL.toLocalDate());
        }
        Timestamp fechaRegSQL = rs.getTimestamp("FechaRegistro");
        if (fechaRegSQL != null) {
            cliente.setFechaRegistro(fechaRegSQL.toLocalDateTime());
        }
        int idMembresia = rs.getInt("IdMembresia");
        if (rs.wasNull()) {
            cliente.setIdMembresia(null);
        } else {
            cliente.setIdMembresia(idMembresia);
        }
        return cliente;
    }

    @Override
    public Cliente findById(Integer id) {
        String sql = "SELECT * FROM Cliente WHERE IdCliente = ?";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAOImpl.findById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Cliente findByEmail(String email) {
        String sql = "SELECT * FROM Cliente WHERE Email = ?";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAOImpl.findByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";
        try (Connection conn = conexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAOImpl.findAll: " + e.getMessage());
            e.printStackTrace();
        }
        return clientes;
    }

    @Override
    public void save(Cliente entity) {
        String sql = "INSERT INTO Cliente (Nombre, Apellido, Email, ContrasenaHash, Telefono, FechaNacimiento, FechaRegistro, IdMembresia) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, entity.getNombre());
            pstmt.setString(2, entity.getApellido());
            pstmt.setString(3, entity.getEmail());
            pstmt.setString(4, entity.getContrasenaHash());
            pstmt.setString(5, entity.getTelefono());
            pstmt.setDate(6, entity.getFechaNacimiento() != null ? Date.valueOf(entity.getFechaNacimiento()) : null);
            pstmt.setTimestamp(7, entity.getFechaRegistro() != null ? Timestamp.valueOf(entity.getFechaRegistro()) : Timestamp.valueOf(LocalDateTime.now()));
            if (entity.getIdMembresia() != null) {
                pstmt.setInt(8, entity.getIdMembresia());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setIdCliente(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAOImpl.save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Cliente update(Cliente entity) {
        String sql = "UPDATE Cliente SET Nombre = ?, Apellido = ?, Email = ?, ContrasenaHash = ?, Telefono = ?, FechaNacimiento = ?, FechaRegistro = ?, IdMembresia = ? WHERE IdCliente = ?";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getNombre());
            pstmt.setString(2, entity.getApellido());
            pstmt.setString(3, entity.getEmail());
            pstmt.setString(4, entity.getContrasenaHash());
            pstmt.setString(5, entity.getTelefono());
            pstmt.setDate(6, entity.getFechaNacimiento() != null ? Date.valueOf(entity.getFechaNacimiento()) : null);
            pstmt.setTimestamp(7, entity.getFechaRegistro() != null ? Timestamp.valueOf(entity.getFechaRegistro()) : null);

            if (entity.getIdMembresia() != null) {
                pstmt.setInt(8, entity.getIdMembresia());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            pstmt.setInt(9, entity.getIdCliente());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAOImpl.update: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Cliente entity) {
        if (entity != null) {
            deleteById(entity.getIdCliente());
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM Cliente WHERE IdCliente = ?";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAOImpl.deleteById: " + e.getMessage());
            e.printStackTrace();
        }
    }
}