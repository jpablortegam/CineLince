// CompraDAOImpl.java
package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.DAO.impl.PromocionDAOImpl;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.CompraProductoDetalladaDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.PromocionDTO;
import com.example.cinelinces.utils.SessionManager;
import com.example.cinelinces.utils.SummaryContext;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompraDAOImpl implements CompraDAO {

    private final PromocionDAO promoDAO = new PromocionDAOImpl();

    @Override
    public List<CompraDetalladaDTO> findComprasByClienteId(int idCliente) {
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

    @Override
    public void saveFromSummary(SummaryContext ctx) {
        var seats   = ctx.getSelectedSeats();
        var prods   = ctx.getSelectedProducts();
        var func    = ctx.getSelectedFunction();
        var dateTime= ctx.getSelectedDateTime();
        int clientId = SessionManager.getInstance().getCurrentCliente().getIdCliente();

        // 1) calcular subtotales
        BigDecimal sumTickets = func.getPrecioBoleto()
                .multiply(BigDecimal.valueOf(seats.size()));
        BigDecimal sumProds = prods.stream()
                .map(ProductoSelectionDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2) buscar promoción activa
        List<PromocionDTO> promos = promoDAO.findActiveByDate(dateTime.toLocalDate());
        PromocionDTO promo = promos.isEmpty() ? null : promos.get(0);
        BigDecimal discount = promo == null
                ? BigDecimal.ZERO
                : sumTickets.add(sumProds).multiply(promo.getDescuento());

        BigDecimal totalFinal = sumTickets.add(sumProds).subtract(discount);

        String insertVenta =
                "INSERT INTO Venta (FechaVenta, Total, MetodoPago, Estado, Facturado, IdPromocion, IdCliente) " +
                        "VALUES (?,?,?,?,?,?,?)";

        String insertBoleto =
                "INSERT INTO Boleto (FechaCompra, PrecioFinal, CodigoQR, IdAsiento, IdFuncion, IdVenta, IdCliente) " +
                        "VALUES (?,?,?,?,?,?,?)";

        String insertDetalle =
                "INSERT INTO DetalleVenta (IdVenta, IdProducto, Cantidad, PrecioUnitario, Subtotal) " +
                        "VALUES (?,?,?,?,?)";

        try (Connection conn = MySQLConnection.getConnection()) {
            conn.setAutoCommit(false);

            // a) Venta
            int idVenta;
            try (PreparedStatement ps = conn.prepareStatement(insertVenta, Statement.RETURN_GENERATED_KEYS)) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.setBigDecimal(2, totalFinal);
                ps.setString(3, "Efectivo");
                ps.setString(4, "Completado");
                ps.setBoolean(5, false);
                if (promo != null) ps.setInt(6, promo.getId());
                else          ps.setNull(6, Types.INTEGER);
                ps.setInt(7, clientId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No se obtuvo IdVenta");
                    idVenta = keys.getInt(1);
                }
            }

            // b) Boletos
            try (PreparedStatement ps = conn.prepareStatement(insertBoleto)) {
                for (AsientoDTO a : seats) {
                    ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    // distribuir descuento proporcionalmente si lo deseas; aquí usamos precio neto total/tickets
                    BigDecimal neto = func.getPrecioBoleto()
                            .subtract(discount.divide(BigDecimal.valueOf(seats.size()), 2, BigDecimal.ROUND_HALF_UP));
                    ps.setBigDecimal(2, neto);
                    ps.setString(3, "QR-" + UUID.randomUUID());
                    ps.setInt(4, a.getIdAsiento());
                    ps.setInt(5, func.getIdFuncion());
                    ps.setInt(6, idVenta);
                    ps.setInt(7, clientId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // c) DetalleVenta
            try (PreparedStatement ps = conn.prepareStatement(insertDetalle)) {
                for (ProductoSelectionDTO p : prods) {
                    ps.setInt(1, idVenta);
                    ps.setInt(2, p.getIdProducto());
                    ps.setInt(3, p.getCantidad());
                    ps.setBigDecimal(4, p.getPrecioUnitario());
                    ps.setBigDecimal(5, p.getSubtotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            try { MySQLConnection.getConnection().rollback(); }
            catch (SQLException ignore) {}
        }
    }
}
