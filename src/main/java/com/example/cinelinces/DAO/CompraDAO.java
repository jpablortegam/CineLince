// Nueva interfaz para compras detalladas
package com.example.cinelinces.DAO;

import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import java.util.List;

public interface CompraDAO {
    List<CompraDetalladaDTO> findComprasByClienteId(int idCliente);
}
