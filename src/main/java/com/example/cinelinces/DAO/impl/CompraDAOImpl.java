// CompraDAOImpl.java
package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.CompraProductoDetalladaDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompraDAOImpl implements CompraDAO {

    public CompraDAOImpl() {
        // Constructor vacío
    }

    @Override
    public List<CompraDetalladaDTO> findComprasByClienteId(int idCliente) {
        // Para retrocompatibilidad devolvemos los boletos
        return findComprasDeBoletosByClienteId(idCliente);
    }

    @Override
    public List<CompraDetalladaDTO> findComprasDeBoletosByClienteId(int idCliente) {
        String sql =
                "SELECT b.IdBoleto, b.PrecioFinal, b.FechaCompra, b.CodigoQR, b.IdAsiento, " +
                        "       v.IdVenta, v.Total AS TotalVenta, v.MetodoPago, v.Estado AS EstadoVenta, v.Facturado, v.IdPromocion, " +
                        "       f.IdFuncion, f.FechaHora AS FechaHoraFuncion, f.Precio AS PrecioBoletoFuncion, f.Estado AS EstadoFuncion, " +
                        "       p.IdPelicula, p.Titulo AS TituloPelicula, p.Duracion AS DuracionMinutos, " +
                        "       p.Clasificacion AS ClasificacionPelicula, p.Sinopsis AS SinopsisPelicula, " +
                        "       p.Fotografia AS FotografiaPelicula, p.FechaEstreno AS FechaEstrenoPelicula, " +
                        "       tp.Nombre AS NombreTipoPelicula, " +
                        "       s.IdSala, s.Numero AS NumeroSala, s.TipoSala, " +
                        "       c.Nombre AS NombreCine " +
                        "FROM Boleto b " +
                        "JOIN Venta v       ON b.IdVenta    = v.IdVenta " +
                        "JOIN Funcion f     ON b.IdFuncion  = f.IdFuncion " +
                        "JOIN Pelicula p    ON f.IdPelicula = p.IdPelicula " +
                        "JOIN Sala s        ON f.IdSala     = s.IdSala " +
                        "JOIN Cine c        ON s.IdCine     = c.IdCine " +
                        "LEFT JOIN TipoPelicula tp ON p.IdTipoPelicula = tp.IdTipoPelicula " +
                        "WHERE b.IdCliente = ?";

        List<CompraDetalladaDTO> compras = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

    @Override
    public List<CompraProductoDetalladaDTO> findComprasDeProductosByClienteId(int idCliente) {
        String sql =
                "SELECT dv.IdDetalleVenta, dv.IdVenta, dv.IdProducto, p.Nombre AS NombreProducto, " +
                        "       p.Descripcion AS DescripcionProducto, dv.Cantidad, dv.PrecioUnitario, dv.Subtotal " +
                        "FROM DetalleVenta dv " +
                        "JOIN Producto p ON dv.IdProducto = p.IdProducto " +
                        "WHERE dv.IdVenta IN (SELECT DISTINCT IdVenta FROM Boleto WHERE IdCliente = ?)";

        List<CompraProductoDetalladaDTO> lista = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CompraProductoDetalladaDTO dto = new CompraProductoDetalladaDTO(
                            rs.getInt("IdDetalleVenta"),
                            rs.getInt("IdVenta"),
                            rs.getInt("IdProducto"),
                            rs.getString("NombreProducto"),
                            rs.getString("DescripcionProducto"),
                            rs.getInt("Cantidad"),
                            rs.getBigDecimal("PrecioUnitario"),
                            rs.getBigDecimal("Subtotal")
                    );
                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
