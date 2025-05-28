package com.example.cinelinces.DAO;

import com.example.cinelinces.model.Boleto;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;

import java.util.List;

public interface BoletoDAO extends GenericDao<Boleto, Integer> {
    /**
     * Obtiene todos los boletos comprados por un cliente.
     * @param idCliente el ID del cliente
     * @return lista de boletos
     */
    List<Boleto> findByClienteId(int idCliente);

    public interface CompraDAO {
        List<CompraDetalladaDTO> findComprasByClienteId(int idCliente);
    }

}
