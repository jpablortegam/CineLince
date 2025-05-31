package com.example.cinelinces.controllers;

import com.example.cinelinces.DAO.CompraDAO;
import com.example.cinelinces.DAO.PromocionDAO;
import com.example.cinelinces.DAO.impl.CompraDAOImpl;
import com.example.cinelinces.DAO.impl.PromocionDAOImpl;
import com.example.cinelinces.model.DTO.AsientoDTO;
import com.example.cinelinces.model.DTO.FuncionDetallada;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import com.example.cinelinces.model.DTO.PromocionDTO;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controlador de purchase-summary-view.fxml.
 * Muestra listados de boletos, productos, promoción, totales,
 * permite aplicar un código manual y escoger método de pago.
 * Al confirmar, redirige a la cuenta si el usuario está logueado;
 * si no, muestra un modal con la “tarjeta de compra” (PurchaseCard).
 */
public class PurchaseSummaryViewController {

    // 1) GridPane para boletos
    @FXML private GridPane boletosBox;
    // 2) GridPane para productos
    @FXML private GridPane productosBox;
    // 3) GridPane para la info de la promoción aplicada
    @FXML private GridPane promoBox;

    // Labels de subtotales, descuento y total final
    @FXML private Label subtotalBoletosLabel;
    @FXML private Label subtotalProductosLabel;
    @FXML private Label descuentoLabel;
    @FXML private Label totalPagarLabel;

    // Botones para cancelar y confirmar
    @FXML private Button btnCancelSummary;
    @FXML private Button btnConfirmSummary;

    // Nuevo campo de texto para ingresar un código de promoción
    @FXML private TextField promoCodeField;
    // Botón que dispara la comprobación/aplicación del código manual
    @FXML private Button applyPromoButton;
    // Nuevo ComboBox para seleccionar el método de pago
    @FXML private ComboBox<String> paymentMethodCombo;

    private final SummaryContext ctx = SummaryContext.getInstance();
    private final PromocionDAO promocionDAO = new PromocionDAOImpl();
    private final CompraDAO compraDAO = new CompraDAOImpl();

    // Para guardar internamente si ya apliqué un código (para que no duplique promos)
    private String promoAplicadaManualmente = null;
    private BigDecimal descuentoAplicadoManualmente = BigDecimal.ZERO;

    /**
     * initialize() se ejecuta automáticamente después de cargar el FXML.
     * Rellena las tres secciones (boletos, productos y promoción + totales).
     */
    @FXML
    public void initialize() {
        // 1) Listar boletos (asientos) seleccionados
        List<AsientoDTO> asientos = ctx.getSelectedSeats();
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

            // d) Precio unitario de boleto (desde la función)
            BigDecimal precioBoleto = funcion.getPrecioBoleto();
            Label lblPrecio = new Label("$" + precioBoleto);

            boletosBox.add(lblPelicula, 0, rowA);
            boletosBox.add(lblSala,     1, rowA);
            boletosBox.add(lblAsiento,  2, rowA);
            boletosBox.add(lblPrecio,   3, rowA);

            subtotalBoletos = subtotalBoletos.add(precioBoleto);
            rowA++;
        }
        subtotalBoletosLabel.setText("$" + subtotalBoletos);

        // 2) Listar productos seleccionados
        List<ProductoSelectionDTO> prods = ctx.getSelectedProducts();

        BigDecimal subtotalProds = BigDecimal.ZERO;
        int rowP = 0;
        for (ProductoSelectionDTO p : prods) {
            Label lblProd = new Label(p.getNombre() + " — $" + p.getPrecioUnitario());
            Label lblCant = new Label(String.valueOf(p.getCantidad()));
            Label lblSubt = new Label("$" + p.getSubtotal());

            productosBox.add(lblProd, 0, rowP);
            productosBox.add(lblCant, 1, rowP);
            productosBox.add(lblSubt, 2, rowP);

            subtotalProds = subtotalProds.add(p.getSubtotal());
            rowP++;
        }
        subtotalProductosLabel.setText("$" + subtotalProds);

