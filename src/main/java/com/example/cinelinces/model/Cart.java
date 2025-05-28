/*
 * Modelos para gestión de carrito y DAOs de compra
 */

// ---------- Carrito ----------
package com.example.cinelinces.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el carrito de compras de un cliente.
 */
public class Cart {
    private int clienteId;
    private List<CartItem> items = new ArrayList<>();
    private String promocionCodigo;
    private BigDecimal total = BigDecimal.ZERO;

    public Cart(int clienteId) {
        this.clienteId = clienteId;
    }

    public void addItem(CartItem item) {
        items.add(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // aquí aplicar lógica de promociones usando promocionCodigo
    }

    // Getters y setters
    public int getClienteId() { return clienteId; }
    public List<CartItem> getItems() { return items; }
    public String getPromocionCodigo() { return promocionCodigo; }
    public void setPromocionCodigo(String promocionCodigo) {
        this.promocionCodigo = promocionCodigo;
        recalculateTotal();
    }
    public BigDecimal getTotal() { return total; }
}
