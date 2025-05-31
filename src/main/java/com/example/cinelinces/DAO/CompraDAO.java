// CompraDAO.java
package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.CompraProductoDetalladaDTO;
import com.example.cinelinces.utils.SummaryContext;

import java.util.List;

public interface CompraDAO {
    List<CompraDetalladaDTO> findComprasByClienteId(int idCliente);

    List<CompraDetalladaDTO> findComprasDeBoletosByClienteId(int idCliente);

    List<CompraProductoDetalladaDTO> findComprasDeProductosByClienteId(int idCliente);

    void saveFromSummary(SummaryContext ctx);
}
