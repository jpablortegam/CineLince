package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.DAO.ProductoDAO;
import com.example.cinelinces.DAO.impl.PromocionDAOImpl;
import com.example.cinelinces.DAO.impl.ProductoDAOImpl;
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
    private final ProductoDAO productoDAO = new ProductoDAOImpl();

    // --------------------------------------------------------------------------------
    // 1) Recupera todas las compras de un cliente registrado (boletos + productos).
    // --------------------------------------------------------------------------------
    @Override
    public List<CompraDetalladaDTO> findComprasByClienteId(int idCliente) {
        // 1.1) Leer todos los boletos de ese cliente (Venta + Boleto + Función + Película)
        List<CompraDetalladaDTO> comprasBoletos = findComprasDeBoletosByClienteId(idCliente);

        // 1.2) Leer todos los productos comprados (DetalleVenta) de cada venta de ese cliente
        List<CompraProductoDetalladaDTO> detalleProductos = findComprasDeProductosByClienteId(idCliente);

        // 1.3) Agrupar los CompraProductoDetalladaDTO por idVenta
        Map<Integer, List<CompraProductoDetalladaDTO>> productosPorVenta = detalleProductos.stream()
                .collect(Collectors.groupingBy(CompraProductoDetalladaDTO::getIdVenta));

        // 1.4) Para cada DTO de boleto, asignar su lista de productos (si la hay)
        for (CompraDetalladaDTO dto : comprasBoletos) {
            int ventaId = dto.getIdVenta();
            List<CompraProductoDetalladaDTO> detalles = productosPorVenta.getOrDefault(ventaId, Collections.emptyList());

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

    // --------------------------------------------------------------------------------
    // 2) Obtiene todos los boletos (asientos) de un cliente, con datos de Venta y Función.
    // --------------------------------------------------------------------------------
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

                    compras.add(compra);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compras;
    }

    // --------------------------------------------------------------------------------
    // 3) Recupera los productos comprados por el cliente, agrupados por idVenta.
    // --------------------------------------------------------------------------------
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

    // --------------------------------------------------------------------------------
    // 4) Inserta Venta + Boleto(s) + DetalleVenta (productos).
    //    Si el usuario es invitado (currentClient == null), inserta IdCliente = NULL.
    //    Además, decrementa el stock de cada producto comprado.
    // --------------------------------------------------------------------------------
    @Override
    public void saveFromSummary(SummaryContext ctx) {
        List<AsientoDTO> seats = ctx.getSelectedSeats();
        List<ProductoSelectionDTO> prods = ctx.getSelectedProducts();
        FuncionDetallada func = ctx.getSelectedFunction();

        Cliente currentClient = SessionManager.getInstance().getCurrentCliente();
        Integer clientId = (currentClient != null) ? currentClient.getIdCliente() : null;

        // 4.1) Sumar boletos
        BigDecimal sumTickets = BigDecimal.ZERO;
        if (func != null && func.getPrecioBoleto() != null && seats != null && !seats.isEmpty()) {
            sumTickets = func.getPrecioBoleto().multiply(BigDecimal.valueOf(seats.size()));
        }

        // 4.2) Sumar productos
        BigDecimal sumProds = BigDecimal.ZERO;
        if (prods != null && !prods.isEmpty()) {
            sumProds = prods.stream()
                    .map(ProductoSelectionDTO::getSubtotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 4.3) Aplicar promoción (si la hay)
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

        // 4.4) Calcular total final
        BigDecimal totalFinal = sumTickets.add(sumProds).subtract(discount);
        LocalDateTime ventaTimestamp = LocalDateTime.now();

        // 4.5) Sentencias SQL
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
            // ---- 4.6) INSERT en Venta ----
            try (PreparedStatement psVenta = conn.prepareStatement(insertVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setTimestamp(1, Timestamp.valueOf(ventaTimestamp)); // Fecha
                psVenta.setBigDecimal(2, totalFinal);                       // Total
                psVenta.setString(3, ctx.getMetodoPago());                  // MetodoPago
                psVenta.setString(4, "Completado");                          // Estado
                psVenta.setBoolean(5, false);                                // Facturado

                // IdPromocion (si aplica)
                if (promoAplicada != null) {
                    psVenta.setInt(6, promoAplicada.getId());
                } else {
                    psVenta.setNull(6, Types.INTEGER);
                }

                // IdCliente (si es invitado, se inserta NULL)
                if (clientId != null) {
                    psVenta.setInt(7, clientId);
                } else {
                    psVenta.setNull(7, Types.INTEGER);
                }

                psVenta.executeUpdate();
                try (ResultSet keys = psVenta.getGeneratedKeys()) {
                    if (keys.next()) {
                        idVenta = keys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el IdVenta generado.");
                    }
                }
            }

            // ---- 4.7) INSERT en Boleto (uno por cada asiento) ----
            if (seats != null && !seats.isEmpty() && func != null && func.getPrecioBoleto() != null) {
                try (PreparedStatement psBoleto = conn.prepareStatement(insertBoleto)) {
                    BigDecimal descuentoPorBoleto = BigDecimal.ZERO;
                    if (discount.compareTo(BigDecimal.ZERO) > 0 && seats.size() > 0) {
                        descuentoPorBoleto = discount.divide(BigDecimal.valueOf(seats.size()), 2, RoundingMode.HALF_UP);
                    }
                    for (AsientoDTO a : seats) {
                        psBoleto.setTimestamp(1, Timestamp.valueOf(ventaTimestamp)); // FechaCompra
                        BigDecimal precioNetoBoleto = func.getPrecioBoleto().subtract(descuentoPorBoleto);
                        psBoleto.setBigDecimal(2, precioNetoBoleto.max(BigDecimal.ZERO)); // PrecioFinal
                        psBoleto.setString(3, "QR-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
                        psBoleto.setInt(4, a.getIdAsiento());                // IdAsiento
                        psBoleto.setInt(5, func.getIdFuncion());              // IdFuncion
                        psBoleto.setInt(6, idVenta);                          // IdVenta (FK)

                        // IdCliente (si es invitado, NULL)
                        if (clientId != null) {
                            psBoleto.setInt(7, clientId);
                        } else {
                            psBoleto.setNull(7, Types.INTEGER);
                        }

                        psBoleto.addBatch();
                    }
                    psBoleto.executeBatch();
                }
            }

            // ---- 4.8) INSERT en DetalleVenta (productos) ----
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
                // 4.8.1) Decrementar stock de cada producto
                for (ProductoSelectionDTO p : prods) {
                    productoDAO.decrementStock(p.getIdProducto(), p.getCantidad());
                }
            }

            // ---- 4.9) Commit de la transacción ----
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

    // --------------------------------------------------------------------------------
    // 5) Recupera la última venta realizada por INVITADO (IdCliente = NULL),
    //    construye el DTO con todos los detalles (boletos + productos).
    // --------------------------------------------------------------------------------
    public CompraDetalladaDTO findLastCompraGuest() {
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
                        "WHERE v.IdVenta = (SELECT MAX(IdVenta) FROM Venta)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                FuncionDetallada fd = new FuncionDetallada();
                Timestamp tsFunc = rs.getTimestamp("FechaHoraFuncion");
                if (tsFunc != null) {
                    fd.setFechaHoraFuncion(tsFunc.toLocalDateTime());
                }
                fd.setIdFuncion(rs.getInt("IdFuncion"));
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

                // 5.1) Recuperar productos para esta venta
                List<CompraProductoDetalladaDTO> prodList = findComprasDeProductosByVentaId(compra.getIdVenta());
                List<ProductoSelectionDTO> listaProductos = prodList.stream()
                        .map(prodDet -> new ProductoSelectionDTO(
                                prodDet.getIdProducto(),
                                prodDet.getNombreProducto(),
                                prodDet.getCantidad(),
                                prodDet.getPrecioUnitario(),
                                prodDet.getSubtotal()
                        ))
                        .collect(Collectors.toList());
                compra.setProductosComprados(listaProductos);

                return compra;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --------------------------------------------------------------------------------
    // 6) Recupera all productos comprados para una venta específica.
    // --------------------------------------------------------------------------------
    private List<CompraProductoDetalladaDTO> findComprasDeProductosByVentaId(int idVenta) {
        String sql =
                "SELECT dv.IdDetalleVenta, dv.IdVenta, dv.IdProducto, p.Nombre AS NombreProducto, " +
                        "       p.Descripcion AS DescripcionProducto, dv.Cantidad, dv.PrecioUnitario, dv.Subtotal " +
                        "FROM DetalleVenta dv " +
                        "JOIN Producto p ON dv.IdProducto = p.IdProducto " +
                        "WHERE dv.IdVenta = ?";

        List<CompraProductoDetalladaDTO> lista = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);
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
