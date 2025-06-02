package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.DAO.ProductoDAO;
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.BoletoGeneradoDTO;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.CompraProductoDetalladaDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.PromocionDTO;
import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.utils.SessionManager;
import com.example.cinelinces.utils.SummaryContext;
import com.example.cinelinces.database.MySQLConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date; // java.util.Date para compatibilidad con ResultSet.getDate
import java.util.stream.Collectors;

public class CompraDAOImpl implements CompraDAO {

    private final PromocionDAO promoDAO = new PromocionDAOImpl();
    private final ProductoDAO productoDAO = new ProductoDAOImpl();

    @Override
    public List<CompraDetalladaDTO> findComprasByClienteId(int idCliente) {
        List<CompraDetalladaDTO> comprasAgrupadas = new ArrayList<>();
        Map<Integer, CompraDetalladaDTO> ventasMap = new LinkedHashMap<>(); // Para mantener el orden y agrupar por IdVenta

        // Consulta SQL para obtener detalles de la venta, boletos y la función asociada.
        // Los detalles de productos se cargarán por separado para cada venta.
        String sql =
                "SELECT v.IdVenta, v.Fecha AS FechaVenta, v.Total AS TotalVenta, v.MetodoPago, v.Estado AS EstadoVenta, " +
                        "       v.Facturado, v.IdPromocion, promo.CodigoPromo, promo.Nombre AS NombrePromocion, " +
                        "       c.IdCliente, c.Nombre AS NombreCliente, c.Apellido AS ApellidoCliente, " +
                        "       b.IdBoleto, b.PrecioFinal AS PrecioFinalBoleto, b.FechaCompra AS FechaCompraBoleto, " +
                        "       b.CodigoQR AS CodigoQRBoleto, b.IdAsiento AS IdAsientoBoleto, " +
                        "       a.Fila AS FilaAsiento, a.Numero AS NumeroAsiento, " +
                        "       f.IdFuncion, f.FechaHora AS FechaHoraFuncion, f.Precio AS PrecioBoletoFuncion, " +
                        "       f.Estado AS EstadoFuncion, " +
                        "       p.IdPelicula, p.Titulo AS TituloPelicula, p.Duracion AS DuracionMinutos, " +
                        "       p.Clasificacion AS ClasificacionPelicula, p.Sinopsis AS SinopsisPelicula, " +
                        "       p.Fotografia AS FotografiaPelicula, p.FechaEstreno AS FechaEstrenoPelicula, " +
                        "       tp.Nombre AS NombreTipoPelicula, " +
                        "       s.IdSala, s.Numero AS NumeroSala, s.TipoSala, " +
                        "       cine.Nombre AS NombreCine " +
                        "FROM Venta v " +
                        "LEFT JOIN Cliente c ON v.IdCliente = c.IdCliente " +
                        "LEFT JOIN Promocion promo ON v.IdPromocion = promo.IdPromocion " +
                        "JOIN Boleto b ON v.IdVenta = b.IdVenta " +
                        "JOIN Asiento a ON b.IdAsiento = a.IdAsiento " +
                        "JOIN Funcion f ON b.IdFuncion = f.IdFuncion " +
                        "JOIN Pelicula p ON f.IdPelicula = p.IdPelicula " +
                        "JOIN Sala s ON f.IdSala = s.IdSala " +
                        "JOIN Cine cine ON s.IdCine = cine.IdCine " +
                        "LEFT JOIN TipoPelicula tp ON p.IdTipoPelicula = tp.IdTipoPelicula " +
                        "WHERE v.IdCliente = ? " +
                        "ORDER BY v.Fecha DESC, b.IdBoleto ASC"; // Ordenar para agrupar boletos por venta

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idVenta = rs.getInt("IdVenta");
                    CompraDetalladaDTO compra = ventasMap.get(idVenta);

                    if (compra == null) {
                        // Si es una nueva venta, crear el DTO principal
                        compra = new CompraDetalladaDTO();
                        compra.setIdVenta(idVenta);
                        compra.setFechaCompra(rs.getTimestamp("FechaVenta").toLocalDateTime());
                        compra.setTotalVenta(rs.getBigDecimal("TotalVenta"));
                        compra.setMetodoPago(rs.getString("MetodoPago"));
                        compra.setEstadoVenta(rs.getString("EstadoVenta"));
                        compra.setFacturado(rs.getBoolean("Facturado"));
                        compra.setIdPromocion(rs.getObject("IdPromocion") != null ? rs.getInt("IdPromocion") : null);
                        compra.setCodigoPromocion(rs.getString("CodigoPromo"));
                        compra.setNombrePromocion(rs.getString("NombrePromocion"));

                        compra.setIdCliente(rs.getInt("IdCliente"));
                        // Manejo de cliente que podría ser nulo en Venta (si fuera el caso)
                        String nombreCliente = rs.getString("NombreCliente");
                        String apellidoCliente = rs.getString("ApellidoCliente");
                        compra.setNombreCliente((nombreCliente != null ? nombreCliente : "") + (apellidoCliente != null ? " " + apellidoCliente : ""));


                        // Detalles de la función para esta venta (asumiendo una sola función por venta)
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
                        Date fechaEstreno = rs.getDate("FechaEstrenoPelicula");
                        if (fechaEstreno != null) { // Convertir java.sql.Date a LocalDate
                            fd.setFechaEstrenoPelicula(((java.sql.Date) fechaEstreno).toLocalDate());
                        }
                        fd.setNombreTipoPelicula(rs.getString("NombreTipoPelicula"));
                        fd.setIdSala(rs.getInt("IdSala"));
                        fd.setNumeroSala(rs.getInt("NumeroSala"));
                        fd.setTipoSala(rs.getString("TipoSala"));
                        fd.setNombreCine(rs.getString("NombreCine"));
                        compra.setFuncion(fd);

                        // Cargar productos para esta venta
                        List<CompraProductoDetalladaDTO> productosDetalles = findComprasDeProductosByVentaId(idVenta);
                        List<ProductoSelectionDTO> listaProductos = productosDetalles.stream()
                                .map(prodDet -> new ProductoSelectionDTO(
                                        prodDet.getIdProducto(),
                                        prodDet.getNombreProducto(),
                                        prodDet.getCantidad(),
                                        prodDet.getPrecioUnitario(),
                                        prodDet.getSubtotal()
                                ))
                                .collect(Collectors.toList());
                        compra.setProductosComprados(listaProductos);

                        ventasMap.put(idVenta, compra);
                        comprasAgrupadas.add(compra); // Añadir a la lista final solo una vez por venta
                    }

                    // Añadir cada boleto a la lista de boletos generados de la venta
                    BoletoGeneradoDTO boletoGenerado = new BoletoGeneradoDTO(
                            rs.getInt("IdBoleto"),
                            rs.getString("CodigoQRBoleto"),
                            rs.getInt("IdAsientoBoleto"),
                            rs.getString("FilaAsiento"),
                            rs.getInt("NumeroAsiento"),
                            rs.getBigDecimal("PrecioFinalBoleto"),
                            rs.getTimestamp("FechaCompraBoleto").toLocalDateTime(),
                            rs.getInt("IdFuncion")
                    );
                    compra.getBoletosGenerados().add(boletoGenerado);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comprasAgrupadas;
    }

