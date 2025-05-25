package com.example.cinelinces.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MySQLConnection {
    private static Connection conn = null;

    public static void Connect() {
        try {
            if (conn == null || conn.isClosed()) { // Solo conectar si no está abierta
                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Cargar las variables del archivo .env
                Dotenv dotenv = Dotenv.load();

                // Obtener las variables del archivo .env
                String dbUser = dotenv.get("DB_USER");
                String dbName = dotenv.get("DB_NAME");
                String dbPass = dotenv.get("DB_PASS");
                String dbPort = dotenv.get("DB_PORT");
                String dbHost = dotenv.get("DB_HOST");


                System.out.println("dbUser: " + dbUser);

                // Verificar que todas las variables sean válidas
                if (dbUser == null || dbName == null || dbPass == null || dbPort == null || dbHost == null) {
                    throw new IllegalArgumentException("Faltan variables de entorno necesarias para la conexion.");
                }

                // Construir la URL de conexión usando las variables
                String connectionUrl = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", dbHost, dbPort, dbName);

                // Establecer la conexión
                conn = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
                Logger.getLogger(MySQLConnection.class.getName()).log(Level.INFO, "Conexion a la base de datos exitosa.");
            }
        } catch (ClassNotFoundException | SQLException | IllegalArgumentException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, "Error al conectar a la base de datos.", ex);
        }
    }



    // Obtener la conexión
    public static Connection getConnection() {
        if (conn == null || isConnectionClosed()) {
            Connect(); // Si la conexión está cerrada o es nula, intenta reconectar
        }
        return conn;
    }

    // Verificar si la conexión está cerrada
    private static boolean isConnectionClosed() {
        try {
            return conn == null || conn.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    // Desconectar de la base de datos
    public static void Disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Conexión a la base de datos cerrada.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
