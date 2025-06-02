package com.example.cinelinces.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompraDetalladaDTO {
    // Campos de la Venta (compra principal)
    private int idVenta;
    private LocalDateTime fechaCompra;
    private Integer idCliente; // Puede ser null para invitados
    private String nombreCliente; // Nombre completo del cliente o "Invitado"
    private BigDecimal totalVenta; // El total de la venta (boletos + productos - descuento)
    private String metodoPago;
    private String estadoVenta;
    private boolean facturado;
    private Integer idPromocion; // ID de la promoción aplicada
    private String codigoPromocion; // Código de la promoción
    private String nombrePromocion; // Nombre de la promoción

    // Información de la Función (asumiendo que una venta es para una sola función)
    // Si una venta pudiera abarcar funciones diferentes, este campo debería ser null
    // y la función estaría en cada BoletoGeneradoDTO. Para tu caso, está bien aquí.
    private FuncionDetallada funcion;

    // Lista de productos comprados en esta venta
    private List<ProductoSelectionDTO> productosComprados;

    // **CAMPO CRÍTICO:** Lista de los boletos individuales generados para esta compra
    private List<BoletoGeneradoDTO> boletosGenerados;

    // Constructor vacío (necesario para frameworks como JavaFX)
    public CompraDetalladaDTO() {
        this.boletosGenerados = new ArrayList<>(); // Inicializar para evitar NullPointerException
        this.productosComprados = new ArrayList<>(); // También inicializar
    }

    // Constructor más completo (puedes ajustar este según tus necesidades)
    public CompraDetalladaDTO(int idVenta, LocalDateTime fechaCompra, Integer idCliente, String nombreCliente,
                              BigDecimal totalVenta, String metodoPago, String estadoVenta, boolean facturado,
                              Integer idPromocion, String codigoPromocion, String nombrePromocion,
                              FuncionDetallada funcion, List<ProductoSelectionDTO> productosComprados,
                              List<BoletoGeneradoDTO> boletosGenerados) {
        this.idVenta = idVenta;
        this.fechaCompra = fechaCompra;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.totalVenta = totalVenta;
        this.metodoPago = metodoPago;
        this.estadoVenta = estadoVenta;
        this.facturado = facturado;
        this.idPromocion = idPromocion;
        this.codigoPromocion = codigoPromocion;
        this.nombrePromocion = nombrePromocion;
        this.funcion = funcion;
        this.productosComprados = productosComprados;
        this.boletosGenerados = boletosGenerados; // Se asigna la lista directamente
        if (this.productosComprados == null) this.productosComprados = new ArrayList<>(); // Asegurar que no sea null
        if (this.boletosGenerados == null) this.boletosGenerados = new ArrayList<>(); // Asegurar que no sea null
    }

    // Getters
    public int getIdVenta() {
        return idVenta;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public String getEstadoVenta() {
        return estadoVenta;
    }

    public boolean isFacturado() {
        return facturado;
    }

    public Integer getIdPromocion() {
        return idPromocion;
    }

    public String getCodigoPromocion() {
        return codigoPromocion;
    }

    public String getNombrePromocion() {
        return nombrePromocion;
    }

    public FuncionDetallada getFuncion() {
        return funcion;
    }

    public List<ProductoSelectionDTO> getProductosComprados() {
        return productosComprados;
    }

    public List<BoletoGeneradoDTO> getBoletosGenerados() {
        return boletosGenerados;
    }

    // Setters
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public void setEstadoVenta(String estadoVenta) {
        this.estadoVenta = estadoVenta;
    }

    public void setFacturado(boolean facturado) {
        this.facturado = facturado;
    }

    public void setIdPromocion(Integer idPromocion) {
        this.idPromocion = idPromocion;
    }

    public void setCodigoPromocion(String codigoPromocion) {
        this.codigoPromocion = codigoPromocion;
    }

    public void setNombrePromocion(String nombrePromocion) {
        this.nombrePromocion = nombrePromocion;
    }

    public void setFuncion(FuncionDetallada funcion) {
        this.funcion = funcion;
    }

    public void setProductosComprados(List<ProductoSelectionDTO> productosComprados) {
        this.productosComprados = productosComprados;
    }

    public void setBoletosGenerados(List<BoletoGeneradoDTO> boletosGenerados) {
        this.boletosGenerados = boletosGenerados;
    }
}