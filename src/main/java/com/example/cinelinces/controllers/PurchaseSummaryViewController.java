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
import java.math.RoundingMode; // Asegúrate de tener esta importación
import java.time.LocalDate;    // Asegúrate de tener esta importación
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

    // Campo de texto para ingresar un código de promoción
    @FXML private TextField promoCodeField;
    // Botón que dispara la comprobación/aplicación del código manual
    @FXML private Button applyPromoButton; // Este ID debe coincidir con tu FXML si el botón se llama así
    // ComboBox para seleccionar el método de pago
    @FXML private ComboBox<String> paymentMethodCombo;

    private final SummaryContext ctx = SummaryContext.getInstance();
    private final PromocionDAO promocionDAO = new PromocionDAOImpl(); // Asegúrate que PromocionDAO y su Impl estén actualizados
    private final CompraDAO compraDAO = new CompraDAOImpl();

    // Para guardar internamente si ya apliqué un código manualmente
    private String promoAplicadaManualmente = null;
    private BigDecimal descuentoAplicadoManualmente = BigDecimal.ZERO;

    /**
     * initialize() se ejecuta automáticamente después de cargar el FXML.
     * Rellena las secciones de boletos, productos y totales.
     * Ya NO aplica promociones automáticamente.
     */
    @FXML
    public void initialize() {
        // 1) Listar boletos (asientos) seleccionados
        List<AsientoDTO> asientos = ctx.getSelectedSeats();
        FuncionDetallada funcion = ctx.getSelectedFunction();
        BigDecimal subtotalBoletos = BigDecimal.ZERO;

        if (asientos != null && funcion != null) {
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

        // 2) Listar productos seleccionados
        List<ProductoSelectionDTO> prods = ctx.getSelectedProducts();
        BigDecimal subtotalProds = BigDecimal.ZERO;
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

        // 3) Inicializar sección de promoción como vacía y descuento como cero
        if (promoBox != null) {
            promoBox.getChildren().clear(); // Limpia el área de detalles de promoción
        }
        BigDecimal descuentoInicial = BigDecimal.ZERO;
        descuentoLabel.setText("-$" + descuentoInicial.setScale(2, RoundingMode.HALF_UP));

        // 4) Mostrar total provisional (sin descuento inicial)
        BigDecimal totalPagar = subtotalBoletos.add(subtotalProds).subtract(descuentoInicial);
        totalPagarLabel.setText("$" + totalPagar.setScale(2, RoundingMode.HALF_UP));

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
     * Se buscará si existe una promoción con ese código, que esté activa y sea vigente para la fecha actual.
     * Si es válida, se recalcula el descuento y el total. Si no, se muestra un diálogo de error.
     */
    @FXML
    private void handleApplyPromo() {
        String codigoIngresado = promoCodeField.getText().trim();
        if (codigoIngresado.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Código vacío", "Por favor ingresa un código de promoción.");
            return;
        }

        LocalDate hoy = LocalDate.now();
        // Usar el método del DAO que busca por código, estado 'Activa' Y VALIDEZ DE FECHA
        // Asegúrate de que PromocionDAO y su implementación tengan el método findActiveByCodigoAndDate
        Optional<PromocionDTO> optPromo = promocionDAO.findActiveByCodigoAndDate(codigoIngresado, hoy);

        if (optPromo.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Promoción no válida", "El código ingresado no corresponde a ninguna promoción activa o vigente para la fecha actual.");
            return;
        }

        // Si llegó aquí, la promo existe, está activa y es vigente para hoy:
        PromocionDTO promo = optPromo.get();
        promoAplicadaManualmente = promo.getCodigo();

        BigDecimal subtotalBoletos = parseCurrencyLabel(subtotalBoletosLabel.getText());
        BigDecimal subtotalProds   = parseCurrencyLabel(subtotalProductosLabel.getText());
        BigDecimal porcentaje      = promo.getDescuento();
        BigDecimal base            = subtotalBoletos.add(subtotalProds);

        // Aplicamos el descuento manual
        descuentoAplicadoManualmente = base.multiply(porcentaje);
        descuentoLabel.setText("-$" + descuentoAplicadoManualmente.setScale(2, RoundingMode.HALF_UP));

        // Vaciamos el GridPane de promoBox y ponemos solo ésta:
        if (promoBox != null) {
            promoBox.getChildren().clear();
        }
        Label lblTituloPromo = new Label("Promoción aplicada:");
        Label lblCodigo     = new Label(promo.getCodigo() + " (" + promo.getNombre() + ")");
        int pctEntero       = porcentaje.multiply(new BigDecimal("100")).intValue();
        Label lblPct        = new Label("-" + pctEntero + "%");

        if (promoBox != null) {
            promoBox.add(lblTituloPromo, 0, 0);
            promoBox.add(lblCodigo,      1, 0);
            promoBox.add(lblPct,         2, 0);
        }

        // Volver a calcular y mostrar el total final
        BigDecimal totalFinal = base.subtract(descuentoAplicadoManualmente);
        totalPagarLabel.setText("$" + totalFinal.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Al dar clic en “Confirmar pago”:
     * - Verificamos si el usuario está logueado (SessionManager.isLoggedIn()).
     * * Si está logueado → redirigimos a la vista de “Mi cuenta”.
     * * Si NO está logueado → abrimos un modal con PurchaseCardController.
     */
    @FXML
    private void handleConfirmSummary() {
        String metodoPagoSeleccionado = paymentMethodCombo.getValue();
        if (metodoPagoSeleccionado == null || metodoPagoSeleccionado.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Método de pago", "Debes seleccionar un método de pago.");
            return;
        }
        ctx.setMetodoPago(metodoPagoSeleccionado);

        if (promoAplicadaManualmente != null) {
            // Si se aplicó una promoción manualmente, se usa ese código
            ctx.setCodigoPromocion(promoAplicadaManualmente);
        } else {
            // Si no se aplicó ninguna promoción manualmente, se limpia cualquier código previo en el contexto
            // Esto es opcional, depende de si quieres que el contexto recuerde una promo auto-aplicada
            // que ya no existe. Como ya no hay auto-aplicación, esto asegura que solo las manuales se guarden.
            ctx.setCodigoPromocion(null);
        }


        compraDAO.saveFromSummary(ctx);
        boolean isLoggedIn = SessionManager.getInstance().isLoggedIn();

        if (isLoggedIn) {
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
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/views/purchase-card-view.fxml"));
                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("Detalles de tu compra");
                dialog.setScene(new Scene(loader.load()));
                dialog.setResizable(false);

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
        try {
            String parsableText = text;
            if (text.startsWith("$")) {
                parsableText = text.substring(1);
            }
            return new BigDecimal(parsableText.trim()); // Añadido trim por si acaso
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear valor monetario: " + text + " - " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}