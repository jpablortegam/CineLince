package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.DAO.impl.CompraDAOImpl;
import com.example.cinelinces.DAO.impl.PromocionDAOImpl;
// import com.example.cinelinces.model.Cliente; // No directamente necesario aquí, lo maneja SessionManager
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.PromocionDTO;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
// import com.example.cinelinces.utils.SessionManager; // No directamente necesario, si no lo usas en el controlador
import com.example.cinelinces.utils.SummaryContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PurchaseSummaryViewController {

    @FXML
    private GridPane boletosBox;
    @FXML
    private GridPane productosBox;
    @FXML
    private GridPane promoBox;
    @FXML
    private Label subtotalBoletosLabel;
    @FXML
    private Label subtotalProductosLabel;
    @FXML
    private Label descuentoLabel;
    @FXML
    private Label totalPagarLabel;
    @FXML
    private Button btnCancelSummary;
    @FXML
    private Button btnConfirmSummary;
    @FXML
    private TextField promoCodeField;
    @FXML
    private Button applyPromoButton;
    @FXML
    private ComboBox<String> paymentMethodCombo;

    private final SummaryContext ctx = SummaryContext.getInstance();
    private final PromocionDAO promocionDAO = new PromocionDAOImpl();
    private final CompraDAO compraDAO = new CompraDAOImpl();

    private String promoAplicadaManualmente = null;

    @FXML
    public void initialize() {
        renderSummary(); // Llamamos a un método para inicializar el resumen
    }

    private void renderSummary() {
        FuncionDetallada funcion = ctx.getSelectedFunction();
        List<AsientoDTO> asientos = ctx.getSelectedSeats();
        BigDecimal subtotalBoletos = BigDecimal.ZERO;

        if (boletosBox != null) boletosBox.getChildren().clear();

        if (asientos != null && funcion != null && funcion.getPrecioBoleto() != null) {
            int rowA = 0;
            for (AsientoDTO asiento : asientos) {
                String textoPeli = funcion.getTituloPelicula()
                        + " | "
                        + funcion.getFechaHoraFuncion().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                );
                Label lblPelicula = new Label(textoPeli);
                Label lblSala = new Label("Sala " + funcion.getNumeroSala());
                Label lblAsiento = new Label(asiento.getFila() + asiento.getNumero());
                BigDecimal precioBoleto = funcion.getPrecioBoleto();
                Label lblPrecio = new Label("$" + precioBoleto.setScale(2, RoundingMode.HALF_UP));

                boletosBox.add(lblPelicula, 0, rowA);
                boletosBox.add(lblSala, 1, rowA);
                boletosBox.add(lblAsiento, 2, rowA);
                boletosBox.add(lblPrecio, 3, rowA);

                subtotalBoletos = subtotalBoletos.add(precioBoleto);
                rowA++;
            }
        }
        subtotalBoletosLabel.setText("$" + subtotalBoletos.setScale(2, RoundingMode.HALF_UP));

        List<ProductoSelectionDTO> prods = ctx.getSelectedProducts();
        BigDecimal subtotalProds = BigDecimal.ZERO;

        if (productosBox != null) productosBox.getChildren().clear();
        if (prods != null) {
            int rowP = 0;
            for (ProductoSelectionDTO p : prods) {
                Label lblProd = new Label(p.getNombre() + " — $" + p.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP));
                Label lblCant = new Label(String.valueOf(p.getCantidad()));
                Label lblSubt = new Label("$" + p.getSubtotal().setScale(2, RoundingMode.HALF_UP));

                productosBox.add(lblProd, 0, rowP);
                productosBox.add(lblCant, 1, rowP);
                productosBox.add(lblSubt, 2, rowP);

                subtotalProds = subtotalProds.add(p.getSubtotal());
                rowP++;
            }
        }
        subtotalProductosLabel.setText("$" + subtotalProds.setScale(2, RoundingMode.HALF_UP));

        // Calcular y mostrar promoción y total
        applyCurrentPromoAndCalculateTotal();

        if (paymentMethodCombo.getItems().isEmpty()) { // Solo carga una vez
            paymentMethodCombo.getItems().addAll(
                    "Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito", "PayPal"
            );
            paymentMethodCombo.setValue("Efectivo");
        }
    }

    private void applyCurrentPromoAndCalculateTotal() {
        promoBox.getChildren().clear();
        descuentoLabel.setText("-$" + BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

        BigDecimal subtotalBoletos = parseCurrencyLabel(subtotalBoletosLabel.getText());
        BigDecimal subtotalProds = parseCurrencyLabel(subtotalProductosLabel.getText());
        BigDecimal baseTotal = subtotalBoletos.add(subtotalProds);

        BigDecimal currentDiscountAmount = BigDecimal.ZERO; // Cantidad de descuento actual

        String promoCode = promoAplicadaManualmente != null ? promoAplicadaManualmente : promoCodeField.getText().trim();

        if (promoCode != null && !promoCode.isEmpty()) {
            LocalDate hoy = LocalDate.now();
            Optional<PromocionDTO> optPromo = promocionDAO.findActiveByCodigoAndDate(promoCode, hoy);
            if (optPromo.isPresent()) {
                PromocionDTO promo = optPromo.get();
                currentDiscountAmount = baseTotal.multiply(promo.getDescuento());
                descuentoLabel.setText("-$" + currentDiscountAmount.setScale(2, RoundingMode.HALF_UP));

                Label lblTituloPromo = new Label("Promoción aplicada:");
                Label lblCodigo = new Label(promo.getCodigo() + " (" + promo.getNombre() + ")");
                int pctEntero = promo.getDescuento().multiply(new BigDecimal("100")).intValue();
                Label lblPct = new Label("-" + pctEntero + "%");
                promoBox.add(lblTituloPromo, 0, 0);
                promoBox.add(lblCodigo, 1, 0);
                promoBox.add(lblPct, 2, 0);
            }
        }

        BigDecimal totalFinal = baseTotal.subtract(currentDiscountAmount);
        totalPagarLabel.setText("$" + totalFinal.setScale(2, RoundingMode.HALF_UP));

        // **ACTUALIZAR EL CONTEXTO CON EL TOTAL CALCULADO**
        ctx.setTotalVentaCalculado(totalFinal);
    }


    @FXML
    private void handleApplyPromo() {
        String codigoIngresado = promoCodeField.getText().trim();
        if (codigoIngresado.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Código vacío", "Por favor ingresa un código de promoción.");
            return;
        }

        LocalDate hoy = LocalDate.now();
        Optional<PromocionDTO> optPromo = promocionDAO.findActiveByCodigoAndDate(codigoIngresado, hoy);
        if (optPromo.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Promoción no válida", "El código ingresado no corresponde a ninguna promoción activa o vigente para la fecha actual.");
            // Resetear promo si no es válida
            promoAplicadaManualmente = null;
            applyCurrentPromoAndCalculateTotal(); // Recalcular sin promo
            return;
        }

        PromocionDTO promo = optPromo.get();
        promoAplicadaManualmente = promo.getCodigo(); // Guardar el código aplicado
        applyCurrentPromoAndCalculateTotal(); // Recalcular con la nueva promo
    }

    private void showAlert(Alert.AlertType alertType, String códigoVacío, String s) {
    }

    @FXML
    private void handleConfirmSummary() {
        String metodoPagoSeleccionado = paymentMethodCombo.getValue();
        if (metodoPagoSeleccionado == null || metodoPagoSeleccionado.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Método de pago", "Debes seleccionar un método de pago.");
            return;
        }
        ctx.setMetodoPago(metodoPagoSeleccionado);

        if (promoAplicadaManualmente != null) {
            ctx.setCodigoPromocion(promoAplicadaManualmente);
        } else {
            ctx.setCodigoPromocion(null);
        }

        try {
            // Llama a saveFromSummary, que ahora devuelve la DTO de la compra completa
            CompraDetalladaDTO compraGenerada = compraDAO.saveFromSummary(ctx);

            if (compraGenerada == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron guardar los detalles de la compra.");
                return;
            }

            // Actualiza el SummaryContext con la compra generada (contiene los boletos individuales)
            ctx.setUltimaCompraDetallada(compraGenerada);

            // Abre la tarjeta de compra
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/purchase-card-view.fxml"));
                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("Detalles de tu compra");
                dialog.setScene(new Scene(loader.load()));
                dialog.setResizable(false);

                Object controller = loader.getController();
                if (controller instanceof com.example.cinelinces.controllers.PurchaseCardController) {
                    com.example.cinelinces.controllers.PurchaseCardController cardCtrl =
                            (com.example.cinelinces.controllers.PurchaseCardController) controller;
                    cardCtrl.setData(compraGenerada); // Pasamos la compra completa y precisa
                }
                dialog.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error de Interfaz", "No se pudo mostrar la tarjeta de compra: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error al Procesar Compra", "Ocurrió un error inesperado: " + e.getMessage());
        }

        // Cerrar diálogo de resumen
        Stage st = (Stage) btnConfirmSummary.getScene().getWindow();
        st.close();
    }

    @FXML
    private void handleCancelSummary() {
        Stage st = (Stage) btnCancelSummary.getScene().getWindow();
        st.close();
    }

    private BigDecimal parseCurrencyLabel(String text) {
        if (text == null || text.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            String parsableText = text;
            if (text.startsWith("$")) {
                parsableText = text.substring(1);
            }
            return new BigDecimal(parsableText.trim());
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear valor monetario: '" + text + "' - " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}