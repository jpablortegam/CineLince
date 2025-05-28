package com.example.cinelinces.controllers;

import com.example.cinelinces.model.DTO.CompraDetalladaDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
// Elimina 'import java.io.File;' y 'java.net.MalformedURLException;' si no los usas directamente.
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
    private static final String PLACEHOLDER_IMAGE_PATH = "/com/example/images/placeholder_poster.png"; // Define como constante

    public void setData(CompraDetalladaDTO compra) {
        movieTitleLabel.setText(compra.getFuncion().getTituloPelicula());
        cinemaAndSalaLabel.setText(compra.getFuncion().getNombreCine() + " - Sala " + compra.getFuncion().getNumeroSala());
        functionDateTimeLabel.setText("Función: " + compra.getFuncion().getFechaHoraFuncion().format(DATETIME_FORMATTER));
        purchaseDateTimeLabel.setText("Compra: " + compra.getFechaCompra().format(DATETIME_FORMATTER));
        seatLabel.setText("Asiento: " + compra.getIdAsiento());
        ticketIdLabel.setText("Boleto ID: " + compra.getIdBoleto());
        priceLabel.setText(String.format("$%.2f", compra.getPrecioFinal()));
        paymentMethodLabel.setText(compra.getMetodoPago());
        purchaseStatusLabel.setText("Estado: " + compra.getEstadoVenta());

        // --- Lógica de Carga de Imagen Mejorada ---
        String posterPath = compra.getFuncion().getFotografiaPelicula();
        Image finalImageToShow = null;

        if (posterPath != null && !posterPath.isEmpty()) {
            try {
                if (posterPath.startsWith("http://") || posterPath.startsWith("https://")) {
                    finalImageToShow = new Image(posterPath, true); // Cargar desde URL
                } else if (posterPath.startsWith("/")) { // Asumir recurso del Classpath
                    InputStream imageStream = getClass().getResourceAsStream(posterPath);
                    if (imageStream != null) {
                        finalImageToShow = new Image(imageStream);
                    } else {
                        System.err.println("⚠️ Recurso de póster NO ENCONTRADO en classpath: " + posterPath + " para " + compra.getFuncion().getTituloPelicula());
                    }
                } else {
                    // Aquí podrías manejar rutas de archivo locales si es un caso de uso,
                    // por ahora, si no es URL ni classpath absoluto, lo consideramos no encontrado.
                    System.err.println("❓ Ruta de póster no reconocida (ni URL, ni classpath absoluto): " + posterPath);
                }

                // Verificar si la imagen se cargó con error (ej. URL inválida, datos corruptos)
                if (finalImageToShow != null && finalImageToShow.isError()) {
                    System.err.println("⛔ Error al decodificar la imagen del póster: " + posterPath);
                    if (finalImageToShow.getException() != null) {
                        finalImageToShow.getException().printStackTrace(System.err);
                    }
                    finalImageToShow = null; // Forzar fallback a placeholder
                }
            } catch (Exception e) {
                System.err.println("💥 Excepción al intentar cargar el póster '" + posterPath + "': " + e.getMessage());
                // e.printStackTrace(System.err); // Descomenta para ver el stack trace completo si es necesario
                finalImageToShow = null; // Forzar fallback a placeholder
            }
        }

        // Si finalImageToShow es null (no se cargó el póster o hubo error), intentar cargar placeholder
        if (finalImageToShow == null) {
            try {
                InputStream placeholderStream = getClass().getResourceAsStream(PLACEHOLDER_IMAGE_PATH);
                if (placeholderStream != null) {
                    finalImageToShow = new Image(placeholderStream);
                    if (finalImageToShow.isError()) {
                        System.err.println("⛔ Error al decodificar la imagen placeholder: " + PLACEHOLDER_IMAGE_PATH);
                        if (finalImageToShow.getException() != null) {
                            finalImageToShow.getException().printStackTrace(System.err);
                        }
                        finalImageToShow = null; // No hay imagen válida
                    }
                } else {
                    System.err.println("⚠️ Placeholder NO ENCONTRADO en classpath: " + PLACEHOLDER_IMAGE_PATH);
                }
            } catch (Exception e) {
                System.err.println("💥 Excepción crítica al cargar placeholder '" + PLACEHOLDER_IMAGE_PATH + "': " + e.getMessage());
                // e.printStackTrace(System.err);
                finalImageToShow = null;
            }
        }

        moviePosterImageView.setImage(finalImageToShow); // Establece la imagen final (póster o placeholder o null)
        // --- Fin Lógica de Carga de Imagen ---

        if (compra.getCodigoQR() != null && !compra.getCodigoQR().isEmpty()) {
            qrInfoLabel.setText("QR: " + compra.getCodigoQR().substring(0, Math.min(compra.getCodigoQR().length(), 15)) + "...");
        } else {
            qrInfoLabel.setText("QR: No disponible");
        }
    }
}