        // 3) Buscar promoción activa automáticamente (si existe)
        LocalDate hoy = LocalDate.now();
        List<PromocionDTO> promos = promocionDAO.findActiveByDate(hoy);

        BigDecimal descuentoTotal = BigDecimal.ZERO;
        if (!promos.isEmpty()) {
            // Aplica solo la PRIMERA promoción activa
            PromocionDTO promo = promos.get(0);
            BigDecimal porcentaje = promo.getDescuento(); // ej. 0.10 = 10%
            BigDecimal base = subtotalBoletos.add(subtotalProds);
            descuentoTotal = base.multiply(porcentaje);

            Label lblTituloPromo = new Label("Promoción aplicada:");
            Label lblCodigo     = new Label(promo.getCodigo() + " (" + promo.getNombre() + ")");
            int pctEntero = porcentaje.multiply(new BigDecimal("100")).intValue();
            Label lblPct       = new Label("-" + pctEntero + "%");

            promoBox.add(lblTituloPromo, 0, 0);
            promoBox.add(lblCodigo,      1, 0);
            promoBox.add(lblPct,         2, 0);
        }
        descuentoLabel.setText("-$" + descuentoTotal);

        // 4) Mostrar total provisional (antes de que el usuario ingrese manualmente promoción
        //    o cambie el método de pago). Más adelante se recalculará si aplica código manual.
        BigDecimal totalPagar = subtotalBoletos.add(subtotalProds).subtract(descuentoTotal);
        totalPagarLabel.setText("$" + totalPagar);

