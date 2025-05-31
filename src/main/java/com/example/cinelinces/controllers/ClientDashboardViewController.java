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

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label registroLabel;
    @FXML
    private VBox purchasesContainer;

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

    public void setCompras(List<CompraDetalladaDTO> compras) {
        purchasesContainer.getChildren().clear();

        if (compras == null || compras.isEmpty()) {
            Label noPurchasesLabel = new Label("Aún no has realizado ninguna compra.");
            noPurchasesLabel.getStyleClass().add("no-purchases-label");
            purchasesContainer.getChildren().add(noPurchasesLabel);
            return;
        }

        Map<LocalDateTime, List<CompraDetalladaDTO>> agrupadoPorFecha = compras.stream()
                .collect(Collectors.groupingBy(CompraDetalladaDTO::getFechaCompra));

        for (List<CompraDetalladaDTO> grupo : agrupadoPorFecha.values()) {
            CompraDetalladaDTO base = grupo.get(0);
            CompraDetalladaDTO combinado = new CompraDetalladaDTO();
            combinado.setFuncion(base.getFuncion());
            combinado.setFechaCompra(base.getFechaCompra());
            combinado.setMetodoPago(base.getMetodoPago());
            combinado.setEstadoVenta(base.getEstadoVenta());
            combinado.setCodigoQR(base.getCodigoQR());


            String asientosConcat = grupo.stream()
                    .map(CompraDetalladaDTO::getIdAsiento)
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(", "));
            combinado.setIdAsiento(asientosConcat.isEmpty() ? "N/A" : asientosConcat);
            String boletosConcat = grupo.stream()
                    .map(CompraDetalladaDTO::getIdBoleto)
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(", "));
            combinado.setIdBoleto(boletosConcat.isEmpty() ? "N/A" : boletosConcat);
            BigDecimal totalReal = base.getTotalVenta() != null
                    ? base.getTotalVenta()
                    : BigDecimal.ZERO;
            combinado.setPrecioFinal(totalReal);
            combinado.setProductosComprados(base.getProductosComprados());
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
