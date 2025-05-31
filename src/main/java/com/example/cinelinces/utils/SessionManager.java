package com.example.cinelinces.utils;

import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.impl.CompraDAOImpl;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;

import java.util.Collections;
import java.util.List;

public class SessionManager {
    private static SessionManager instance;
    private Cliente currentCliente;
    private final CompraDAO compraDAO;

    private SessionManager() {
        this.compraDAO = new CompraDAOImpl();
    }

    public static synchronized void init() {
        if (instance == null) {
            instance = new SessionManager();
        }
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SessionManager no inicializado. Llama a init() primero.");
        }
        return instance;
    }

    public Cliente getCurrentCliente() {
        return currentCliente;
    }

    public void setCurrentCliente(Cliente c) {
        this.currentCliente = c;
    }

    public void clearSession() {
        this.currentCliente = null;
    }

    public boolean isLoggedIn() {
        return this.currentCliente != null;
    }

    public List<CompraDetalladaDTO> getComprasDetalladas() {
        if (!isLoggedIn()) return Collections.emptyList();
        return compraDAO.findComprasByClienteId(currentCliente.getIdCliente());
    }
}
