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
                            rs.getString("CodigoPromo")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promos;
    }

    @Override
    public List<PromocionDTO> findAllActivePromos() {
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
                        rs.getString("CodigoPromo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promos;
    }

    @Override
    public Optional<PromocionDTO> findByCodigo(String codigo) {
        String sql = "SELECT IdPromocion, Nombre, Descuento, CodigoPromo " +
                "FROM Promocion " +
                "WHERE CodigoPromo = ? " +
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
                            rs.getString("CodigoPromo")
                    );
                    return Optional.of(promo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<PromocionDTO> findActiveByCodigoAndDate(String codigo, LocalDate fecha) {
        String sql = "SELECT IdPromocion, Nombre, Descuento, CodigoPromo " +
                "FROM Promocion " +
                "WHERE CodigoPromo = ? " +
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
                            rs.getString("CodigoPromo")
                    );
                    return Optional.of(promo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}