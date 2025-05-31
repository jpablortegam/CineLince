package com.example.cinelinces.DAO;

import com.example.cinelinces.model.Cliente;

public interface ClienteDAO extends GenericDao<Cliente, Integer> {
    Cliente findByEmail(String email);

}