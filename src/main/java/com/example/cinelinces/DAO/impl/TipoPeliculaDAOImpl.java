package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.TipoPeliculaDAO;
import com.example.cinelinces.database.MySQLConnection; // Asegúrate de que esta clase sea correcta para tu conexión
import com.example.cinelinces.model.TipoPelicula;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TipoPeliculaDAOImpl implements TipoPeliculaDAO {

    private final MySQLConnection ConexionBD;

    public TipoPeliculaDAOImpl() {
        this.ConexionBD = new MySQLConnection();
    }

    private TipoPelicula mapResultSetToTipoPelicula(ResultSet rs) throws SQLException {
        TipoPelicula tipo = new TipoPelicula();
        tipo.setIdTipoPelicula(rs.getInt("IdTipoPelicula"));
        tipo.setNombre(rs.getString("Nombre"));
        tipo.setDescripcion(rs.getString("Descripcion"));
        return tipo;
    }

    @Override
    public TipoPelicula findById(Integer id) {
        String sql = "SELECT IdTipoPelicula, Nombre, Descripcion FROM TipoPelicula WHERE IdTipoPelicula = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTipoPelicula(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TipoPelicula> findAll() {
        List<TipoPelicula> tipos = new ArrayList<>();
        String sql = "SELECT IdTipoPelicula, Nombre, Descripcion FROM TipoPelicula";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tipos.add(mapResultSetToTipoPelicula(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipos;
    }

    @Override
    public void save(TipoPelicula entity) {
        String sql = "INSERT INTO TipoPelicula (Nombre, Descripcion) VALUES (?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entity.getNombre());
            pstmt.setString(2, entity.getDescripcion());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setIdTipoPelicula(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TipoPelicula update(TipoPelicula entity) {
        String sql = "UPDATE TipoPelicula SET Nombre = ?, Descripcion = ? WHERE IdTipoPelicula = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getNombre());
            pstmt.setString(2, entity.getDescripcion());
            pstmt.setInt(3, entity.getIdTipoPelicula());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(TipoPelicula entity) {
        if (entity != null) {
            deleteById(entity.getIdTipoPelicula());
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM TipoPelicula WHERE IdTipoPelicula = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}