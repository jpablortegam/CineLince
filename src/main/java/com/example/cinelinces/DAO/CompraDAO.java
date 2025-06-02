package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.CompraProductoDetalladaDTO; // Asumo que aún la usas en algún lugar
import com.example.cinelinces.utils.SummaryContext;

import java.util.List;

public interface CompraDAO {
    // Este método ahora devolverá la CompraDetalladaDTO completa con todos sus detalles,
    // incluyendo la lista de BoletoGeneradoDTOs y productos.
    CompraDetalladaDTO saveFromSummary(SummaryContext ctx);

    // Este método ahora debe devolver una lista de CompraDetalladaDTOs,
    // donde cada DTO represente una VENTA completa con sus boletos y productos.
    List<CompraDetalladaDTO> findComprasByClienteId(int idCliente);

    // Este método puede ser útil para recuperar los detalles de productos para una venta específica
    List<CompraProductoDetalladaDTO> findComprasDeProductosByClienteId(int idCliente);

    // Si aún necesitas esta para la última compra de invitado, su implementación debe ser coherente
    // con el nuevo diseño de CompraDetalladaDTO (que agrupa boletos y productos).
    CompraDetalladaDTO findLastCompraGuest();
}