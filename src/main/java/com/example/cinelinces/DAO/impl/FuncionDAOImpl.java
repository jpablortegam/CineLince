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

    private final MySQLConnection conexionBD = new MySQLConnection();

    private Funcion mapResultSetToFuncion(ResultSet rs) throws SQLException {
        Funcion funcion = new Funcion();
        funcion.setIdFuncion(rs.getInt("IdFuncion"));
        Timestamp ts = rs.getTimestamp("FechaHora");
        if (ts != null) {
            funcion.setFechaHora(ts.toLocalDateTime());
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
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFuncion(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Funcion> findAll() {
        List<Funcion> lista = new ArrayList<>();
        String sql = "SELECT * FROM Funcion";
        try (Connection conn = conexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapResultSetToFuncion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void save(Funcion entity) {
        String sql = "INSERT INTO Funcion (FechaHora, Precio, Estado, IdPelicula, IdSala) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, entity.getFechaHora() != null ? Timestamp.valueOf(entity.getFechaHora()) : null);
            ps.setBigDecimal(2, entity.getPrecio());
            ps.setString(3, entity.getEstado());
            ps.setInt(4, entity.getIdPelicula());
            ps.setInt(5, entity.getIdSala());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        entity.setIdFuncion(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Funcion update(Funcion entity) {
        String sql = "UPDATE Funcion SET FechaHora=?, Precio=?, Estado=?, IdPelicula=?, IdSala=? WHERE IdFuncion=?";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, entity.getFechaHora() != null ? Timestamp.valueOf(entity.getFechaHora()) : null);
            ps.setBigDecimal(2, entity.getPrecio());
            ps.setString(3, entity.getEstado());
            ps.setInt(4, entity.getIdPelicula());
            ps.setInt(5, entity.getIdSala());
            ps.setInt(6, entity.getIdFuncion());
            int affected = ps.executeUpdate();
            if (affected > 0) return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Funcion entity) {
        if (entity != null) deleteById(entity.getIdFuncion());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM Funcion WHERE IdFuncion = ?";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<ActorPeliculaDTO> findActoresForPelicula(int idPelicula, Connection conn) throws SQLException {
        List<ActorPeliculaDTO> list = new ArrayList<>();
        String sql = "SELECT A.Nombre AS NombreActor, PA.Personaje " +
                "FROM Actor A JOIN PeliculaActor PA ON A.IdActor = PA.IdActor " +
                "WHERE PA.IdPelicula = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPelicula);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ActorPeliculaDTO(rs.getString("NombreActor"), rs.getString("Personaje")));
                }
            }
        }
        return list;
    }

    @Override
    public List<FuncionDetallada> findFuncionesDetalladasByCineId(int idCine) {
        List<FuncionDetallada> lista = new ArrayList<>();
        String sql =
                "SELECT C.IdCine AS IdCine, C.Nombre AS NombreCine, " +
                        "S.IdSala, S.Numero AS NumeroSala, S.TipoSala, " +
                        "P.IdPelicula, P.Titulo AS TituloPelicula, P.Duracion AS DuracionMinutos, P.Clasificacion AS ClasificacionPelicula, " +
                        "P.Sinopsis AS SinopsisPelicula, P.Fotografia AS FotografiaPelicula, P.FechaEstreno AS FechaEstrenoPelicula, " +
                        "P.Idioma AS IdiomaPelicula, P.Subtitulada AS SubtituladaPelicula, " +
                        "P.CalificacionPromedio AS CalificacionPromedioPelicula, P.TotalCalificaciones AS TotalCalificacionesPelicula, " +
                        "TP.Nombre AS NombreTipoPelicula, Est.Nombre AS NombreEstudio, Dir.Nombre AS NombreDirector, " +
                        "F.IdFuncion, F.FechaHora AS FechaHoraFuncion, F.Precio AS PrecioBoleto, F.Estado AS EstadoFuncion " +
                        "FROM Funcion F " +
                        "JOIN Sala S ON F.IdSala = S.IdSala " +
                        "JOIN Pelicula P ON F.IdPelicula = P.IdPelicula " +
                        "JOIN Cine C ON S.IdCine = C.IdCine " +
                        "LEFT JOIN TipoPelicula TP ON P.IdTipoPelicula = TP.IdTipoPelicula " +
                        "LEFT JOIN Estudio Est ON P.IdEstudio = Est.IdEstudio " +
                        "LEFT JOIN Director Dir ON P.IdDirector = Dir.IdDirector " +
                        "WHERE C.IdCine = ? " +
                        "ORDER BY F.FechaHora, S.Numero";

        try (Connection conn = conexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FuncionDetallada dto = new FuncionDetallada();
                    dto.setIdCine(rs.getInt("IdCine"));
                    dto.setNombreCine(rs.getString("NombreCine"));
                    dto.setIdSala(rs.getInt("IdSala"));
                    dto.setNumeroSala(rs.getInt("NumeroSala"));
                    dto.setTipoSala(rs.getString("TipoSala"));
                    dto.setIdPelicula(rs.getInt("IdPelicula"));
                    dto.setTituloPelicula(rs.getString("TituloPelicula"));
                    dto.setDuracionMinutos(rs.getInt("DuracionMinutos"));
                    dto.setClasificacionPelicula(rs.getString("ClasificacionPelicula"));
                    dto.setSinopsisPelicula(rs.getString("SinopsisPelicula"));
                    dto.setFotografiaPelicula(rs.getString("FotografiaPelicula"));
                    Date d = rs.getDate("FechaEstrenoPelicula");
                    if (d != null) dto.setFechaEstrenoPelicula(d.toLocalDate());
                    dto.setIdiomaPelicula(rs.getString("IdiomaPelicula"));
                    dto.setSubtituladaPelicula(rs.getBoolean("SubtituladaPelicula"));
                    dto.setCalificacionPromedioPelicula(rs.getDouble("CalificacionPromedioPelicula"));
                    dto.setTotalCalificacionesPelicula(rs.getInt("TotalCalificacionesPelicula"));
                    dto.setNombreTipoPelicula(rs.getString("NombreTipoPelicula"));
                    dto.setNombreEstudio(rs.getString("NombreEstudio"));
                    dto.setNombreDirector(rs.getString("NombreDirector"));
                    Timestamp ts2 = rs.getTimestamp("FechaHoraFuncion");
                    if (ts2 != null) dto.setFechaHoraFuncion(ts2.toLocalDateTime());
                    dto.setPrecioBoleto(rs.getBigDecimal("PrecioBoleto"));
                    dto.setEstadoFuncion(rs.getString("EstadoFuncion"));
                    dto.setIdFuncion(rs.getInt("IdFuncion"));
                    dto.setActores(findActoresForPelicula(dto.getIdPelicula(), conn));
                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<LocalDateTime> findHorariosByCinePeliculaFecha(int idCine, int idPelicula, LocalDate fecha) {
        List<LocalDateTime> horarios = new ArrayList<>();
        String sql = "SELECT F.FechaHora FROM Funcion F " +
                "JOIN Sala S ON F.IdSala = S.IdSala " +
                "WHERE S.IdCine = ? AND F.IdPelicula = ? AND DATE(F.FechaHora)=? " +
                "AND F.Estado='En Venta' ORDER BY F.FechaHora";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCine);
            ps.setInt(2, idPelicula);
            ps.setDate(3, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts3 = rs.getTimestamp("FechaHora");
                    if (ts3 != null) horarios.add(ts3.toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return horarios;
    }

    @Override
    public List<LocalDate> findFechasDisponiblesByCinePelicula(int idCine, int idPelicula) {
        List<LocalDate> fechas = new ArrayList<>();
        String sql =
                "SELECT DISTINCT DATE(F.FechaHora) AS Fecha " +
                        "FROM Funcion F " +
                        "JOIN Sala S ON F.IdSala = S.IdSala " +
                        "WHERE S.IdCine = ? AND F.IdPelicula = ? " +
                        "ORDER BY Fecha";
        try (Connection conn = conexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCine);
            ps.setInt(2, idPelicula);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = rs.getDate("Fecha");
                    if (d != null) fechas.add(d.toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fechas;
    }
}