// PurchaseSummaryViewController.java
package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.DAO.impl.CompraDAOImpl;
import com.example.cinelinces.DAO.impl.PromocionDAOImpl;
import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.PromocionDTO;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.utils.SessionManager;
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
    private BigDecimal descuentoAplicadoManualmente = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        List<AsientoDTO> asientos = ctx.getSelectedSeats();
        FuncionDetallada funcion = ctx.getSelectedFunction();
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

        promoBox.getChildren().clear();
        descuentoLabel.setText("-$" + BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

        BigDecimal totalPagar = subtotalBoletos.add(subtotalProds);
        totalPagarLabel.setText("$" + totalPagar.setScale(2, RoundingMode.HALF_UP));

        paymentMethodCombo.getItems().clear();
        paymentMethodCombo.getItems().addAll(
                "Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito", "PayPal"
        );
        paymentMethodCombo.setValue("Efectivo");
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
            return;
        }

        PromocionDTO promo = optPromo.get();
        promoAplicadaManualmente = promo.getCodigo();

        BigDecimal subtotalBoletos = parseCurrencyLabel(subtotalBoletosLabel.getText());
        BigDecimal subtotalProds = parseCurrencyLabel(subtotalProductosLabel.getText());
        BigDecimal porcentaje = promo.getDescuento();
        BigDecimal base = subtotalBoletos.add(subtotalProds);

        descuentoAplicadoManualmente = base.multiply(porcentaje);
        descuentoLabel.setText("-$" + descuentoAplicadoManualmente.setScale(2, RoundingMode.HALF_UP));

        if (promoBox != null) {
            promoBox.getChildren().clear();
            Label lblTituloPromo = new Label("Promoción aplicada:");
            Label lblCodigo = new Label(promo.getCodigo() + " (" + promo.getNombre() + ")");
            int pctEntero = porcentaje.multiply(new BigDecimal("100")).intValue();
            Label lblPct = new Label("-" + pctEntero + "%");
            promoBox.add(lblTituloPromo, 0, 0);
            promoBox.add(lblCodigo, 1, 0);
            promoBox.add(lblPct, 2, 0);
        }

        BigDecimal totalFinal = base.subtract(descuentoAplicadoManualmente);
        totalPagarLabel.setText("$" + totalFinal.setScale(2, RoundingMode.HALF_UP));
    }

    @FXML
    private void handleConfirmSummary() throws NoSuchMethodException {
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

        boolean isLoggedIn = SessionManager.getInstance().isLoggedIn();
        CompraDetalladaDTO compraParaMostrar = null;

        if (isLoggedIn) {
            try {
                compraDAO.saveFromSummary(ctx);

                Cliente currentClient = SessionManager.getInstance().getCurrentCliente();
                if (currentClient == null) {
                    showAlert(Alert.AlertType.ERROR, "Error de Sesión", "No se pudo identificar al cliente para mostrar la compra.");
                    return;
                }
                List<CompraDetalladaDTO> comprasGuardadas = compraDAO.findComprasByClienteId(currentClient.getIdCliente());

                if (comprasGuardadas != null && !comprasGuardadas.isEmpty()) {
                    compraParaMostrar = comprasGuardadas.get(0);

                    BigDecimal totalCorrecto = parseCurrencyLabel(totalPagarLabel.getText());
                    compraParaMostrar.setPrecioFinal(totalCorrecto);

                    List<ProductoSelectionDTO> productosSeleccionados = ctx.getSelectedProducts();
                    compraParaMostrar.setProductosComprados(productosSeleccionados);

                    ctx.setUltimaCompraDetallada(compraParaMostrar);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron recuperar los detalles de la compra después de guardarla.");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error al Procesar Compra", "Ocurrió un error: " + e.getMessage());
                return;
            }

        } else {
            try {
                compraDAO.saveFromSummary(ctx);
                CompraDetalladaDTO dtoInvitado = ((CompraDAOImpl) compraDAO).findLastCompraGuest();
                if (dtoInvitado == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron recuperar los detalles de la compra (invitado).");
                    return;
                }
                compraParaMostrar = dtoInvitado;
                BigDecimal totalCorrecto = parseCurrencyLabel(totalPagarLabel.getText());
                compraParaMostrar.setPrecioFinal(totalCorrecto);
                compraParaMostrar.setProductosComprados(ctx.getSelectedProducts());

                ctx.setUltimaCompraDetallada(compraParaMostrar);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error al Procesar Compra", "No se pudo guardar la compra como invitado: " + e.getMessage());
                return;
            }
        }
        if (compraParaMostrar != null) {
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
                    cardCtrl.setData(compraParaMostrar);
                }
                dialog.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error de Interfaz", "No se pudo mostrar la tarjeta de compra: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error al preparar los datos de la compra: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Información", "No hay detalles de compra para mostrar.");
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

    private void showAlert(Alert.AlertType type, String titulo, String mensaje) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
