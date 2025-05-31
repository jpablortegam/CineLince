package com.example.cinelinces.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLConnection {
    private static final Logger LOGGER = Logger.getLogger(MySQLConnection.class.getName());
    private static final Dotenv DOTENV = Dotenv.load();

    private static final String DB_USER = DOTENV.get("DB_USER");
    private static final String DB_NAME = DOTENV.get("DB_NAME");
    private static final String DB_PASS = DOTENV.get("DB_PASS");
    private static final String DB_PORT = DOTENV.get("DB_PORT");
    private static final String DB_HOST = DOTENV.get("DB_HOST");
    private static final String CONNECTION_URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
            DB_HOST,
            DB_PORT,
            DB_NAME
    );

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "No se pudo cargar el driver de MySQL.", e);
        }
        if (DB_USER == null || DB_NAME == null || DB_PASS == null || DB_PORT == null || DB_HOST == null) {
            String msg = "Faltan variables de entorno para la conexión a la base de datos. " +
                    "Revisa tu archivo .env (DB_USER, DB_NAME, DB_PASS, DB_PORT, DB_HOST).";
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalStateException(msg);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(CONNECTION_URL, DB_USER, DB_PASS);
        LOGGER.log(Level.INFO, "→ Se abrió una nueva conexión JDBC a MySQL.");
        return conn;
    }

    public static void disconnect(Connection conn) {
        if (conn == null) return;
        try {
            if (!conn.isClosed()) {
                conn.close();
                LOGGER.log(Level.INFO, "Conexión JDBC cerrada correctamente.");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al cerrar la conexión JDBC.", ex);
        }
    }
}
