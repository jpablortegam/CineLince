package com.example.cinelinces.DAO.impl;


import com.example.cinelinces.DAO.CineDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.Cine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CineDAOImpl implements CineDAO {

    private Cine mapResultSetToCine(ResultSet rs) throws SQLException {
        Cine cine = new Cine();
        cine.setIdCine(rs.getInt("IdCine"));
        cine.setNombre(rs.getString("Nombre"));
        cine.setDireccion(rs.getString("Direccion"));
        cine.setCiudad(rs.getString("Ciudad"));
        cine.setEstado(rs.getString("Estado"));
        cine.setCodigoPostal(rs.getString("CodigoPostal"));
        cine.setTelefono(rs.getString("Telefono"));
        Time aperturaSQL = rs.getTime("HoraApertura");
        if (aperturaSQL != null) {
            cine.setHoraApertura(aperturaSQL.toLocalTime());
        }
        Time cierreSQL = rs.getTime("HoraCierre");
        if (cierreSQL != null) {
            cine.setHoraCierre(cierreSQL.toLocalTime());
        }
        return cine;
    }

    @Override
    public Cine findById(Integer id) {
        String sql = "SELECT * FROM Cine WHERE IdCine = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCine(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en CineDAOImpl.findById: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("Error de conexión en CineDAOImpl.findById (conn es null): " + npe.getMessage());
            npe.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Cine> findAll() {
        List<Cine> cines = new ArrayList<>();
        String sql = "SELECT * FROM Cine ORDER BY Nombre ASC";
        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cines.add(mapResultSetToCine(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en CineDAOImpl.findAll: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("Error de conexión en CineDAOImpl.findAll (conn es null): " + npe.getMessage());
            npe.printStackTrace();
        }
        return cines;
    }

    @Override
    public void save(Cine entity) {
        String sql = "INSERT INTO Cine (Nombre, Direccion, Ciudad, Estado, CodigoPostal, Telefono, HoraApertura, HoraCierre) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entity.getNombre());
            pstmt.setString(2, entity.getDireccion());
            pstmt.setString(3, entity.getCiudad());
            pstmt.setString(4, entity.getEstado());
            pstmt.setString(5, entity.getCodigoPostal());
            pstmt.setString(6, entity.getTelefono());
            pstmt.setTime(7, entity.getHoraApertura() != null ? Time.valueOf(entity.getHoraApertura()) : null);
            pstmt.setTime(8, entity.getHoraCierre() != null ? Time.valueOf(entity.getHoraCierre()) : null);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setIdCine(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en CineDAOImpl.save: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("Error de conexión en CineDAOImpl.save (conn es null): " + npe.getMessage());
            npe.printStackTrace();
        }
    }

    @Override
    public Cine update(Cine entity) {
        String sql = "UPDATE Cine SET Nombre = ?, Direccion = ?, Ciudad = ?, Estado = ?, CodigoPostal = ?, Telefono = ?, HoraApertura = ?, HoraCierre = ? WHERE IdCine = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getNombre());
            pstmt.setString(2, entity.getDireccion());
            pstmt.setString(3, entity.getCiudad());
            pstmt.setString(4, entity.getEstado());
            pstmt.setString(5, entity.getCodigoPostal());
            pstmt.setString(6, entity.getTelefono());
            pstmt.setTime(7, entity.getHoraApertura() != null ? Time.valueOf(entity.getHoraApertura()) : null);
            pstmt.setTime(8, entity.getHoraCierre() != null ? Time.valueOf(entity.getHoraCierre()) : null);
            pstmt.setInt(9, entity.getIdCine());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
        } catch (SQLException e) {
            System.err.println("Error en CineDAOImpl.update: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("Error de conexión en CineDAOImpl.update (conn es null): " + npe.getMessage());
            npe.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Cine entity) {
        if (entity != null) {
            deleteById(entity.getIdCine());
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM Cine WHERE IdCine = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en CineDAOImpl.deleteById: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("Error de conexión en CineDAOImpl.deleteById (conn es null): " + npe.getMessage());
            npe.printStackTrace();
        }
    }
}
