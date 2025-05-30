package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream; // Necesario para getResourceAsStream
import java.time.format.DateTimeFormatter;

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

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");
    // La ruta del placeholder ya es absoluta para el classpath, asegúrate que el archivo exista ahí
    private static final String PLACEHOLDER_IMAGE_PATH = "/com/example/images/placeholder_poster.png";
    // Este es el prefijo que usa HomeViewController para las rutas relativas de los pósters
    private static final String DEFAULT_POSTER_RESOURCE_PREFIX = "/com/example/images/";

    public void setData(CompraDetalladaDTO compra) {
        movieTitleLabel.setText(compra.getFuncion().getTituloPelicula());
        cinemaAndSalaLabel.setText(compra.getFuncion().getNombreCine() + " - Sala " + compra.getFuncion().getNumeroSala());
        functionDateTimeLabel.setText("Función: " + compra.getFuncion().getFechaHoraFuncion().format(DATETIME_FORMATTER));
        purchaseDateTimeLabel.setText("Compra: " + compra.getFechaCompra().format(DATETIME_FORMATTER));
        // Ajuste para mostrar Fila y Número de asiento si están disponibles en el DTO (requeriría modificar CompraDetalladaDTO)
        // Por ahora, si IdAsiento es solo el ID:
        seatLabel.setText("Asiento ID: " + compra.getIdAsiento()); // O "Asiento: " + compra.getFilaAsiento() + compra.getNumeroAsiento()
        ticketIdLabel.setText("Boleto ID: " + compra.getIdBoleto());
        priceLabel.setText(String.format("$%.2f", compra.getPrecioFinal()));
        paymentMethodLabel.setText("Pago: " + compra.getMetodoPago());
        purchaseStatusLabel.setText("Estado: " + compra.getEstadoVenta());

        // --- Lógica de Carga de Imagen Ajustada ---
        String posterPathFromDB = compra.getFuncion().getFotografiaPelicula();
        Image finalImageToShow = null;

        if (posterPathFromDB != null && !posterPathFromDB.isEmpty()) {
            String pathToLoad = posterPathFromDB;
            boolean isExternalUrl = posterPathFromDB.toLowerCase().startsWith("http://") || posterPathFromDB.toLowerCase().startsWith("https://");
            boolean isFileProtocol = posterPathFromDB.toLowerCase().startsWith("file:");


            if (!isExternalUrl && !isFileProtocol && !posterPathFromDB.startsWith("/")) {
                // Si no es URL, ni ruta de archivo local, ni ruta absoluta de classpath,
                // asumimos que es una ruta relativa y le anteponemos el prefijo.
                pathToLoad = DEFAULT_POSTER_RESOURCE_PREFIX + posterPathFromDB;
            }
            // Ahora pathToLoad debería ser una URL, una ruta de archivo local, o una ruta de classpath absoluta (ej. "/com/example/images/posters/roma.jpg")

            try {
                if (isExternalUrl || isFileProtocol) {
                    finalImageToShow = new Image(pathToLoad, true); // Cargar desde URL o ruta de archivo
                } else { // Es una ruta de classpath (debería ser absoluta ahora)
                    InputStream imageStream = getClass().getResourceAsStream(pathToLoad);
                    if (imageStream != null) {
                        finalImageToShow = new Image(imageStream);
                        try { imageStream.close(); } catch (Exception ex) { /* ignorar error al cerrar */ }
                    } else {
                        System.err.println("⚠️ Recurso de póster NO ENCONTRADO en classpath: " + pathToLoad + " para " + compra.getFuncion().getTituloPelicula());
                    }
                }

                if (finalImageToShow != null && finalImageToShow.isError()) {
                    System.err.println("⛔ Error al decodificar la imagen del póster: " + pathToLoad);
                    if (finalImageToShow.getException() != null) {
                        // No imprimas el stack trace completo en producción, pero útil para depurar
                        // finalImageToShow.getException().printStackTrace(System.err);
                        System.err.println("   Error específico: " + finalImageToShow.getException().getMessage());
                    }
                    finalImageToShow = null;
                }
            } catch (Exception e) {
                System.err.println("💥 Excepción al intentar cargar el póster '" + pathToLoad + "': " + e.getMessage());
                finalImageToShow = null;
            }
        }

        if (finalImageToShow == null) { // Si falló la carga del póster principal
            try {
                InputStream placeholderStream = getClass().getResourceAsStream(PLACEHOLDER_IMAGE_PATH);
                if (placeholderStream != null) {
                    finalImageToShow = new Image(placeholderStream);
                    try { placeholderStream.close(); } catch (Exception ex) { /* ignorar error al cerrar */ }
                    if (finalImageToShow.isError()) {
                        System.err.println("⛔ Error al decodificar la imagen placeholder: " + PLACEHOLDER_IMAGE_PATH);
                        if (finalImageToShow.getException() != null) {
                            // finalImageToShow.getException().printStackTrace(System.err);
                            System.err.println("   Error específico placeholder: " + finalImageToShow.getException().getMessage());
                        }
                        finalImageToShow = null;
                    }
                } else {
                    System.err.println("⚠️ Placeholder NO ENCONTRADO en classpath: " + PLACEHOLDER_IMAGE_PATH);
                }
            } catch (Exception e) {
                System.err.println("💥 Excepción crítica al cargar placeholder '" + PLACEHOLDER_IMAGE_PATH + "': " + e.getMessage());
                finalImageToShow = null;
            }
        }

        moviePosterImageView.setImage(finalImageToShow);
        // --- Fin Lógica de Carga de Imagen ---

        if (compra.getCodigoQR() != null && !compra.getCodigoQR().isEmpty()) {
            qrInfoLabel.setText("QR: " + compra.getCodigoQR().substring(0, Math.min(compra.getCodigoQR().length(), 15)) + "...");
        } else {
            qrInfoLabel.setText("QR: No disponible");
        }
    }
}