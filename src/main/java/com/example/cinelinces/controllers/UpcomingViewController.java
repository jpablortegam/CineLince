package com.example.cinelinces.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class UpcomingViewController implements Initializable {

    @FXML
    private ComboBox<String> filterGenre;

    @FXML
    private ComboBox<String> filterMonth;

    @FXML
    private TextField searchField;

    @FXML
    private GridPane upcomingMoviesGrid;

    @FXML
    private Pagination upcomingPagination;

    // Lista de géneros de ejemplo
    private final List<String> genres = Arrays.asList(
            "Todos", "Acción", "Aventura", "Comedia", "Drama", "Terror",
            "Ciencia Ficción", "Fantasía", "Romance", "Animación"
    );

    // Lista de meses de ejemplo
    private final List<String> months = Arrays.asList(
            "Todos", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
            "Octubre", "Noviembre", "Diciembre"
    );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar filtros
        setupFilters();

        // Configurar la paginación
        setupPagination();

        // Configurar el campo de búsqueda
        setupSearchField();
    }

    private void setupFilters() {
        // Inicializar combobox de géneros
        filterGenre.getItems().addAll(genres);
        filterGenre.setValue("Todos");
        filterGenre.setOnAction(event -> refreshMoviesGrid());

        // Inicializar combobox de meses
        filterMonth.getItems().addAll(months);
        filterMonth.setValue("Todos");
        filterMonth.setOnAction(event -> refreshMoviesGrid());
    }

    private void setupPagination() {
        upcomingPagination.setPageFactory(pageIndex -> {
            refreshMoviesGrid();
            return new VBox(); // El contenido real se actualiza en refreshMoviesGrid
        });
    }

    private void setupSearchField() {
        // Configurar el campo de búsqueda para filtrar al escribir
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshMoviesGrid();
        });
    }

    private void refreshMoviesGrid() {
        // Limpiar el grid actual
        upcomingMoviesGrid.getChildren().clear();

        // En una aplicación real, aquí filtrarías tus películas basado en:
        // - filterGenre.getValue()
        // - filterMonth.getValue()
        // - searchField.getText()
        // - upcomingPagination.getCurrentPageIndex()

        // Este es un ejemplo simple que muestra películas ficticias
        int page = upcomingPagination.getCurrentPageIndex();
        int moviesPerPage = 8; // 4 columnas x 2 filas

        for (int i = 0; i < Math.min(moviesPerPage, 8); i++) {
            int row = i / 4;
            int col = i % 4;

            int movieIndex = page * moviesPerPage + i;
            VBox movieCard = createUpcomingMovieCard("Próxima Película " + movieIndex);

            upcomingMoviesGrid.add(movieCard, col, row);
        }
    }

    private VBox createUpcomingMovieCard(String title) {
        // Crear una tarjeta de película para próximos estrenos
        VBox card = new VBox(8);
        card.setPrefWidth(200);
        card.setPrefHeight(300);
        card.getStyleClass().add("movie-card");

        // Crear un placeholder para la imagen
        ImageView poster = new ImageView();
        poster.setFitWidth(180);
        poster.setFitHeight(240);
        poster.getStyleClass().add("movie-poster");

        // Crear etiqueta para el título
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("movie-title");

        // Crear etiqueta para la fecha de estreno
        Label releaseDate = new Label("Estreno: Próximamente");
        releaseDate.getStyleClass().add("movie-release-date");

        card.getChildren().addAll(poster, titleLabel, releaseDate);
        return card;
    }
}