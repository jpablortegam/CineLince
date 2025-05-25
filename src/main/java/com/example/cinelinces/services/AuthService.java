package com.example.cinelinces.services;


/*
 * Lógica de autenticación / registro (stubs).
 */


public class AuthService {
    public static boolean authenticate(String user, String pass) {
        // TODO: conectar con tu backend real
        return "admin".equals(user) && "password".equals(pass);
    }

    public static boolean register(String name, String email, String user, String pass) {
        // TODO: conectar con tu backend real
        return true;
    }
}
