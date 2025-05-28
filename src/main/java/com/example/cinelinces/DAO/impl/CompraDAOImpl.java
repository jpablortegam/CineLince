package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.database.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDAOImpl implements CompraDAO {

    public CompraDAOImpl() {
        // Ya no inyectamos Connection
    }

    @Override
    public List<CompraDetalladaDTO> findComprasByClienteId(int idCliente) {
        String sql = """
          SELECT
            b.IdBoleto, b.PrecioFinal, b.FechaCompra, b.CodigoQR, b.IdAsiento,
            v.IdVenta, v.Total AS TotalVenta, v.MetodoPago, v.Estado AS EstadoVenta, v.Facturado, v.IdPromocion,
            f.IdFuncion, f.FechaHora AS FechaHoraFuncion, f.Precio AS PrecioBoletoFuncion, f.Estado AS EstadoFuncion,
            p.IdPelicula, p.Titulo AS TituloPelicula, p.Duracion AS DuracionMinutos,
            p.Clasificacion AS ClasificacionPelicula, p.Sinopsis AS SinopsisPelicula,
            p.Fotografia AS FotografiaPelicula, p.FechaEstreno AS FechaEstrenoPelicula,
            tp.Nombre AS NombreTipoPelicula,
            s.IdSala, s.Numero AS NumeroSala, s.TipoSala,
            c.Nombre AS NombreCine
          FROM Boleto b
          JOIN Venta v       ON b.IdVenta    = v.IdVenta
          JOIN Funcion f     ON b.IdFuncion  = f.IdFuncion
          JOIN Pelicula p    ON f.IdPelicula = p.IdPelicula
          JOIN TipoPelicula tp ON p.IdTipoPelicula = tp.IdTipoPelicula
          JOIN Sala s        ON f.IdSala     = s.IdSala
          JOIN Cine c        ON s.IdCine     = c.IdCine
          WHERE b.IdCliente = ?
        """;

        List<CompraDetalladaDTO> compras = new ArrayList<>();
        try (
                Connection conn = MySQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FuncionDetallada fd = new FuncionDetallada();
                    fd.setIdFuncion(rs.getInt("IdFuncion"));
                    fd.setFechaHoraFuncion(rs.getTimestamp("FechaHoraFuncion").toLocalDateTime());
                    fd.setPrecioBoleto(rs.getBigDecimal("PrecioBoletoFuncion"));
                    fd.setEstadoFuncion(rs.getString("EstadoFuncion"));
                    fd.setIdPelicula(rs.getInt("IdPelicula"));
                    fd.setTituloPelicula(rs.getString("TituloPelicula"));
                    fd.setDuracionMinutos(rs.getInt("DuracionMinutos"));
                    fd.setClasificacionPelicula(rs.getString("ClasificacionPelicula"));
                    fd.setSinopsisPelicula(rs.getString("SinopsisPelicula"));
                    fd.setFotografiaPelicula(rs.getString("FotografiaPelicula"));
                    fd.setFechaEstrenoPelicula(rs.getDate("FechaEstrenoPelicula").toLocalDate());
                    fd.setNombreTipoPelicula(rs.getString("NombreTipoPelicula"));
                    fd.setIdSala(rs.getInt("IdSala"));
                    fd.setNumeroSala(rs.getInt("NumeroSala"));
                    fd.setTipoSala(rs.getString("TipoSala"));
                    fd.setNombreCine(rs.getString("NombreCine"));

                    CompraDetalladaDTO compra = new CompraDetalladaDTO(
                            rs.getInt("IdBoleto"),
                            rs.getBigDecimal("PrecioFinal"),
                            rs.getTimestamp("FechaCompra").toLocalDateTime(),
                            rs.getString("CodigoQR"),
                            rs.getInt("IdAsiento"),
                            rs.getInt("IdVenta"),
                            rs.getBigDecimal("TotalVenta"),
                            rs.getString("MetodoPago"),
                            rs.getString("EstadoVenta"),
                            rs.getBoolean("Facturado"),
                            (Integer) rs.getObject("IdPromocion"),
                            fd
                    );
                    compras.add(compra);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compras;
    }
}
