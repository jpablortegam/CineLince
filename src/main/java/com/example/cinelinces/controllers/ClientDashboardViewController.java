package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Cliente;
import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientDashboardViewController {

    @FXML private Label welcomeLabel;
    @FXML private Label emailLabel;
    @FXML private Label registroLabel;
    @FXML private VBox purchasesContainer; // VBox dentro del ScrollPane

    private MainViewController mainController;
    private Cliente cliente;

    public void setMainViewController(MainViewController m) {
        this.mainController = m;
    }

    public void setClienteData(Cliente c) {
        this.cliente = c;
        welcomeLabel.setText("¡Bienvenido, " + c.getNombre() + " " + c.getApellido() + "!");
        emailLabel.setText("Email: " + c.getEmail());
        String fmt = "dd/MM/yyyy 'a las' HH:mm";
        registroLabel.setText("Registrado: " + c.getFechaRegistro().format(DateTimeFormatter.ofPattern(fmt)));
    }

    /**
     * Este método reemplaza la implementación original. Ahora:
     *  1) Agrupa todos los CompraDetalladaDTO por la misma venta (en este caso, por fecha de compra).
     *     Si prefieres agrupar por un campo distinto (por ejemplo, idVenta), basta con cambiar el groupingBy.
     *
     *  2) Por cada grupo (venta), crea un único DTO "combinado":
     *     - Concatena todos los idAsiento (p.ej. "A1, B2, C3").
     *     - Concatena todos los idBoleto (p.ej. "227, 226").
     *     - Toma el total real de la venta desde base.getTotalVenta() (columna Total de la tabla Venta).
     *     - Copia la lista de productos (productosComprados) que previamente llenó el DAO.
     *
     *  3) Carga UNA sola tarjeta ("purchase-card-view.fxml") con ese DTO combinado.
     *     De este modo no aparecen múltiples tarjetas por asiento, sino una sola por venta.
     */
    public void setCompras(List<CompraDetalladaDTO> compras) {
        purchasesContainer.getChildren().clear();

        if (compras == null || compras.isEmpty()) {
            Label noPurchasesLabel = new Label("Aún no has realizado ninguna compra.");
            noPurchasesLabel.getStyleClass().add("no-purchases-label");
            purchasesContainer.getChildren().add(noPurchasesLabel);
            return;
        }

        // 1) Agrupar por fecha de compra. Si prefieres usar idVenta, reemplaza por groupingBy(CompraDetalladaDTO::getIdVenta)
        Map<LocalDateTime, List<CompraDetalladaDTO>> agrupadoPorFecha = compras.stream()
                .collect(Collectors.groupingBy(CompraDetalladaDTO::getFechaCompra));

        // 2) Para cada grupo, crear un DTO combinado
        for (List<CompraDetalladaDTO> grupo : agrupadoPorFecha.values()) {
            // Tomar el primer elemento únicamente para copiar campos que son comunes (función, método, estado, QR, productos)
            CompraDetalladaDTO base = grupo.get(0);
            CompraDetalladaDTO combinado = new CompraDetalladaDTO();

            // 2.1) Copiar datos de la función y la venta
            combinado.setFuncion(base.getFuncion());
            combinado.setFechaCompra(base.getFechaCompra());
            combinado.setMetodoPago(base.getMetodoPago());
            combinado.setEstadoVenta(base.getEstadoVenta());
            combinado.setCodigoQR(base.getCodigoQR());

            // 2.2) Concatenar todos los idAsiento de ese grupo ("A1, B2, C3, ...")
            String asientosConcat = grupo.stream()
                    .map(CompraDetalladaDTO::getIdAsiento)
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(", "));
            combinado.setIdAsiento(asientosConcat.isEmpty() ? "N/A" : asientosConcat);

            // 2.3) Concatenar todos los idBoleto de ese grupo ("227, 226, ...")
            String boletosConcat = grupo.stream()
                    .map(CompraDetalladaDTO::getIdBoleto)
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(", "));
            combinado.setIdBoleto(boletosConcat.isEmpty() ? "N/A" : boletosConcat);

            // 2.4) Toma el "total real" de la venta desde la columna Total (base.getTotalVenta())
            //      En el DTO, usamos precioFinal para mostrar ese total en la tarjeta.
            BigDecimal totalReal = base.getTotalVenta() != null
                    ? base.getTotalVenta()
                    : BigDecimal.ZERO;
            combinado.setPrecioFinal(totalReal);

            // 2.5) Copiar la lista de productos comprados (dulcería) que el DAO ya llenó en base.getProductosComprados()
            combinado.setProductosComprados(base.getProductosComprados());

            // 3) Cargar una sola tarjeta FXML con el DTO combinado
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/cinelinces/purchase-card-view.fxml")
                );
                Node purchaseCardNode = loader.load();

                PurchaseCardController cardController = loader.getController();
                cardController.setData(combinado);

                purchasesContainer.getChildren().add(purchaseCardNode);
            } catch (IOException e) {
                e.printStackTrace();
                Label errorLabel = new Label(
                        "Error al cargar detalle de la compra: " + base.getFuncion().getTituloPelicula()
                );
                purchasesContainer.getChildren().add(errorLabel);
            }
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        mainController.showAccount();
    }
}