        // 5) Inicializar el ComboBox de métodos de pago
        paymentMethodCombo.getItems().clear();
        paymentMethodCombo.getItems().addAll(
                "Efectivo",
                "Tarjeta de Crédito",
                "Tarjeta de Débito",
                "PayPal"
        );
        paymentMethodCombo.setValue("Efectivo"); // Opción por defecto
    }

    /**
     * Método invocado cuando el usuario hace clic en “Aplicar” código de promoción.
     * Se buscará si existe una promoción con ese código (independientemente de la fecha),
     * y si es válida, se recalcula el descuento y el total. Si no existe o no es válida,
     * se muestra un diálogo de error.
     */
    @FXML
    private void handleApplyPromo() {
        String codigoIngresado = promoCodeField.getText().trim();
        if (codigoIngresado.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Código vacío", "Por favor ingresa un código de promoción.");
            return;
        }

        // Suponemos que nuestro DAO de promociones puede buscar por código exacto:
        Optional<PromocionDTO> optPromo = promocionDAO.findAllActivePromos() // hipotético: retorna TODAS las promos activas
                .stream()
                .filter(p -> p.getCodigo().equalsIgnoreCase(codigoIngresado))
                .findFirst();

        if (optPromo.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Promoción no válida", "El código ingresado no corresponde a ninguna promoción activa.");
            return;
        }

        // Si llegó aquí, la promo existe y está activa:
        PromocionDTO promo = optPromo.get();
        promoAplicadaManualmente = promo.getCodigo();

        // Recalculamos el descuento en pantalla, ignorando la promo automática anterior:
        BigDecimal subtotalBoletos = parseCurrencyLabel(subtotalBoletosLabel.getText());
        BigDecimal subtotalProds   = parseCurrencyLabel(subtotalProductosLabel.getText());
        BigDecimal porcentaje      = promo.getDescuento();
        BigDecimal base            = subtotalBoletos.add(subtotalProds);

        // Aplicamos el descuento manual
        descuentoAplicadoManualmente = base.multiply(porcentaje);
        descuentoLabel.setText("-$" + descuentoAplicadoManualmente);

        // Vaciamos el GridPane de promoBox y ponemos solo ésta:
        promoBox.getChildren().clear();
        Label lblTituloPromo = new Label("Promoción aplicada:");
        Label lblCodigo     = new Label(promo.getCodigo() + " (" + promo.getNombre() + ")");
        int pctEntero       = porcentaje.multiply(new BigDecimal("100")).intValue();
        Label lblPct        = new Label("-" + pctEntero + "%");
        promoBox.add(lblTituloPromo, 0, 0);
        promoBox.add(lblCodigo,      1, 0);
        promoBox.add(lblPct,         2, 0);

        // Volver a calcular y mostrar el total final
        BigDecimal totalFinal = base.subtract(descuentoAplicadoManualmente);
        totalPagarLabel.setText("$" + totalFinal);
    }

    /**
     * Al dar clic en “Confirmar pago”:
     *  - Verificamos si el usuario está logueado (SessionManager.isLoggedIn()).
     *    * Si está logueado → redirigimos a la vista de “Mi cuenta” (por ejemplo, cargar otro FXML).
     *    * Si NO está logueado → abrimos un modal con PurchaseCardController para mostrar los detalles + QR.
     */
    @FXML
    private void handleConfirmSummary() {
        // 1) Tomar método de pago seleccionado
        String metodoPagoSeleccionado = paymentMethodCombo.getValue();
        if (metodoPagoSeleccionado == null || metodoPagoSeleccionado.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Método de pago", "Debes seleccionar un método de pago.");
            return;
        }
        ctx.setMetodoPago(metodoPagoSeleccionado);

        // 2) Tomar código de promo (si aplicó), se guardó en promoAplicadaManualmente
        if (promoAplicadaManualmente != null) {
            ctx.setCodigoPromocion(promoAplicadaManualmente);
        }

        // 3) Llamar a DAO para guardar en BD (venta, boletos, detalle venta, etc.)
        compraDAO.saveFromSummary(ctx);

        // 4) Revisar si el usuario está logueado
        boolean isLoggedIn = SessionManager.getInstance().isLoggedIn();

        if (isLoggedIn) {
            // Si está logueado, redirijo a “MiCuentaView.fxml”
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/views/mi-cuenta-view.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Mi cuenta");
                stage.setScene(new Scene(loader.load()));
                stage.setResizable(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista de Mi cuenta.");
            }
        } else {
            // Si NO está logueado, abro modal con “PurchaseCard.fxml”
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/views/purchase-card-view.fxml"));
                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("Detalles de tu compra");
                dialog.setScene(new Scene(loader.load()));
                dialog.setResizable(false);

                // Antes de mostrar, le paso el DTO resultante al PurchaseCardController
                // El método saveFromSummary() ya habrá generado un CompraDetalladaDTO dentro de ctx
                // Supongamos que ctx.getUltimaCompraDetallada() retorna ese DTO recién guardado:
                Object controller = loader.getController();
                if (controller instanceof com.example.cinelinces.controllers.PurchaseCardController) {
                    com.example.cinelinces.controllers.PurchaseCardController cardCtrl =
                            (com.example.cinelinces.controllers.PurchaseCardController) controller;
                    cardCtrl.setData(ctx.getUltimaCompraDetallada());
                }

                dialog.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo mostrar la tarjeta de compra.");
            }
        }

        // 5) Por último, cierro esta ventana de “Resumen de compra”
        Stage st = (Stage) btnConfirmSummary.getScene().getWindow();
        st.close();
    }

    /** Cierra la ventana de resumen sin hacer nada más. */
    @FXML
    private void handleCancelSummary() {
        Stage st = (Stage) btnCancelSummary.getScene().getWindow();
        st.close();
    }

    /**
     * Método de utilidad para mostrar un Alert rápido.
     */
    private void showAlert(Alert.AlertType type, String titulo, String mensaje) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Convierte un string de etiqueta (“$123.45”) en BigDecimal, quitando el símbolo.
     */
    private BigDecimal parseCurrencyLabel(String text) {
        // Ejemplo: si text = "$150.00", quita el “$” y parsea
        try {
            if (text.startsWith("$")) {
                return new BigDecimal(text.substring(1));
            }
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
