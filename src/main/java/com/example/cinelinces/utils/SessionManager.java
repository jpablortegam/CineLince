package com.example.cinelinces.utils; // O el paquete que prefieras para utilidades/servicios

import com.example.cinelinces.model.Cliente;

public class SessionManager {
    private static SessionManager instance;
    private Cliente currentCliente;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Cliente getCurrentCliente() {
        return currentCliente;
    }

    public void setCurrentCliente(Cliente currentCliente) {
        this.currentCliente = currentCliente;
    }

    public void clearSession() {
        this.currentCliente = null;
    }

    public boolean isLoggedIn() {
        return this.currentCliente != null;
    }
}