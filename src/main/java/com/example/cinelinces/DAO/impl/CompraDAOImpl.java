package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.PromocionDAO;
// Asegúrate que PromocionDAOImpl esté en el mismo paquete o importa correctamente
// import com.example.cinelinces.DAO.impl.PromocionDAOImpl;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.CompraProductoDetalladaDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.PromocionDTO;
import com.example.cinelinces.model.Cliente; // Necesario para SessionManager
import com.example.cinelinces.utils.SessionManager;
import com.example.cinelinces.utils.SummaryContext;

import java.math.BigDecimal;
import java.math.RoundingMode; // Necesario para RoundingMode
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Necesario para Optional
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
                        "       v.IdVenta, v.Total AS TotalVenta, v.MetodoPago, v.Estado AS EstadoVenta, v.Facturado, v.IdPromocion, v.Fecha AS FechaDeLaVenta, " + // Seleccionamos v.Fecha
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
                        "WHERE b.IdCliente = ? ORDER BY v.Fecha DESC, b.IdBoleto DESC"; // CORREGIDO AQUÍ: v.Fecha en lugar de v.FechaVenta

        List<CompraDetalladaDTO> compras = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FuncionDetallada fd = new FuncionDetallada();
                    fd.setIdFuncion(rs.getInt("IdFuncion"));
                    Timestamp fechaHoraFuncionTs = rs.getTimestamp("FechaHoraFuncion");
                    if (fechaHoraFuncionTs != null) {
                        fd.setFechaHoraFuncion(fechaHoraFuncionTs.toLocalDateTime());
                    }
                    fd.setPrecioBoleto(rs.getBigDecimal("PrecioBoletoFuncion"));
                    fd.setEstadoFuncion(rs.getString("EstadoFuncion"));
                    fd.setIdPelicula(rs.getInt("IdPelicula"));
                    fd.setTituloPelicula(rs.getString("TituloPelicula"));
                    fd.setDuracionMinutos(rs.getInt("DuracionMinutos"));
                    fd.setClasificacionPelicula(rs.getString("ClasificacionPelicula"));
                    fd.setSinopsisPelicula(rs.getString("SinopsisPelicula"));
                    fd.setFotografiaPelicula(rs.getString("FotografiaPelicula"));
                    Date fechaEstrenoDb = rs.getDate("FechaEstrenoPelicula");
                    if (fechaEstrenoDb != null) {
                        fd.setFechaEstrenoPelicula(fechaEstrenoDb.toLocalDate());
                    }
                    fd.setNombreTipoPelicula(rs.getString("NombreTipoPelicula"));
                    fd.setIdSala(rs.getInt("IdSala"));
                    fd.setNumeroSala(rs.getInt("NumeroSala"));
                    fd.setTipoSala(rs.getString("TipoSala"));
                    fd.setNombreCine(rs.getString("NombreCine"));

                    CompraDetalladaDTO compra = new CompraDetalladaDTO();
                    // Asumiendo que CompraDetalladaDTO.idBoleto e idAsiento son String (como se discutió para la tarjeta)
                    // Si siguen siendo int en tu DTO, quita String.valueOf()
                    compra.setIdBoleto(String.valueOf(rs.getInt("IdBoleto")));
                    compra.setPrecioFinal(rs.getBigDecimal("PrecioFinal"));
                    Timestamp fechaCompraTs = rs.getTimestamp("FechaCompra"); // Fecha de compra del boleto
                    if (fechaCompraTs != null) {
                        compra.setFechaCompra(fechaCompraTs.toLocalDateTime());
                    }
                    // Si quisieras usar la Fecha de la Venta en el DTO (además de la FechaCompra del boleto):
                    // Timestamp fechaDeLaVentaTs = rs.getTimestamp("FechaDeLaVenta");
                    // if (fechaDeLaVentaTs != null && compra.getFechaCompra() == null) { // O alguna otra lógica
                    //     compra.setFechaCompra(fechaDeLaVentaTs.toLocalDateTime()); // O a un nuevo campo en el DTO
                    // }

                    compra.setCodigoQR(rs.getString("CodigoQR"));
                    compra.setIdAsiento(String.valueOf(rs.getInt("IdAsiento")));
                    compra.setIdVenta(rs.getInt("IdVenta"));
                    compra.setTotalVenta(rs.getBigDecimal("TotalVenta"));
                    compra.setMetodoPago(rs.getString("MetodoPago"));
                    compra.setEstadoVenta(rs.getString("EstadoVenta"));
                    compra.setFacturado(rs.getBoolean("Facturado"));
                    compra.setIdPromocion((Integer) rs.getObject("IdPromocion"));
                    compra.setFuncion(fd);
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
        List<AsientoDTO> seats = ctx.getSelectedSeats();
        List<ProductoSelectionDTO> prods = ctx.getSelectedProducts();
        FuncionDetallada func = ctx.getSelectedFunction();

        Cliente currentClient = SessionManager.getInstance().getCurrentCliente();
        if (currentClient == null) {
            System.err.println("Error: Intento de guardar compra sin cliente logueado en CompraDAOImpl.");
            throw new IllegalStateException("No hay un cliente logueado para realizar la compra.");
        }
        int clientId = currentClient.getIdCliente();

        BigDecimal sumTickets = BigDecimal.ZERO;
        if (func != null && func.getPrecioBoleto() != null && seats != null && !seats.isEmpty()) {
            sumTickets = func.getPrecioBoleto().multiply(BigDecimal.valueOf(seats.size()));
        }

        BigDecimal sumProds = BigDecimal.ZERO;
        if (prods != null) {
            sumProds = prods.stream()
                    .map(ProductoSelectionDTO::getSubtotal)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        PromocionDTO promoAplicadaManualmente = null;
        BigDecimal discount = BigDecimal.ZERO;
        String codigoPromoManual = ctx.getCodigoPromocion();

        if (codigoPromoManual != null && !codigoPromoManual.isEmpty()) {
            Optional<PromocionDTO> optPromo = promoDAO.findActiveByCodigoAndDate(codigoPromoManual, LocalDate.now());
            if (optPromo.isPresent()) {
                promoAplicadaManualmente = optPromo.get();
                if (promoAplicadaManualmente.getDescuento() != null) {
                    discount = sumTickets.add(sumProds).multiply(promoAplicadaManualmente.getDescuento());
                }
            }
        }

        BigDecimal totalFinal = sumTickets.add(sumProds).subtract(discount);
        LocalDateTime ventaTimestamp = LocalDateTime.now();

        // CORREGIDO: Nombre de columna FechaVenta a Fecha
        String insertVenta =
                "INSERT INTO Venta (Fecha, Total, MetodoPago, Estado, Facturado, IdPromocion, IdCliente) " +
                        "VALUES (?,?,?,?,?,?,?)";
        String insertBoleto =
                "INSERT INTO Boleto (FechaCompra, PrecioFinal, CodigoQR, IdAsiento, IdFuncion, IdVenta, IdCliente) " +
                        "VALUES (?,?,?,?,?,?,?)";
        String insertDetalle =
                "INSERT INTO DetalleVenta (IdVenta, IdProducto, Cantidad, PrecioUnitario, Subtotal) " +
                        "VALUES (?,?,?,?,?)";

        Connection conn = null;
        try {
            conn = MySQLConnection.getConnection();
            conn.setAutoCommit(false);

            int idVenta;
            try (PreparedStatement psVenta = conn.prepareStatement(insertVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setTimestamp(1, Timestamp.valueOf(ventaTimestamp)); // Corresponde a la columna "Fecha"
                psVenta.setBigDecimal(2, totalFinal);
                psVenta.setString(3, ctx.getMetodoPago());
                psVenta.setString(4, "Completado");
                psVenta.setBoolean(5, false);
                if (promoAplicadaManualmente != null) {
                    psVenta.setInt(6, promoAplicadaManualmente.getId());
                } else {
                    psVenta.setNull(6, Types.INTEGER);
                }
                psVenta.setInt(7, clientId);
                psVenta.executeUpdate();
                try (ResultSet keys = psVenta.getGeneratedKeys()) {
                    if (keys.next()) {
                        idVenta = keys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el IdVenta generado.");
                    }
                }
            }

            if (seats != null && !seats.isEmpty() && func != null && func.getPrecioBoleto() != null) {
                try (PreparedStatement psBoleto = conn.prepareStatement(insertBoleto)) {
                    BigDecimal descuentoPorBoleto = BigDecimal.ZERO;
                    if (discount.compareTo(BigDecimal.ZERO) > 0 && seats.size() > 0) {
                        descuentoPorBoleto = discount.divide(BigDecimal.valueOf(seats.size()), 2, RoundingMode.HALF_UP);
                    }

                    for (AsientoDTO a : seats) {
                        psBoleto.setTimestamp(1, Timestamp.valueOf(ventaTimestamp)); // FechaCompra del boleto
                        BigDecimal precioNetoBoleto = func.getPrecioBoleto().subtract(descuentoPorBoleto);
                        psBoleto.setBigDecimal(2, precioNetoBoleto.max(BigDecimal.ZERO));
                        psBoleto.setString(3, "QR-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
                        // Asumiendo que AsientoDTO tiene getIdAsiento() que devuelve int
                        // y CompraDetalladaDTO.idAsiento sigue siendo int según tu último DTO
                        psBoleto.setInt(4, a.getIdAsiento());
                        psBoleto.setInt(5, func.getIdFuncion());
                        psBoleto.setInt(6, idVenta);
                        psBoleto.setInt(7, clientId);
                        psBoleto.addBatch();
                    }
                    psBoleto.executeBatch();
                }
            }

            if (prods != null && !prods.isEmpty()) {
                try (PreparedStatement psDetalle = conn.prepareStatement(insertDetalle)) {
                    for (ProductoSelectionDTO p : prods) {
                        psDetalle.setInt(1, idVenta);
                        psDetalle.setInt(2, p.getIdProducto());
                        psDetalle.setInt(3, p.getCantidad());
                        psDetalle.setBigDecimal(4, p.getPrecioUnitario());
                        psDetalle.setBigDecimal(5, p.getSubtotal());
                        psDetalle.addBatch();
                    }
                    psDetalle.executeBatch();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Error en la transacción, realizando rollback.");
                    conn.rollback();
                } catch (SQLException exRollback) {
                    exRollback.printStackTrace();
                }
            }
            throw new RuntimeException("Error al guardar la compra.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}