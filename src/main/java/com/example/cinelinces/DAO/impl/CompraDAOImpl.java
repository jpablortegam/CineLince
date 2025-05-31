package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.CompraProductoDetalladaDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.PromocionDTO;
import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.utils.SessionManager;
import com.example.cinelinces.utils.SummaryContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class CompraDAOImpl implements CompraDAO {

    private final PromocionDAO promoDAO = new PromocionDAOImpl();

    @Override
    public List<CompraDetalladaDTO> findComprasByClienteId(int idCliente) {
        // 1) Obtener todos los boletos (asientos) y datos de la venta en DTOs provisionales
        List<CompraDetalladaDTO> comprasBoletos = findComprasDeBoletosByClienteId(idCliente);

        // 2) Obtener todos los productos (dulcería) asociados a esas ventas
        List<CompraProductoDetalladaDTO> detalleProductos = findComprasDeProductosByClienteId(idCliente);

        // 3) Agrupar los objetos CompraProductoDetalladaDTO por idVenta
        Map<Integer, List<CompraProductoDetalladaDTO>> productosPorVenta = detalleProductos.stream()
                .collect(Collectors.groupingBy(CompraProductoDetalladaDTO::getIdVenta));

        // 4) Para cada CompraDetalladaDTO, asignar la lista de productos correspondientes
        for (CompraDetalladaDTO dto : comprasBoletos) {
            int ventaId = dto.getIdVenta();
            List<CompraProductoDetalladaDTO> detalles = productosPorVenta.getOrDefault(ventaId, Collections.emptyList());

            // Convertir cada CompraProductoDetalladaDTO a ProductoSelectionDTO
            List<ProductoSelectionDTO> listaProductos = detalles.stream()
                    .map(prodDet -> new ProductoSelectionDTO(
                            prodDet.getIdProducto(),
                            prodDet.getNombreProducto(),
                            prodDet.getCantidad(),
                            prodDet.getPrecioUnitario(),
                            prodDet.getSubtotal()
                    ))
                    .collect(Collectors.toList());

            dto.setProductosComprados(listaProductos);
        }

        return comprasBoletos;
    }

    @Override
    public List<CompraDetalladaDTO> findComprasDeBoletosByClienteId(int idCliente) {
        String sql =
                "SELECT b.IdBoleto, b.PrecioFinal AS PrecioFinalBoleto, b.FechaCompra AS FechaCompraBoleto, " +
                        "       b.CodigoQR AS CodigoQRBoleto, b.IdAsiento AS IdAsientoBoleto, " +
                        "       v.IdVenta, v.Total AS TotalVenta, v.MetodoPago, v.Estado AS EstadoVenta, " +
                        "       v.Facturado, v.IdPromocion, v.Fecha AS FechaVenta, " +
                        "       f.IdFuncion, f.FechaHora AS FechaHoraFuncion, f.Precio AS PrecioBoletoFuncion, " +
                        "       f.Estado AS EstadoFuncion, " +
                        "       p.IdPelicula, p.Titulo AS TituloPelicula, p.Duracion AS DuracionMinutos, " +
                        "       p.Clasificacion AS ClasificacionPelicula, p.Sinopsis AS SinopsisPelicula, " +
                        "       p.Fotografia AS FotografiaPelicula, p.FechaEstreno AS FechaEstrenoPelicula, " +
                        "       tp.Nombre AS NombreTipoPelicula, " +
                        "       s.IdSala, s.Numero AS NumeroSala, s.TipoSala, " +
                        "       c.Nombre AS NombreCine " +
                        "FROM Boleto b " +
                        "JOIN Venta v ON b.IdVenta = v.IdVenta " +
                        "JOIN Funcion f ON b.IdFuncion = f.IdFuncion " +
                        "JOIN Pelicula p ON f.IdPelicula = p.IdPelicula " +
                        "JOIN Sala s ON f.IdSala = s.IdSala " +
                        "JOIN Cine c ON s.IdCine = c.IdCine " +
                        "LEFT JOIN TipoPelicula tp ON p.IdTipoPelicula = tp.IdTipoPelicula " +
                        "WHERE b.IdCliente = ? " +
                        "ORDER BY v.Fecha DESC, b.IdBoleto DESC";

        List<CompraDetalladaDTO> compras = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Construir el objeto FuncionDetallada
                    FuncionDetallada fd = new FuncionDetallada();
                    fd.setIdFuncion(rs.getInt("IdFuncion"));
                    Timestamp tsFunc = rs.getTimestamp("FechaHoraFuncion");
                    if (tsFunc != null) {
                        fd.setFechaHoraFuncion(tsFunc.toLocalDateTime());
                    }
                    fd.setPrecioBoleto(rs.getBigDecimal("PrecioBoletoFuncion"));
                    fd.setEstadoFuncion(rs.getString("EstadoFuncion"));
                    fd.setIdPelicula(rs.getInt("IdPelicula"));
                    fd.setTituloPelicula(rs.getString("TituloPelicula"));
                    fd.setDuracionMinutos(rs.getInt("DuracionMinutos"));
                    fd.setClasificacionPelicula(rs.getString("ClasificacionPelicula"));
                    fd.setSinopsisPelicula(rs.getString("SinopsisPelicula"));
                    fd.setFotografiaPelicula(rs.getString("FotografiaPelicula"));
                    Date fechaEstreno = rs.getDate("FechaEstrenoPelicula");
                    if (fechaEstreno != null) {
                        fd.setFechaEstrenoPelicula(((java.sql.Date) fechaEstreno).toLocalDate());
                    }
                    fd.setNombreTipoPelicula(rs.getString("NombreTipoPelicula"));
                    fd.setIdSala(rs.getInt("IdSala"));
                    fd.setNumeroSala(rs.getInt("NumeroSala"));
                    fd.setTipoSala(rs.getString("TipoSala"));
                    fd.setNombreCine(rs.getString("NombreCine"));

                    // Construir el objeto CompraDetalladaDTO provisional
                    CompraDetalladaDTO compra = new CompraDetalladaDTO();
                    compra.setIdBoleto(String.valueOf(rs.getInt("IdBoleto")));
                    compra.setPrecioFinal(rs.getBigDecimal("PrecioFinalBoleto"));
                    Timestamp tsCompra = rs.getTimestamp("FechaCompraBoleto");
                    if (tsCompra != null) {
                        compra.setFechaCompra(tsCompra.toLocalDateTime());
                    }
                    compra.setCodigoQR(rs.getString("CodigoQRBoleto"));
                    compra.setIdAsiento(String.valueOf(rs.getInt("IdAsientoBoleto")));
                    compra.setIdVenta(rs.getInt("IdVenta"));
                    compra.setTotalVenta(rs.getBigDecimal("TotalVenta"));
                    compra.setMetodoPago(rs.getString("MetodoPago"));
                    compra.setEstadoVenta(rs.getString("EstadoVenta"));
                    compra.setFacturado(rs.getBoolean("Facturado"));
                    compra.setIdPromocion((Integer) rs.getObject("IdPromocion"));
                    compra.setFuncion(fd);

                    // Al inicio, productosComprados queda vacío (se llenará más adelante)
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
            throw new IllegalStateException("No hay un cliente logueado para realizar la compra.");
        }
        int clientId = currentClient.getIdCliente();

        // Calcular suma de boletos
        BigDecimal sumTickets = BigDecimal.ZERO;
        if (func != null && func.getPrecioBoleto() != null && seats != null && !seats.isEmpty()) {
            sumTickets = func.getPrecioBoleto().multiply(BigDecimal.valueOf(seats.size()));
        }

        // Calcular suma de productos
        BigDecimal sumProds = BigDecimal.ZERO;
        if (prods != null && !prods.isEmpty()) {
            sumProds = prods.stream()
                    .map(ProductoSelectionDTO::getSubtotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Gestionar promoción
        PromocionDTO promoAplicada = null;
        BigDecimal discount = BigDecimal.ZERO;
        String codigoPromoManual = ctx.getCodigoPromocion();
        if (codigoPromoManual != null && !codigoPromoManual.isEmpty()) {
            Optional<PromocionDTO> optPromo = promoDAO.findActiveByCodigoAndDate(codigoPromoManual, LocalDate.now());
            if (optPromo.isPresent()) {
                promoAplicada = optPromo.get();
                if (promoAplicada.getDescuento() != null) {
                    discount = sumTickets.add(sumProds).multiply(promoAplicada.getDescuento());
                }
            }
        }

        // Total final
        BigDecimal totalFinal = sumTickets.add(sumProds).subtract(discount);
        LocalDateTime ventaTimestamp = LocalDateTime.now();

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
            // 1) Insertar fila en Venta
            try (PreparedStatement psVenta = conn.prepareStatement(insertVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setTimestamp(1, Timestamp.valueOf(ventaTimestamp)); // Fecha
                psVenta.setBigDecimal(2, totalFinal);
                psVenta.setString(3, ctx.getMetodoPago());
                psVenta.setString(4, "Completado");
                psVenta.setBoolean(5, false);
                if (promoAplicada != null) {
                    psVenta.setInt(6, promoAplicada.getId());
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

            // 2) Insertar boletos
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
                        psBoleto.setString(3, "QR-" + UUID.randomUUID()
                                .toString()
                                .substring(0, 12)
                                .toUpperCase());
                        psBoleto.setInt(4, a.getIdAsiento());
                        psBoleto.setInt(5, func.getIdFuncion());
                        psBoleto.setInt(6, idVenta);
                        psBoleto.setInt(7, clientId);
                        psBoleto.addBatch();
                    }
                    psBoleto.executeBatch();
                }
            }

            // 3) Insertar productos en DetalleVenta
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
