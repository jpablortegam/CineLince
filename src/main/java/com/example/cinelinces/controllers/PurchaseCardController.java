package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import com.example.cinelinces.model.DTO.ProductoSelectionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PurchaseCardController {

    @FXML
    private ImageView moviePosterImageView;
    @FXML
    private Label movieTitleLabel;
    @FXML
    private Label cinemaAndSalaLabel;
    @FXML
    private Label functionDateTimeLabel;
    @FXML
    private Label purchaseDateTimeLabel;
    @FXML
    private Label seatLabel;
    @FXML
    private Label ticketIdLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label paymentMethodLabel;
    @FXML
    private Label purchaseStatusLabel;
    @FXML
    private Label qrInfoLabel;
    @FXML
    private VBox productsVBox;
    @FXML
    private Label noProductsLabel;

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");
    private static final String PLACEHOLDER_IMAGE_PATH = "/com/example/images/placeholder_poster.png";
    private static final String DEFAULT_POSTER_RESOURCE_PREFIX = "/com/example/images/";

    public void setData(CompraDetalladaDTO compra) {
        if (compra == null) {
            movieTitleLabel.setText("Error: Datos de compra no disponibles.");
            return;
        }
        if (compra.getFuncion() != null) {
            movieTitleLabel.setText(compra.getFuncion().getTituloPelicula());
            loadPosterImage(compra.getFuncion().getFotografiaPelicula());
            cinemaAndSalaLabel.setText(
                    compra.getFuncion().getNombreCine() + " - Sala " + compra.getFuncion().getNumeroSala()
            );
            functionDateTimeLabel.setText(
                    "Función: " + compra.getFuncion().getFechaHoraFuncion().format(DATETIME_FORMATTER)
            );
        } else {
            movieTitleLabel.setText("Función no especificada");
            loadPosterImage(null);
        }
        purchaseDateTimeLabel.setText(
                "Compra: " + (compra.getFechaCompra() != null
                        ? compra.getFechaCompra().format(DATETIME_FORMATTER)
                        : "N/A")
        );
        seatLabel.setText("Asiento(s): " +
                (compra.getIdAsiento() != null && !compra.getIdAsiento().isEmpty()
                        ? compra.getIdAsiento()
                        : "N/A")
        );
        ticketIdLabel.setText("ID Boleto(s): " +
                (compra.getIdBoleto() != null && !compra.getIdBoleto().isEmpty()
                        ? compra.getIdBoleto()
                        : "N/A")
        );
        priceLabel.setText(String.format("$%.2f",
                compra.getPrecioFinal() != null ? compra.getPrecioFinal() : 0.00)
        );
        paymentMethodLabel.setText("Pago: " +
                (compra.getMetodoPago() != null ? compra.getMetodoPago() : "N/A")
        );
        purchaseStatusLabel.setText("Estado: " +
                (compra.getEstadoVenta() != null ? compra.getEstadoVenta() : "N/A")
        );
        if (compra.getCodigoQR() != null && !compra.getCodigoQR().isEmpty()) {
            String truncated = compra.getCodigoQR().length() > 20
                    ? compra.getCodigoQR().substring(0, 20) + "..."
                    : compra.getCodigoQR();
            qrInfoLabel.setText("QR Ref: " + truncated);
        } else {
            qrInfoLabel.setText("QR Ref: No disponible");
        }
        if (productsVBox.getChildren().size() > 1) {
            productsVBox.getChildren().remove(1, productsVBox.getChildren().size());
        }
        noProductsLabel.setVisible(false);
        noProductsLabel.setManaged(false);

        List<ProductoSelectionDTO> productos = compra.getProductosComprados();
        if (productos != null && !productos.isEmpty()) {
            for (ProductoSelectionDTO p : productos) {
                Label prodLabel = new Label(
                        p.getCantidad() + "x " + p.getNombre() +
                                " ($" + p.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP) +
                                " c/u = $" + p.getSubtotal().setScale(2, RoundingMode.HALF_UP) + ")"
                );
                prodLabel.getStyleClass().add("details-label-small");
                productsVBox.getChildren().add(prodLabel);
            }
        } else {
            noProductsLabel.setVisible(true);
            noProductsLabel.setManaged(true);
        }
    }

    private void loadPosterImage(String posterPathFromDB) {
        Image finalImageToShow = null;

        if (posterPathFromDB != null && !posterPathFromDB.isEmpty()) {
            String pathToLoad = posterPathFromDB;
            boolean isExternalUrl = posterPathFromDB.toLowerCase().startsWith("http://")
                    || posterPathFromDB.toLowerCase().startsWith("https://");
            boolean isFileProtocol = posterPathFromDB.toLowerCase().startsWith("file:");

            if (!isExternalUrl && !isFileProtocol && !posterPathFromDB.startsWith("/")) {
                pathToLoad = DEFAULT_POSTER_RESOURCE_PREFIX + posterPathFromDB;
            }

            try {
                if (isExternalUrl || isFileProtocol) {
                    finalImageToShow = new Image(pathToLoad, true);
                } else {
                    InputStream stream = getClass().getResourceAsStream(pathToLoad);
                    if (stream != null) {
                        finalImageToShow = new Image(stream);
                        try {
                            stream.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        System.err.println("Poster no encontrado en classpath: " + pathToLoad);
                    }
                }
                if (finalImageToShow != null && finalImageToShow.isError()) {
                    System.err.println("Error al decodificar imagen: " + pathToLoad +
                            " - " + finalImageToShow.getException().getMessage());
                    finalImageToShow = null;
                }
            } catch (Exception ex) {
                System.err.println("Excepción cargando póster '" + pathToLoad + "': " + ex.getMessage());
                finalImageToShow = null;
            }
        }
        if (finalImageToShow == null) {
            try {
                InputStream placeholderStream = getClass().getResourceAsStream(PLACEHOLDER_IMAGE_PATH);
                if (placeholderStream != null) {
                    finalImageToShow = new Image(placeholderStream);
                    try {
                        placeholderStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (finalImageToShow.isError()) {
                        System.err.println("Error decodificando placeholder: " + PLACEHOLDER_IMAGE_PATH +
                                " - " + finalImageToShow.getException().getMessage());
                        finalImageToShow = null;
                    }
                } else {
                    System.err.println("Placeholder NO encontrado: " + PLACEHOLDER_IMAGE_PATH);
                }
            } catch (Exception ex) {
                System.err.println("Error cargando placeholder: " + ex.getMessage());
                finalImageToShow = null;
            }
        }

        moviePosterImageView.setImage(finalImageToShow);
    }
}
