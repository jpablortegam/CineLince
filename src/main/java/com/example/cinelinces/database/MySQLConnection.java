package com.example.cinelinces.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase utilitaria para abrir conexiones a MySQL.
 * Cada vez que se invoca getConnection(), se crea una conexión nueva mediante DriverManager.getConnection(...).
 *
 * Para que funcione, debe haber un archivo .env en la raíz del proyecto (o en /resources), con las siguientes variables:
 *   DB_USER=…
 *   DB_NAME=…
 *   DB_PASS=…
 *   DB_PORT=…
 *   DB_HOST=…
 */
public class MySQLConnection {
    private static final Logger LOGGER = Logger.getLogger(MySQLConnection.class.getName());
    private static final Dotenv DOTENV = Dotenv.load();

    // Leemos las variables de entorno UNA SOLA VEZ y las guardamos en constantes:
    private static final String DB_USER = DOTENV.get("DB_USER");
    private static final String DB_NAME = DOTENV.get("DB_NAME");
    private static final String DB_PASS = DOTENV.get("DB_PASS");
    private static final String DB_PORT = DOTENV.get("DB_PORT");
    private static final String DB_HOST = DOTENV.get("DB_HOST");

    // Construye la URL de conexión. Por ejemplo: "jdbc:mysql://localhost:3306/cinemasystem?serverTimezone=UTC"
    private static final String CONNECTION_URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
            DB_HOST,
            DB_PORT,
            DB_NAME
    );

    static {
        try {
            // Cargar el driver de MySQL una sola vez
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "No se pudo cargar el driver de MySQL.", e);
        }

        // Verificar que todas las variables del .env estén presentes
        if (DB_USER == null || DB_NAME == null || DB_PASS == null || DB_PORT == null || DB_HOST == null) {
            String msg = "Faltan variables de entorno para la conexión a la base de datos. " +
                    "Revisa tu archivo .env (DB_USER, DB_NAME, DB_PASS, DB_PORT, DB_HOST).";
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Abre y devuelve una conexión NUEVA a la base de datos.
     * Cada vez que llames a este método, se generará un Connection distinto.
     *
     * @return Connection abierto (auto-commit = true por defecto).
     * @throws SQLException si falla al conectarse.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(CONNECTION_URL, DB_USER, DB_PASS);
        LOGGER.log(Level.INFO, "→ Se abrió una nueva conexión JDBC a MySQL.");
        return conn;
    }

    // (Opcional) Si en algún momento necesitas cerrar manualmente, puedes invocar:
    //   MySQLConnection.disconnect(conn);
    //
    // Pero la idea es que cada DAO cierre su propio Connection en el finally block.
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