    // Este método solo recupera los productos asociados a una venta específica.
    // Es auxiliar para findComprasByClienteId y findLastCompraGuest.
    @Override
    public List<CompraProductoDetalladaDTO> findComprasDeProductosByClienteId(int idCliente) {
        // Este método parece diseñado para obtener productos de UN cliente, no de una venta específica.
        // Su nombre es confuso. Si lo que quieres es obtener todos los productos de todas las ventas de un cliente,
        // el SQL está bien. Si quieres por VENTA (como en el método privado de abajo), debería ser por IdVenta.
        // Asumo que este método findComprasDeProductosByClienteId debería ser llamado por ID de VENTA para ser útil en el contexto de CompraDetalladaDTO.
        // Si se usa en otros contextos para obtener todos los productos comprados por un cliente sin agrupar por venta,
        // su implementación actual es correcta.

        // Por ahora, redirigiré a la versión por venta si es necesario,
        // o si este método se usaba para agrupar, la lógica de findComprasByClienteId ya lo hace.
        // Si este método es llamado externamente y se espera que devuelva productos
        // asociados a un cliente pero no necesariamente a una sola venta, la SQL original está bien.
        // Dada la refactorización para agrupar por venta en findComprasByClienteId, este método ahora
        // es redundante o su nombre es impreciso si se quiere por Venta.
        return new ArrayList<>(); // Implementación mínima o se podría borrar si no se usa más.
    }

