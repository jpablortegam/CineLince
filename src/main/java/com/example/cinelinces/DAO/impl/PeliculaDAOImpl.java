package com.example.cinelinces.DAO.impl;


import com.example.cinelinces.DAO.GenericDao;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.Pelicula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDAOImpl implements GenericDao<Pelicula, Integer> {

    MySQLConnection ConexionBD = new MySQLConnection();

    private Pelicula mapResultSetToPelicula(ResultSet rs) throws SQLException {
        Pelicula pelicula = new Pelicula();
        pelicula.setIdPelicula(rs.getInt("IdPelicula"));
        pelicula.setTitulo(rs.getString("Titulo"));
        pelicula.setDuracion(rs.getInt("Duracion"));
        pelicula.setSinopsis(rs.getString("Sinopsis"));
        // java.sql.Date a java.time.LocalDate
        Date fechaEstrenoSQL = rs.getDate("FechaEstreno");
        if (fechaEstrenoSQL != null) {
            pelicula.setFechaEstreno(fechaEstrenoSQL.toLocalDate());
        }
        pelicula.setClasificacion(rs.getString("Clasificacion"));
        pelicula.setIdioma(rs.getString("Idioma"));
        pelicula.setSubtitulada(rs.getBoolean("Subtitulada"));
        pelicula.setFotografia(rs.getString("Fotografia"));
        pelicula.setFormato(rs.getString("Formato"));
        pelicula.setEstado(rs.getString("Estado"));

        // Para campos FK que pueden ser NULL, usamos getObject y luego verificamos antes de getInt
        pelicula.setIdEstudio(rs.getObject("IdEstudio") != null ? rs.getInt("IdEstudio") : null);
        pelicula.setIdDirector(rs.getObject("IdDirector") != null ? rs.getInt("IdDirector") : null);
        pelicula.setIdTipoPelicula(rs.getObject("IdTipoPelicula") != null ? rs.getInt("IdTipoPelicula") : null);
        return pelicula;
    }

    @Override
    public Pelicula findById(Integer id) {
        String sql = "SELECT * FROM Pelicula WHERE IdPelicula = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPelicula(rs);
                }
            }
        } catch (SQLException e) {
            // Manejo de excepciones (log, relanzar, etc.)
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Pelicula> findAll() {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM Pelicula";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                peliculas.add(mapResultSetToPelicula(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return peliculas;
    }

    @Override
    public void save(Pelicula entity) {
        String sql = "INSERT INTO Pelicula (Titulo, Duracion, Sinopsis, FechaEstreno, Clasificacion, Idioma, Subtitulada, Fotografia, Formato, Estado, IdEstudio, IdDirector, IdTipoPelicula) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, entity.getTitulo());
            pstmt.setInt(2, entity.getDuracion());
            pstmt.setString(3, entity.getSinopsis());
            pstmt.setDate(4, entity.getFechaEstreno() != null ? Date.valueOf(entity.getFechaEstreno()) : null);
            pstmt.setString(5, entity.getClasificacion());
            pstmt.setString(6, entity.getIdioma());
            pstmt.setBoolean(7, entity.isSubtitulada());
            pstmt.setString(8, entity.getFotografia());
            pstmt.setString(9, entity.getFormato());
            pstmt.setString(10, entity.getEstado());

            // Manejo de FKs nullable
            if (entity.getIdEstudio() != null) pstmt.setInt(11, entity.getIdEstudio());
            else pstmt.setNull(11, Types.INTEGER);

            if (entity.getIdDirector() != null) pstmt.setInt(12, entity.getIdDirector());
            else pstmt.setNull(12, Types.INTEGER);

            if (entity.getIdTipoPelicula() != null) pstmt.setInt(13, entity.getIdTipoPelicula());
            else pstmt.setNull(13, Types.INTEGER);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setIdPelicula(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pelicula update(Pelicula entity) {
        String sql = "UPDATE Pelicula SET Titulo = ?, Duracion = ?, Sinopsis = ?, FechaEstreno = ?, Clasificacion = ?, Idioma = ?, Subtitulada = ?, Fotografia = ?, Formato = ?, Estado = ?, IdEstudio = ?, IdDirector = ?, IdTipoPelicula = ? WHERE IdPelicula = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getTitulo());
            pstmt.setInt(2, entity.getDuracion());
            pstmt.setString(3, entity.getSinopsis());
            pstmt.setDate(4, entity.getFechaEstreno() != null ? Date.valueOf(entity.getFechaEstreno()) : null);
            pstmt.setString(5, entity.getClasificacion());
            pstmt.setString(6, entity.getIdioma());
            pstmt.setBoolean(7, entity.isSubtitulada());
            pstmt.setString(8, entity.getFotografia());
            pstmt.setString(9, entity.getFormato());
            pstmt.setString(10, entity.getEstado());

            if (entity.getIdEstudio() != null) pstmt.setInt(11, entity.getIdEstudio());
            else pstmt.setNull(11, Types.INTEGER);

            if (entity.getIdDirector() != null) pstmt.setInt(12, entity.getIdDirector());
            else pstmt.setNull(12, Types.INTEGER);

            if (entity.getIdTipoPelicula() != null) pstmt.setInt(13, entity.getIdTipoPelicula());
            else pstmt.setNull(13, Types.INTEGER);

            pstmt.setInt(14, entity.getIdPelicula());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return entity; // Retorna la entidad actualizada (o podrías re-obtenerla de la BD)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // O manejar el error de forma diferente
    }

    @Override
    public void delete(Pelicula entity) {
        if (entity != null) {
            deleteById(entity.getIdPelicula());
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM Pelicula WHERE IdPelicula = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}