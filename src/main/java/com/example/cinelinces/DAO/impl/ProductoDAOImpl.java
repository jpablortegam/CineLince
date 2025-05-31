// ProductoDAOImpl.java
package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.ProductoDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.ProductoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ProductoDAO que usa MySQLConnection.
 */
public class ProductoDAOImpl implements ProductoDAO {

    /**
     * Recupera todos los productos cuyo campo Estado = 'Disponible'.
     */
    @Override
    public List<ProductoDTO> findAllAvailable() {
        String sql = "SELECT IdProducto, Nombre, Descripcion, Precio, Stock " +
                "FROM Producto " +
                "WHERE Estado = 'Disponible'";

        List<ProductoDTO> lista = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new ProductoDTO(
                        rs.getInt("IdProducto"),
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getBigDecimal("Precio"),
                        rs.getInt("Stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Decrementa el stock de un producto en la cantidad indicada.
     * Esta versión abre SU PROPIA conexión para verificar y actualizar stock.
     * Si no hay suficiente stock o ocurre un error, lanza RuntimeException.
     */
    @Override
    public void decrementStock(int idProducto, int cantidad) {
        // 1) Verificar que haya suficiente stock
        String selectSql = "SELECT Stock FROM Producto WHERE IdProducto = ?";
        String updateSql = "UPDATE Producto SET Stock = Stock - ? WHERE IdProducto = ? AND Stock >= ?";

        try (Connection conn = MySQLConnection.getConnection()) {
            int stockActual;

            // 1.1) SELECT para ver stock actual
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setInt(1, idProducto);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (!rs.next()) {
                        throw new RuntimeException("El producto con ID " + idProducto + " no existe.");
                    }
                    stockActual = rs.getInt("Stock");
                }
            }

            if (stockActual < cantidad) {
                throw new RuntimeException("No hay suficiente stock para el producto ID "
                        + idProducto + ". Stock disponible: " + stockActual);
            }

            // 1.2) UPDATE para restar stock
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, cantidad);
                psUpdate.setInt(2, idProducto);
                psUpdate.setInt(3, cantidad);

                int filasAfectadas = psUpdate.executeUpdate();
                if (filasAfectadas == 0) {
                    // Esto no debería pasar si el SELECT anterior halló stock suficiente.
                    throw new RuntimeException("Error al decrementar stock del producto ID "
                            + idProducto + ". Quizás no había suficiente stock.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al decrementar stock en la base de datos: " + e.getMessage(), e);
        }
    }

    /**
     * Decrementa el stock de un producto usando la CONEXIÓN EXTERNA que se le pase.
     * Se usa cuando queremos incluir este UPDATE dentro de la misma transacción
     * que inserta Venta/Boleto/DetalleVenta en CompraDAOImpl.
     *
     * @param idProducto  ID del producto cuyo stock deseamos restar.
     * @param cantidad    Cuántas unidades restar.
     * @param conn        Conexión ya abierta (autoCommit=false) que controla la transacción completa.
     * @throws SQLException Si algo falla (producto no existe, falta stock, etc.).
     */
    public void decrementStock(int idProducto, int cantidad, Connection conn) throws SQLException {
        // 2) Misma lógica, pero usando conn que viene por parámetro (sin abrir nueva conexión).
        String selectSql = "SELECT Stock FROM Producto WHERE IdProducto = ?";
        String updateSql = "UPDATE Producto SET Stock = Stock - ? WHERE IdProducto = ? AND Stock >= ?";

        int stockActual;

        // 2.1) SELECT para ver stock actual (misma conexión)
        try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
            psSelect.setInt(1, idProducto);
            try (ResultSet rs = psSelect.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("El producto con ID " + idProducto + " no existe.");
                }
                stockActual = rs.getInt("Stock");
            }
        }

        if (stockActual < cantidad) {
            throw new SQLException("No hay suficiente stock para el producto ID "
                    + idProducto + ". Stock disponible: " + stockActual);
        }

        // 2.2) UPDATE para restar stock (misma conexión, sin hacer commit aquí)
        try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
            psUpdate.setInt(1, cantidad);
            psUpdate.setInt(2, idProducto);
            psUpdate.setInt(3, cantidad);

            int filasAfectadas = psUpdate.executeUpdate();
            if (filasAfectadas == 0) {
                // No debería ocurrir si SELECT anterior encontró stock.
                throw new SQLException("Error al decrementar stock del producto ID "
                        + idProducto + ". Quizás no había suficiente stock.");
            }
        }
        // NOTA: NO se cierra ni se hace commit/rollback de conn aquí. Lo hace el llamador.
    }
}