    // Método auxiliar para cargar productos por IdVenta
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

    @Override
    public CompraDetalladaDTO saveFromSummary(SummaryContext ctx) {
        Connection conn = null;
        try {
            conn = MySQLConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar la Venta (compra principal)
            String sqlInsertVenta = "INSERT INTO Venta (Fecha, Total, MetodoPago, Estado, Facturado, IdPromocion, IdCliente) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psVenta = conn.prepareStatement(sqlInsertVenta, Statement.RETURN_GENERATED_KEYS);

            LocalDateTime fechaCompraVenta = LocalDateTime.now();
            psVenta.setTimestamp(1, Timestamp.valueOf(fechaCompraVenta));
            psVenta.setBigDecimal(2, ctx.getTotalVentaCalculado().setScale(2, RoundingMode.HALF_UP)); // Usa el total calculado del contexto
            psVenta.setString(3, ctx.getMetodoPago());
            psVenta.setString(4, "Completado");
            psVenta.setBoolean(5, false);

            Integer idPromocion = null;
            if (ctx.getCodigoPromocion() != null && !ctx.getCodigoPromocion().isEmpty()) {
                // Se busca la promo de nuevo para obtener su ID, aunque ya se validó en el controlador
                Optional<PromocionDTO> optPromo = promoDAO.findActiveByCodigoAndDate(ctx.getCodigoPromocion(), fechaCompraVenta.toLocalDate());
                if (optPromo.isPresent()) {
                    idPromocion = optPromo.get().getId();
                }
            }
            psVenta.setObject(6, idPromocion, Types.INTEGER);

            Integer clientId = (SessionManager.getInstance().isLoggedIn() && SessionManager.getInstance().getCurrentCliente() != null)
                    ? SessionManager.getInstance().getCurrentCliente().getIdCliente()
                    : null;
            psVenta.setObject(7, clientId, Types.INTEGER);

            psVenta.executeUpdate();
            ResultSet rsVentaKeys = psVenta.getGeneratedKeys();
            int idVentaGenerado;
            if (rsVentaKeys.next()) {
                idVentaGenerado = rsVentaKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID de la venta generada.");
            }

            // 2. Insertar los Boletos y recopilar la información generada
            List<BoletoGeneradoDTO> boletosGenerados = new ArrayList<>();
            FuncionDetallada funcionActual = ctx.getSelectedFunction();
            if (ctx.getSelectedSeats() != null && !ctx.getSelectedSeats().isEmpty() && funcionActual != null) {
                String sqlInsertBoleto = "INSERT INTO Boleto (PrecioFinal, FechaCompra, CodigoQR, IdAsiento, IdFuncion, IdVenta, IdCliente) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psBoleto = conn.prepareStatement(sqlInsertBoleto, Statement.RETURN_GENERATED_KEYS);

                // Calcular el precio de cada boleto considerando el descuento proporcional de la venta
                BigDecimal subtotalBoletosSinDesc = funcionActual.getPrecioBoleto().multiply(new BigDecimal(ctx.getSelectedSeats().size()));
                BigDecimal subtotalProductos = ctx.getSelectedProducts().stream()
                        .map(ProductoSelectionDTO::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalGeneralSinDesc = subtotalBoletosSinDesc.add(subtotalProductos);

                BigDecimal descuentoTotalVenta = BigDecimal.ZERO;
                if (idPromocion != null && totalGeneralSinDesc.compareTo(BigDecimal.ZERO) > 0) {
                    Optional<PromocionDTO> optPromo = promoDAO.findActiveByCodigoAndDate(ctx.getCodigoPromocion(), fechaCompraVenta.toLocalDate());
                    if (optPromo.isPresent()) {
                        descuentoTotalVenta = totalGeneralSinDesc.multiply(optPromo.get().getDescuento());
                    }
                }

                // Si el descuento total es mayor que el subtotal de boletos, el precio del boleto puede ser 0
                BigDecimal descuentoPorBoletoUnitario = BigDecimal.ZERO;
                if (subtotalBoletosSinDesc.compareTo(BigDecimal.ZERO) > 0) { // Evitar división por cero
                    // Porcentaje de descuento aplicado a la base de boletos
                    BigDecimal porcentajeDescuentoBoletos = descuentoTotalVenta.divide(totalGeneralSinDesc, 4, RoundingMode.HALF_UP);
                    descuentoPorBoletoUnitario = porcentajeDescuentoBoletos.multiply(funcionActual.getPrecioBoleto());
                }


                for (AsientoDTO asiento : ctx.getSelectedSeats()) {
                    String codigoQR = "QR-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
                    BigDecimal precioBoletoFinal = funcionActual.getPrecioBoleto().subtract(descuentoPorBoletoUnitario).max(BigDecimal.ZERO);

                    psBoleto.setBigDecimal(1, precioBoletoFinal.setScale(2, RoundingMode.HALF_UP));
                    psBoleto.setTimestamp(2, Timestamp.valueOf(fechaCompraVenta));
                    psBoleto.setString(3, codigoQR);
                    psBoleto.setInt(4, asiento.getIdAsiento());
                    psBoleto.setInt(5, funcionActual.getIdFuncion());
                    psBoleto.setInt(6, idVentaGenerado);
                    psBoleto.setObject(7, clientId, Types.INTEGER); // IdCliente en Boleto

                    psBoleto.executeUpdate();

                    ResultSet rsBoletoKeys = psBoleto.getGeneratedKeys();
                    if (rsBoletoKeys.next()) {
                        int idBoletoGenerado = rsBoletoKeys.getInt(1);

                        // ✔️ Ahora que el boleto está guardado exitosamente, se actualiza el asiento:
                        new AsientoDAOImpl().updateEstadoAsientoEnFuncion(
                                funcionActual.getIdFuncion(),
                                asiento.getIdAsiento(),
                                "Ocupado");

                        boletosGenerados.add(new BoletoGeneradoDTO(
                                idBoletoGenerado, codigoQR, asiento.getIdAsiento(),
                                asiento.getFila(), asiento.getNumero(),
                                precioBoletoFinal, fechaCompraVenta,
                                funcionActual.getIdFuncion()
                        ));
                    } else {
                        throw new SQLException("No se pudo obtener el ID del boleto generado para asiento " + asiento.getIdAsiento());
                    }
                }

            }

            // 3. Insertar DetalleVenta (para productos)
            if (ctx.getSelectedProducts() != null && !ctx.getSelectedProducts().isEmpty()) {
                String sqlInsertDetalle = "INSERT INTO DetalleVenta (IdVenta, IdProducto, Cantidad, PrecioUnitario, Subtotal) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psDetalle = conn.prepareStatement(sqlInsertDetalle);
                for (ProductoSelectionDTO prod : ctx.getSelectedProducts()) {
                    psDetalle.setInt(1, idVentaGenerado);
                    psDetalle.setInt(2, prod.getIdProducto());
                    psDetalle.setInt(3, prod.getCantidad());
                    psDetalle.setBigDecimal(4, prod.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP));
                    psDetalle.setBigDecimal(5, prod.getSubtotal().setScale(2, RoundingMode.HALF_UP));
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();

                // Decrementar stock de productos
                for (ProductoSelectionDTO p : ctx.getSelectedProducts()) {
                    ((ProductoDAOImpl) productoDAO).decrementStock(p.getIdProducto(), p.getCantidad(), conn);
                }
            }

            conn.commit(); // Confirmar la transacción

            // Crear y devolver la CompraDetalladaDTO completa con la información generada
            CompraDetalladaDTO compraFinal = new CompraDetalladaDTO();
            compraFinal.setIdVenta(idVentaGenerado);
            compraFinal.setFechaCompra(fechaCompraVenta);
            compraFinal.setIdCliente(clientId);
            compraFinal.setNombreCliente(SessionManager.getInstance().isLoggedIn() ? SessionManager.getInstance().getCurrentCliente().getNombre() + " " + SessionManager.getInstance().getCurrentCliente().getApellido() : "Invitado");
            compraFinal.setTotalVenta(ctx.getTotalVentaCalculado().setScale(2, RoundingMode.HALF_UP));
            compraFinal.setMetodoPago(ctx.getMetodoPago());
            compraFinal.setEstadoVenta("Completado");
            compraFinal.setFacturado(false);
            compraFinal.setIdPromocion(idPromocion);
            if (idPromocion != null) {
                Optional<PromocionDTO> optPromo = promoDAO.findActiveByCodigoAndDate(ctx.getCodigoPromocion(), fechaCompraVenta.toLocalDate());
                if (optPromo.isPresent()) {
                    compraFinal.setCodigoPromocion(optPromo.get().getCodigo());
                    compraFinal.setNombrePromocion(optPromo.get().getNombre());
                }
            }
            compraFinal.setFuncion(funcionActual); // Mantiene la función seleccionada
            compraFinal.setProductosComprados(ctx.getSelectedProducts()); // Mantiene los productos seleccionados
            compraFinal.setBoletosGenerados(boletosGenerados); // **AQUÍ ESTÁ LA CLAVE: los boletos individuales generados**

            return compraFinal;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Deshacer la transacción en caso de error
                } catch (SQLException exRollback) {
                    exRollback.printStackTrace();
                }
            }
            throw new RuntimeException("Error al guardar la compra: " + e.getMessage(), e);

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

    @Override
    public CompraDetalladaDTO findLastCompraGuest() {
        // Este método recupera una compra detallada para un invitado,
        // buscando la última venta donde IdCliente es NULL.
        // Y agrupa *todos* los boletos y productos asociados a esa venta
        // dentro de una única CompraDetalladaDTO.
        String sqlVenta = "SELECT MAX(IdVenta) FROM Venta WHERE IdCliente IS NULL";
        int lastVentaId = -1;

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlVenta)) {
            if (rs.next()) {
                lastVentaId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (lastVentaId == -1) return null; // No hay ventas de invitado

        // SQL para obtener los detalles de la venta y los boletos asociados
        String sql =
                "SELECT v.IdVenta, v.Fecha AS FechaVenta, v.Total AS TotalVenta, v.MetodoPago, v.Estado AS EstadoVenta, " +
                        "       v.Facturado, v.IdPromocion, promo.CodigoPromo, promo.Nombre AS NombrePromocion, " +
                        "       b.IdBoleto, b.PrecioFinal AS PrecioFinalBoleto, b.FechaCompra AS FechaCompraBoleto, " +
                        "       b.CodigoQR AS CodigoQRBoleto, b.IdAsiento AS IdAsientoBoleto, " +
                        "       a.Fila AS FilaAsiento, a.Numero AS NumeroAsiento, " + // Datos del asiento
                        "       f.IdFuncion, f.FechaHora AS FechaHoraFuncion, f.Precio AS PrecioBoletoFuncion, " +
                        "       f.Estado AS EstadoFuncion, " +
                        "       p.IdPelicula, p.Titulo AS TituloPelicula, p.Duracion AS DuracionMinutos, " +
                        "       p.Clasificacion AS ClasificacionPelicula, p.Sinopsis AS SinopsisPelicula, " +
                        "       p.Fotografia AS FotografiaPelicula, p.FechaEstreno AS FechaEstrenoPelicula, " +
                        "       tp.Nombre AS NombreTipoPelicula, " +
                        "       s.IdSala, s.Numero AS NumeroSala, s.TipoSala, " +
                        "       cine.Nombre AS NombreCine " +
                        "FROM Venta v " +
                        "LEFT JOIN Promocion promo ON v.IdPromocion = promo.IdPromocion " +
                        "JOIN Boleto b ON v.IdVenta = b.IdVenta " +
                        "JOIN Asiento a ON b.IdAsiento = a.IdAsiento " +
                        "JOIN Funcion f ON b.IdFuncion = f.IdFuncion " +
                        "JOIN Pelicula p ON f.IdPelicula = p.IdPelicula " +
                        "JOIN Sala s ON f.IdSala = s.IdSala " +
                        "JOIN Cine cine ON s.IdCine = cine.IdCine " +
                        "LEFT JOIN TipoPelicula tp ON p.IdTipoPelicula = tp.IdTipoPelicula " +
                        "WHERE v.IdVenta = ? " +
                        "ORDER BY b.IdBoleto ASC"; // Importante para recuperar boletos en orden

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lastVentaId);
            try (ResultSet rs = ps.executeQuery()) {
                CompraDetalladaDTO compra = null;
                while (rs.next()) {
                    if (compra == null) {
                        // Inicializar CompraDetalladaDTO una sola vez con los datos de la venta
                        compra = new CompraDetalladaDTO();
                        compra.setIdVenta(rs.getInt("IdVenta"));
                        compra.setFechaCompra(rs.getTimestamp("FechaVenta").toLocalDateTime());
                        compra.setTotalVenta(rs.getBigDecimal("TotalVenta"));
                        compra.setMetodoPago(rs.getString("MetodoPago"));
                        compra.setEstadoVenta(rs.getString("EstadoVenta"));
                        compra.setFacturado(rs.getBoolean("Facturado"));
                        compra.setIdPromocion(rs.getObject("IdPromocion") != null ? rs.getInt("IdPromocion") : null);
                        compra.setCodigoPromocion(rs.getString("CodigoPromo"));
                        compra.setNombrePromocion(rs.getString("NombrePromocion"));
                        compra.setNombreCliente("Invitado"); // Para compras de invitado

                        // Detalles de la función (asumimos que todos los boletos de esta venta son de la misma función)
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
                        Date fechaEstreno = rs.getDate("FechaEstrenoPelicula");
                        if (fechaEstreno != null) { // Convertir java.sql.Date a LocalDate
                            fd.setFechaEstrenoPelicula(((java.sql.Date) fechaEstreno).toLocalDate());
                        }
                        fd.setNombreTipoPelicula(rs.getString("NombreTipoPelicula"));
                        fd.setIdSala(rs.getInt("IdSala"));
                        fd.setNumeroSala(rs.getInt("NumeroSala"));
                        fd.setTipoSala(rs.getString("TipoSala"));
                        fd.setNombreCine(rs.getString("NombreCine"));
                        compra.setFuncion(fd);

                        // Cargar productos para esta venta
                        List<CompraProductoDetalladaDTO> productosDetalles = findComprasDeProductosByVentaId(compra.getIdVenta());
                        List<ProductoSelectionDTO> listaProductos = productosDetalles.stream()
                                .map(prodDet -> new ProductoSelectionDTO(
                                        prodDet.getIdProducto(),
                                        prodDet.getNombreProducto(),
                                        prodDet.getCantidad(),
                                        prodDet.getPrecioUnitario(),
                                        prodDet.getSubtotal()
                                ))
                                .collect(Collectors.toList());
                        compra.setProductosComprados(listaProductos);
                    }

                    // Añadir cada boleto individual a la lista de boletos generados
                    BoletoGeneradoDTO boletoGenerado = new BoletoGeneradoDTO(
                            rs.getInt("IdBoleto"),
                            rs.getString("CodigoQRBoleto"),
                            rs.getInt("IdAsientoBoleto"),
                            rs.getString("FilaAsiento"),
                            rs.getInt("NumeroAsiento"),
                            rs.getBigDecimal("PrecioFinalBoleto"),
                            rs.getTimestamp("FechaCompraBoleto").toLocalDateTime(),
                            rs.getInt("IdFuncion")
                    );
                    compra.getBoletosGenerados().add(boletoGenerado);
                }
                return compra;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}