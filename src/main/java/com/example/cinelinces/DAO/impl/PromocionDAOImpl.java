package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.database.MySQLConnection;
import com.example.cinelinces.model.DTO.PromocionDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PromocionDAOImpl implements PromocionDAO {

    @Override
    public List<PromocionDTO> findActiveByDate(LocalDate fecha) {
        // Corregido: Usar CodigoPromo
        String sql = "SELECT IdPromocion, Nombre, Descuento, CodigoPromo " +
                "FROM Promocion " +
                "WHERE FechaInicio <= ? " +
                "  AND FechaFin >= ? " +
                "  AND Estado = 'Activa'";
        List<PromocionDTO> promos = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Date sqlDate = Date.valueOf(fecha);
            ps.setDate(1, sqlDate);
            ps.setDate(2, sqlDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    promos.add(new PromocionDTO(
                            rs.getInt("IdPromocion"),
                            rs.getString("Nombre"),
                            rs.getBigDecimal("Descuento"),
                            rs.getString("CodigoPromo") // Corregido
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Considera un mejor manejo de excepciones/logging
        }
        return promos;
    }

    @Override
    public List<PromocionDTO> findAllActivePromos() {
        // Corregido: Usar CodigoPromo
        String sql = "SELECT IdPromocion, Nombre, Descuento, CodigoPromo " +
                "FROM Promocion " +
                "WHERE Estado = 'Activa'";
        List<PromocionDTO> promos = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                promos.add(new PromocionDTO(
                        rs.getInt("IdPromocion"),
                        rs.getString("Nombre"),
                        rs.getBigDecimal("Descuento"),
                        rs.getString("CodigoPromo") // Corregido
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Considera un mejor manejo de excepciones/logging
        }
        return promos;
    }

    @Override
    public Optional<PromocionDTO> findByCodigo(String codigo) {
        // Corregido: Usar CodigoPromo.
        // Nota: Este método, tal como está, busca una promoción activa por código pero SIN verificar las fechas.
        // Podrías considerar si este es el comportamiento deseado o si debería también incluir la validación de fecha.
        String sql = "SELECT IdPromocion, Nombre, Descuento, CodigoPromo " +
                "FROM Promocion " +
                "WHERE CodigoPromo = ? " + // Corregido
                "  AND Estado = 'Activa' " +
                "LIMIT 1";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PromocionDTO promo = new PromocionDTO(
                            rs.getInt("IdPromocion"),
                            rs.getString("Nombre"),
                            rs.getBigDecimal("Descuento"),
                            rs.getString("CodigoPromo") // Corregido
                    );
                    return Optional.of(promo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Considera un mejor manejo de excepciones/logging
        }
        return Optional.empty();
    }

    /**
     * Busca una promoción por su código, que esté activa y sea vigente para la fecha especificada.
     * @param codigo El código de la promoción.
     * @param fecha La fecha para la cual la promoción debe ser válida.
     * @return Un Optional conteniendo la PromocionDTO si se encuentra y es válida, o Optional.empty() en caso contrario.
     */
    @Override
    public Optional<PromocionDTO> findActiveByCodigoAndDate(String codigo, LocalDate fecha) {
        // Nuevo método: Usa CodigoPromo y filtra por fecha
        String sql = "SELECT IdPromocion, Nombre, Descuento, CodigoPromo " +
                "FROM Promocion " +
                "WHERE CodigoPromo = ? " +      // Corregido
                "  AND Estado = 'Activa' " +
                "  AND FechaInicio <= ? " +
                "  AND FechaFin >= ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            Date sqlDate = Date.valueOf(fecha);
            ps.setDate(2, sqlDate);
            ps.setDate(3, sqlDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PromocionDTO promo = new PromocionDTO(
                            rs.getInt("IdPromocion"),
                            rs.getString("Nombre"),
                            rs.getBigDecimal("Descuento"),
                            rs.getString("CodigoPromo") // Corregido
                    );
                    return Optional.of(promo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Considera un mejor manejo de excepciones/logging
        }
        return Optional.empty();
    }
}