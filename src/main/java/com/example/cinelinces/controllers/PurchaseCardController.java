package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;

/**
 * Controlador de purchase-card-view.fxml.
 * Se encarga de mostrar todos los datos de la compra recién realizada,
 * incluyendo póster, título, cine, sala, fecha de función, fecha de compra,
 * asiento, ID de boleto, precio, método de pago, estado y un extracto del QR.
 */
public class PurchaseCardController {

    @FXML private ImageView moviePosterImageView;
    @FXML private Label movieTitleLabel;
    @FXML private Label cinemaAndSalaLabel;
    @FXML private Label functionDateTimeLabel;

    @FXML private Label purchaseDateTimeLabel;
    @FXML private Label seatLabel;
    @FXML private Label ticketIdLabel;

    @FXML private Label priceLabel;
    @FXML private Label paymentMethodLabel;

    @FXML private Label purchaseStatusLabel;
    @FXML private Label qrInfoLabel;

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");

    // Ruta al placeholder en /resources (verifica que exista)
    private static final String PLACEHOLDER_IMAGE_PATH = "/com/example/images/placeholder_poster.png";
    // Prefijo que usas para las imágenes de póster en classpath
    private static final String DEFAULT_POSTER_RESOURCE_PREFIX = "/com/example/images/";

    /**
     * Este método lo invoca el PurchaseSummaryViewController justo antes de mostrar
     * la ventana modal de la “PurchaseCard”. Obtenemos un CompraDetalladaDTO con:
     *   - getFuncion() = DTO con título, cine, sala, precio boleto, fecha/hora, ruta a póster, etc.
     *   - getFechaCompra(), getIdAsiento(), getIdBoleto(), getPrecioFinal(), getMetodoPago(), getEstadoVenta(), getCodigoQR()
     */
    public void setData(CompraDetalladaDTO compra) {
        // 1) Título y póster
        movieTitleLabel.setText(compra.getFuncion().getTituloPelicula());
        loadPosterImage(compra.getFuncion().getFotografiaPelicula());

        // 2) Cine y Sala
        cinemaAndSalaLabel.setText(
                compra.getFuncion().getNombreCine() + " - Sala " + compra.getFuncion().getNumeroSala()
        );

        // 3) Fecha/Hora de la función
        functionDateTimeLabel.setText(
                "Función: " + compra.getFuncion().getFechaHoraFuncion().format(DATETIME_FORMATTER)
        );

        // 4) Fecha/Hora de compra
        purchaseDateTimeLabel.setText(
                "Compra: " + compra.getFechaCompra().format(DATETIME_FORMATTER)
        );

        // 5) Asiento y ID de boleto
        //    (En el ejemplo usamos idAsiento; si tienes fila y número en el DTO, reemplaza)
        seatLabel.setText("Asiento ID: " + compra.getIdAsiento());
        ticketIdLabel.setText("Boleto ID: " + compra.getIdBoleto());

        // 6) Precio y método de pago
        priceLabel.setText(String.format("$%.2f", compra.getPrecioFinal()));
        paymentMethodLabel.setText("Pago: " + compra.getMetodoPago());

        // 7) Estado de la venta (ej. “Completada” o “Cancelada”)
        purchaseStatusLabel.setText("Estado: " + compra.getEstadoVenta());

        // 8) Mostrar solo los primeros 15 caracteres del QR (si existe)
        if (compra.getCodigoQR() != null && !compra.getCodigoQR().isEmpty()) {
            String truncated = compra.getCodigoQR().length() > 15
                    ? compra.getCodigoQR().substring(0, 15) + "..."
                    : compra.getCodigoQR();
            qrInfoLabel.setText("QR: " + truncated);
        } else {
            qrInfoLabel.setText("QR: No disponible");
        }
    }

    /**
     * Intenta cargar el póster. Puede venir:
     *   1) URL externa (http://...)
     *   2) Ruta de archivo (file:/C:/...)
     *   3) Ruta relativa en classpath (p. ej. “posters/interestelar.jpg”)
     * Si falla, carga un placeholder por defecto.
     */
    private void loadPosterImage(String posterPathFromDB) {
        Image finalImageToShow = null;

        if (posterPathFromDB != null && !posterPathFromDB.isEmpty()) {
            String pathToLoad = posterPathFromDB;
            boolean isExternalUrl = posterPathFromDB.toLowerCase().startsWith("http://")
                    || posterPathFromDB.toLowerCase().startsWith("https://");
            boolean isFileProtocol = posterPathFromDB.toLowerCase().startsWith("file:");

            if (!isExternalUrl && !isFileProtocol && !posterPathFromDB.startsWith("/")) {
                // Suponemos ruta relativa, anteponemos prefijo de resources
                pathToLoad = DEFAULT_POSTER_RESOURCE_PREFIX + posterPathFromDB;
            }

            try {
                if (isExternalUrl || isFileProtocol) {
                    finalImageToShow = new Image(pathToLoad, true);
                } else {
                    InputStream stream = getClass().getResourceAsStream(pathToLoad);
                    if (stream != null) {
                        finalImageToShow = new Image(stream);
                        stream.close();
                    } else {
                        System.err.println("Poster no encontrado en classpath: " + pathToLoad);
                    }
                }
                if (finalImageToShow != null && finalImageToShow.isError()) {
                    System.err.println("Error al decodificar imagen: " + pathToLoad);
                    finalImageToShow = null;
                }
            } catch (Exception ex) {
                System.err.println("Excepción cargando póster '" + pathToLoad + "': " + ex.getMessage());
                finalImageToShow = null;
            }
        }

        if (finalImageToShow == null) {
            // Cargar placeholder
            try {
                InputStream placeholderStream = getClass().getResourceAsStream(PLACEHOLDER_IMAGE_PATH);
                if (placeholderStream != null) {
                    finalImageToShow = new Image(placeholderStream);
                    placeholderStream.close();
                    if (finalImageToShow.isError()) {
                        System.err.println("Error decodificando placeholder: " + PLACEHOLDER_IMAGE_PATH);
                        finalImageToShow = null;
                    }
                } else {
                    System.err.println("Placeholder NO encontrado: " + PLACEHOLDER_IMAGE_PATH);
                }
            } catch (Exception e) {
                System.err.println("Error cargando placeholder: " + e.getMessage());
                finalImageToShow = null;
            }
        }

        moviePosterImageView.setImage(finalImageToShow);
    }
}
