package com.example.cinelinces.DAO;

import com.example.cinelinces.model.Cliente;

public interface ClienteDAO extends GenericDao<Cliente, Integer> {
    /**
     * Encuentra un cliente por su dirección de email.
     * El email debe ser único en la base de datos.
     *
     * @param email la dirección de email del cliente.
     * @return el objeto Cliente si se encuentra, o null si no.
     */
    Cliente findByEmail(String email);

    // Podrías añadir aquí otros métodos específicos para Cliente si los necesitas,
    // por ejemplo, findByUsername(String username) si tuvieras un campo username separado.
}