package com.example.cinelinces.DAO.impl;

import com.example.cinelinces.DAO.BoletoDAO;
import com.example.cinelinces.model.Boleto;
import com.example.cinelinces.database.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BoletoDAOImpl implements BoletoDAO {

    @Override
    public List<Boleto> findByClienteId(int idCliente) {
        String sql = "SELECT * FROM Boleto WHERE IdCliente = ?";
        List<Boleto> lista = new ArrayList<>();
        try (
                Connection conn = MySQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Boleto b = new Boleto(
                            rs.getInt("IdBoleto"),
                            rs.getBigDecimal("PrecioFinal"),
                            rs.getTimestamp("FechaCompra").toLocalDateTime(),
                            rs.getString("CodigoQR"),
                            rs.getInt("IdFuncion"),
                            rs.getInt("IdCliente"),
                            rs.getInt("IdAsiento"),
                            rs.getInt("IdVenta")
                    );
                    lista.add(b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void save(Boleto b) { /*...*/ }

    @Override
    public Boleto update(Boleto b) { return null; }

    @Override
    public void delete(Boleto b) { /*...*/ }

    @Override
    public void deleteById(Integer id) { /*...*/ }

    @Override
    public Boleto findById(Integer integer) {
        return null;
    }

    @Override
    public List<Boleto> findAll() {
        return List.of();
    }


}