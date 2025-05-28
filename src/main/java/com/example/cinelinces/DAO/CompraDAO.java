// CompraDAO.java
package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.CompraProductoDetalladaDTO;

import java.util.List;

public interface CompraDAO {
    /** Historial “genérico” (por compatibilidad) */
    List<CompraDetalladaDTO> findComprasByClienteId(int idCliente);

    /** Historial de boletos para un cliente */
    List<CompraDetalladaDTO> findComprasDeBoletosByClienteId(int idCliente);

    /** Historial de productos (dulcería) para un cliente */
    List<CompraProductoDetalladaDTO> findComprasDeProductosByClienteId(int idCliente);
}
