package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.FuncionDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.ActorPeliculaDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.Funcion;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FuncionDAOImpl implements FuncionDAO {

    MySQLConnection ConexionBD = new MySQLConnection();

    // ... (otros métodos existentes como mapResultSetToFuncion, findById, findAll, save, update, delete, deleteById, findActoresForPelicula) ...
    // Asegúrate que el método findActoresForPelicula que te proporcioné anteriormente esté aquí.
    private Funcion mapResultSetToFuncion(ResultSet rs) throws SQLException {
        Funcion funcion = new Funcion();
        funcion.setIdFuncion(rs.getInt("IdFuncion"));
        Timestamp fechaHoraSQL = rs.getTimestamp("FechaHora");
        if (fechaHoraSQL != null) {
            funcion.setFechaHora(fechaHoraSQL.toLocalDateTime());
        }
        funcion.setPrecio(rs.getBigDecimal("Precio"));
        funcion.setEstado(rs.getString("Estado"));
        funcion.setIdPelicula(rs.getInt("IdPelicula"));
        funcion.setIdSala(rs.getInt("IdSala"));
        return funcion;
    }

    @Override
    public Funcion findById(Integer id) {
        String sql = "SELECT * FROM Funcion WHERE IdFuncion = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFuncion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en FuncionDAOImpl.findById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Funcion> findAll() {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM Funcion";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                funciones.add(mapResultSetToFuncion(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en FuncionDAOImpl.findAll: " + e.getMessage());
            e.printStackTrace();
        }
        return funciones;
    }

    @Override
    public void save(Funcion entity) {
        String sql = "INSERT INTO Funcion (FechaHora, Precio, Estado, IdPelicula, IdSala) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setTimestamp(1, entity.getFechaHora() != null ? Timestamp.valueOf(entity.getFechaHora()) : null);
            pstmt.setBigDecimal(2, entity.getPrecio());
            pstmt.setString(3, entity.getEstado());
            pstmt.setInt(4, entity.getIdPelicula());
            pstmt.setInt(5, entity.getIdSala());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setIdFuncion(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en FuncionDAOImpl.save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Funcion update(Funcion entity) {
        String sql = "UPDATE Funcion SET FechaHora = ?, Precio = ?, Estado = ?, IdPelicula = ?, IdSala = ? WHERE IdFuncion = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, entity.getFechaHora() != null ? Timestamp.valueOf(entity.getFechaHora()) : null);
            pstmt.setBigDecimal(2, entity.getPrecio());
            pstmt.setString(3, entity.getEstado());
            pstmt.setInt(4, entity.getIdPelicula());
            pstmt.setInt(5, entity.getIdSala());
            pstmt.setInt(6, entity.getIdFuncion());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
        } catch (SQLException e) {
            System.err.println("Error en FuncionDAOImpl.update: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Funcion entity) {
        if (entity != null) {
            deleteById(entity.getIdFuncion());
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM Funcion WHERE IdFuncion = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en FuncionDAOImpl.deleteById: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<ActorPeliculaDTO> findActoresForPelicula(int idPelicula, Connection conn) throws SQLException {
        List<ActorPeliculaDTO> actores = new ArrayList<>();
        String sql = "SELECT A.Nombre AS NombreActor, PA.Personaje " +
                "FROM Actor A " +
                "JOIN PeliculaActor PA ON A.IdActor = PA.IdActor " +
                "WHERE PA.IdPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPelicula);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    actores.add(new ActorPeliculaDTO(
                            rs.getString("NombreActor"),
                            rs.getString("Personaje")
                    ));
                }
            }
        }
        return actores;
    }

    @Override
    public List<FuncionDetallada> findFuncionesDetalladasByCineId(int idCine) {
        List<FuncionDetallada> funcionesDetalladas = new ArrayList<>();
        String sql = "SELECT " +
                "C.Nombre AS NombreCine, " +
                "S.Numero AS NumeroSala, S.TipoSala, S.IdSala, " +
                "P.Titulo AS TituloPelicula, P.Duracion AS DuracionMinutos, P.Clasificacion AS ClasificacionPelicula, " +
                "P.IdPelicula, P.Sinopsis AS SinopsisPelicula, P.Fotografia AS FotografiaPelicula, P.FechaEstreno AS FechaEstrenoPelicula, " +
                "P.Idioma AS IdiomaPelicula, P.Subtitulada AS SubtituladaPelicula, " + // Nuevos campos de Pelicula
                "TP.Nombre AS NombreTipoPelicula, " +
                "Est.Nombre AS NombreEstudio, " + // Campo de Estudio
                "Dir.Nombre AS NombreDirector, " + // Campo de Director
                "F.FechaHora AS FechaHoraFuncion, F.Precio AS PrecioBoleto, F.Estado AS EstadoFuncion, F.IdFuncion " +
                "FROM Funcion F " +
                "JOIN Sala S ON F.IdSala = S.IdSala " +
                "JOIN Pelicula P ON F.IdPelicula = P.IdPelicula " +
                "JOIN Cine C ON S.IdCine = C.IdCine " +
                "LEFT JOIN TipoPelicula TP ON P.IdTipoPelicula = TP.IdTipoPelicula " +
                "LEFT JOIN Estudio Est ON P.IdEstudio = Est.IdEstudio " + // LEFT JOIN para Estudio
                "LEFT JOIN Director Dir ON P.IdDirector = Dir.IdDirector ";  // LEFT JOIN para Director
        // No se puede agregar WHERE y ORDER BY aquí directamente si la consulta continúa

        // Continuación de la consulta original
        sql += "WHERE C.IdCine = ? " +
                "ORDER BY F.FechaHora ASC, S.Numero ASC";


        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCine);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                FuncionDetallada dto = new FuncionDetallada();
                // Poblar campos existentes
                dto.setNombreCine(rs.getString("NombreCine"));
                dto.setNumeroSala(rs.getInt("NumeroSala"));
                dto.setTipoSala(rs.getString("TipoSala"));
                dto.setIdSala(rs.getInt("IdSala"));
                dto.setTituloPelicula(rs.getString("TituloPelicula"));
                dto.setDuracionMinutos(rs.getInt("DuracionMinutos"));
                dto.setClasificacionPelicula(rs.getString("ClasificacionPelicula")); // Ya se estaba poblando
                dto.setIdPelicula(rs.getInt("IdPelicula"));
                dto.setSinopsisPelicula(rs.getString("SinopsisPelicula"));
                dto.setFotografiaPelicula(rs.getString("FotografiaPelicula"));
                Date fechaEstrenoSQL = rs.getDate("FechaEstrenoPelicula");
                if (fechaEstrenoSQL != null) {
                    dto.setFechaEstrenoPelicula(fechaEstrenoSQL.toLocalDate());
                }
                dto.setNombreTipoPelicula(rs.getString("NombreTipoPelicula"));
                Timestamp fechaHoraSQL = rs.getTimestamp("FechaHoraFuncion");
                if (fechaHoraSQL != null) {
                    dto.setFechaHoraFuncion(fechaHoraSQL.toLocalDateTime());
                }
                dto.setPrecioBoleto(rs.getBigDecimal("PrecioBoleto"));
                dto.setEstadoFuncion(rs.getString("EstadoFuncion"));
                dto.setIdFuncion(rs.getInt("IdFuncion"));

                // Poblar nuevos campos
                dto.setNombreEstudio(rs.getString("NombreEstudio"));
                dto.setNombreDirector(rs.getString("NombreDirector"));
                dto.setIdiomaPelicula(rs.getString("IdiomaPelicula"));
                dto.setSubtituladaPelicula(rs.getBoolean("SubtituladaPelicula"));

                // Obtener y asignar la lista de actores
                List<ActorPeliculaDTO> actoresPelicula = findActoresForPelicula(dto.getIdPelicula(), conn);
                dto.setActores(actoresPelicula);

                funcionesDetalladas.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("Error en FuncionDAOImpl.findFuncionesDetalladasByCineId: " + e.getMessage());
            e.printStackTrace();
        }
        return funcionesDetalladas;
    }
}