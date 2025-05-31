package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.DAO.impl.CompraDAOImpl;
import com.example.cinelinces.DAO.impl.PromocionDAOImpl;
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.PromocionDTO;
import com.example.cinelinces.utils.SummaryContext;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador de purchase-summary-view.fxml.
 * Muestra listado de asientos (boletos), productos, promoción y totales.
 */
public class PurchaseSummaryViewController {

    // =============================================================
    //  1) GridPane para desplegar una fila por cada boleto (asiento)
    //     Columnas: [ "Película | FechaHora" ] [ "Sala X" ] [ "Asiento" ] [ "Precio" ]
    // =============================================================
    @FXML private GridPane boletosBox;

    // =============================================================
    //  2) GridPane para desplegar fila por cada producto seleccionado
    //     Columnas: [ "NombreProd — $PrecioUnitario" ] [ "Cantidad" ] [ "Subtotal" ]
    // =============================================================
    @FXML private GridPane productosBox;

    // =============================================================
    //  3) GridPane para la promoción (se rellena solo si hay promoción)
    //     Columnas: [“Promoción aplicada:”] [ “CÓDIGO (Nombre)” ] [ “%-Desc” ]
    // =============================================================
    @FXML private GridPane promoBox;

    // Labels para subtotales, descuento y total final
    @FXML private Label subtotalBoletosLabel;
    @FXML private Label subtotalProductosLabel;
    @FXML private Label descuentoLabel;
    @FXML private Label totalPagarLabel;

    // Botones para “Cancelar” y “Confirmar pago”
    @FXML private Button btnCancelSummary;
    @FXML private Button btnConfirmSummary;

    private final SummaryContext ctx = SummaryContext.getInstance();
    private final PromocionDAO promocionDAO = new PromocionDAOImpl();
    private final CompraDAO compraDAO = new CompraDAOImpl();

    /**
     * initialize() se ejecuta automáticamente después de cargar el FXML.
     * Rellena las tres secciones (boletos, productos y promoción + totales).
     */
    @FXML
    public void initialize() {
        // ------------------------------
        // 1) Listar boletos (asientos) seleccionados
        // ------------------------------
        List<AsientoDTO> asientos = ctx.getSelectedSeats();
        // La información de la película/función está en ctx.getSelectedFunction()
        FuncionDetallada funcion = ctx.getSelectedFunction();

        BigDecimal subtotalBoletos = BigDecimal.ZERO;
        int rowA = 0;

        for (AsientoDTO asiento : asientos) {
            // a) “Película — FechaHora”
            String textoPeli = funcion.getTituloPelicula()
                    + " | "
                    + funcion.getFechaHoraFuncion().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            );
            Label lblPelicula = new Label(textoPeli);

            // b) “Sala X”
            Label lblSala = new Label("Sala " + funcion.getNumeroSala());

            // c) “Asiento” (p. ej. “C10”)
            Label lblAsiento = new Label(asiento.getFila() + asiento.getNumero());

            // d) Precio unitario de este boleto (se toma de la función)
            BigDecimal precioBoleto = funcion.getPrecioBoleto();
            Label lblPrecio = new Label("$" + precioBoleto);

            // Insertar en la grilla boletosBox, columnas 0..3
            boletosBox.add(lblPelicula, 0, rowA);
            boletosBox.add(lblSala,     1, rowA);
            boletosBox.add(lblAsiento,  2, rowA);
            boletosBox.add(lblPrecio,   3, rowA);

            subtotalBoletos = subtotalBoletos.add(precioBoleto);
            rowA++;
        }

        // Mostrar subtotal boletos
        subtotalBoletosLabel.setText("$" + subtotalBoletos);

        // ------------------------------
        // 2) Listar productos seleccionados
        // ------------------------------
        List<ProductoSelectionDTO> prods = ctx.getSelectedProducts();

        BigDecimal subtotalProds = BigDecimal.ZERO;
        int rowP = 0;

        for (ProductoSelectionDTO p : prods) {
            // a) “NombreProd — $PrecioUnitario”
            Label lblProd = new Label(p.getNombre() + " — $" + p.getPrecioUnitario());
            // b) “Cantidad”
            Label lblCant = new Label(String.valueOf(p.getCantidad()));
            // c) “Subtotal”
            Label lblSubt = new Label("$" + p.getSubtotal());

            productosBox.add(lblProd, 0, rowP);
            productosBox.add(lblCant, 1, rowP);
            productosBox.add(lblSubt, 2, rowP);

            subtotalProds = subtotalProds.add(p.getSubtotal());
            rowP++;
        }

        // Mostrar subtotal productos
        subtotalProductosLabel.setText("$" + subtotalProds);

        // ------------------------------
        // 3) Buscar promoción activa y calcular descuento
        // ------------------------------
        LocalDate hoy = LocalDate.now();
        List<PromocionDTO> promos = promocionDAO.findActiveByDate(hoy);

        BigDecimal descuentoTotal = BigDecimal.ZERO;
        if (!promos.isEmpty()) {
            // Aplicamos únicamente la primera promoción activa
            PromocionDTO promo = promos.get(0);
            BigDecimal porcentaje = promo.getDescuento(); // ej. 0.10 para 10%
            BigDecimal base = subtotalBoletos.add(subtotalProds);
            descuentoTotal = base.multiply(porcentaje);

            // Mostrar información de la promoción
            Label lblTituloPromo = new Label("Promoción aplicada:");
            Label lblCodigo     = new Label(promo.getCodigo() + " (" + promo.getNombre() + ")");
            int pctEntero = porcentaje.multiply(new BigDecimal("100")).intValue();
            Label lblPct       = new Label("-" + pctEntero + "%");

            promoBox.add(lblTituloPromo, 0, 0);
            promoBox.add(lblCodigo,      1, 0);
            promoBox.add(lblPct,         2, 0);
        }

        // Mostrar descuento total
        descuentoLabel.setText("-$" + descuentoTotal);

        // Calcular total a pagar
        BigDecimal totalPagar = subtotalBoletos.add(subtotalProds).subtract(descuentoTotal);
        totalPagarLabel.setText("$" + totalPagar);
    }

    /** Cierra la ventana de resumen sin guardar nada. */
    @FXML
    private void handleCancelSummary() {
        Stage st = (Stage) btnCancelSummary.getScene().getWindow();
        st.close();
    }

    /** Confirma el pago, guarda en BD y cierra la ventana. */
    @FXML
    private void handleConfirmSummary() {
        compraDAO.saveFromSummary(ctx);
        Stage st = (Stage) btnConfirmSummary.getScene().getWindow();
        st.close();
    }
}
