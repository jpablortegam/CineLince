package com.example.cinelinces.controllers;

import com.example.cinelinces.model.Movie;
import com.example.cinelinces.services.MovieService;
import com.example.cinelinces.utils.Animations.DialogAnimationHelper;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UpcomingViewController implements Initializable {

    @FXML private ComboBox<String> filterGenre;
    @FXML private ComboBox<String> filterMonth;
    @FXML private TextField searchField;
    @FXML private GridPane upcomingMoviesGrid;
    @FXML private Pagination upcomingPagination;

    @FXML private StackPane upcomingRootStack;
    @FXML private Pane upcomingOverlayPane;

    private final MovieService movieService = new MovieService(); // Asumiendo que este servicio está (parcialmente) implementado
    private List<CardMovieViewController> cardControllers = new ArrayList<>();
    private List<Movie> allUpcomingMovies = new ArrayList<>();

    private DialogAnimationHelper dialogHelper;

    private final List<String> genres = Arrays.asList(
            "Todos", "Acción", "Aventura", "Comedia", "Drama", "Terror",
            "Ciencia Ficción", "Fantasía", "Romance", "Animación"
    );

    private final List<String> months = Arrays.asList(
            "Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    );

    private boolean initialDataLoaded = false;
    private final int MOVIES_PER_PAGE = 8;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            if (upcomingRootStack != null && upcomingOverlayPane != null) {
                dialogHelper = new DialogAnimationHelper(upcomingRootStack, upcomingOverlayPane);
            } else {
                System.err.println("ADVERTENCIA en UpcomingViewController: upcomingRootStack o upcomingOverlayPane no fueron inyectados.");
            }

            filterGenre.getItems().addAll(genres);
            filterMonth.getItems().addAll(months);

            filterGenre.setOnAction(event -> { if (initialDataLoaded) refreshView(); });
            filterMonth.setOnAction(event -> { if (initialDataLoaded) refreshView(); });
            searchField.textProperty().addListener((obs, oldV, newV) -> { if (initialDataLoaded) refreshView(); });

            upcomingPagination.setPageFactory(pageIndex -> {
                if (initialDataLoaded) {
                    populateGridForPage(pageIndex);
                }
                return new VBox();
            });

            filterGenre.setValue("Todos");
            filterMonth.setValue("Todos");

            // Intenta cargar las películas. Si MovieService no está listo, allUpcomingMovies quedará vacío.
            try {
                this.allUpcomingMovies = movieService.fetchUpcoming();
                if (this.allUpcomingMovies == null) {
                    this.allUpcomingMovies = new ArrayList<>(); // Asegurar que no sea nulo
                    System.err.println("MovieService.fetchUpcoming() devolvió null. Se usará una lista vacía.");
                }
            } catch (Exception e) {
                System.err.println("Error al llamar a movieService.fetchUpcoming(): " + e.getMessage());
                this.allUpcomingMovies = new ArrayList<>(); // Usar lista vacía en caso de error
            }


            initialDataLoaded = true;
            refreshView();
        });
    }

    private void refreshView() {
        if (!initialDataLoaded) return;

        List<Movie> filteredMovies = getFilteredMovies();
        int totalMovies = filteredMovies.size();
        int pageCount = (int) Math.ceil((double) totalMovies / MOVIES_PER_PAGE);
        if (pageCount == 0) {
            pageCount = 1;
        }

        int currentPage = upcomingPagination.getCurrentPageIndex();
        upcomingPagination.setPageCount(pageCount);

        if (currentPage >= pageCount) {
            upcomingPagination.setCurrentPageIndex(Math.max(0, pageCount - 1));
        } else {
            // Si setCurrentPageIndex no cambia el índice actual, el pageFactory no se dispara.
            // Forzamos la actualización si es la misma página.
            if (upcomingPagination.getCurrentPageIndex() == currentPage) {
                populateGridForPage(currentPage);
            } else {
                upcomingPagination.setCurrentPageIndex(currentPage);
            }
        }
        // Si después de ajustar, la página es la misma, y no se disparó el factory, llamar explícitamente.
        if (upcomingPagination.getCurrentPageIndex() == currentPage && upcomingPagination.getPageCount() == pageCount) {
            populateGridForPage(upcomingPagination.getCurrentPageIndex());
        }
    }

    private List<Movie> getFilteredMovies() {
        String selectedGenre = filterGenre.getValue();
        String selectedMonth = filterMonth.getValue(); // Lógica de filtro por mes no implementada aquí
        String searchTerm = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";

        if (allUpcomingMovies == null) { // Salvaguarda
            return new ArrayList<>();
        }

        return allUpcomingMovies.stream()
                .filter(movie -> movie != null && ("Todos".equalsIgnoreCase(selectedGenre) || (movie.getGenre() != null && movie.getGenre().stream().anyMatch(genre -> genre.equalsIgnoreCase(selectedGenre)))))
                .filter(movie -> movie != null && (searchTerm.isEmpty() || (movie.getTitle() != null && movie.getTitle().toLowerCase().contains(searchTerm))))
                .collect(Collectors.toList());
    }

    private void populateGridForPage(int pageIndex) {
        if (!initialDataLoaded) return;

        upcomingMoviesGrid.getChildren().clear();
        cardControllers.clear();

        List<Movie> moviesToDisplayOnPage = getFilteredMovies();

        int startIndex = pageIndex * MOVIES_PER_PAGE;
        if (startIndex >= moviesToDisplayOnPage.size() && pageIndex > 0) {
            return;
        }
        if (startIndex < 0) startIndex = 0;

        int endIndex = Math.min(startIndex + MOVIES_PER_PAGE, moviesToDisplayOnPage.size());
        List<Movie> pageSubList = moviesToDisplayOnPage.subList(startIndex, endIndex);

        int col = 0;
        int row = 0;
        int columnsInGrid = upcomingMoviesGrid.getColumnConstraints().size();
        if (columnsInGrid == 0) columnsInGrid = 4;

        for (Movie movie : pageSubList) {
            if (movie == null) continue; // Saltar si la película es nula
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cinelinces/cardMovie-view.fxml"));
                Node cardNode = loader.load();
                CardMovieViewController ctrl = loader.getController();

                // --- INICIO DE SECCIÓN COMENTADA TEMPORALMENTE ---
                /*
                // ESTA LÍNEA CAUSA EL ERROR SI CardMovieViewController no tiene setMovieData(Movie movie)
                // Asegúrate de que tu CardMovieViewController.java esté actualizado con la versión
                // que incluye public void setMovieData(Movie movie)
                ctrl.setMovieData(movie); // Configurar los datos de la película en la tarjeta

                if (dialogHelper != null) {
                    ctrl.initContext(upcomingMoviesGrid, upcomingOverlayPane, upcomingRootStack, dialogHelper);
                } else {
                     System.err.println("ADVERTENCIA en populateGridForPage: dialogHelper es null. No se puede pasar contexto completo a CardMovieViewController para la película: " + movie.getTitle());
                }
                */
                // --- FIN DE SECCIÓN COMENTADA TEMPORALMENTE ---

                // Si descomentas lo anterior, también descomenta las siguientes líneas:
                // upcomingMoviesGrid.add(cardNode, col, row);
                // cardControllers.add(ctrl);

                // Para que compile "de momento" sin la funcionalidad de tarjeta:
                // Puedes añadir un Label simple para saber que se intentó cargar algo.
                // javafx.scene.control.Label tempLabel = new javafx.scene.control.Label("Intentando cargar: " + movie.getTitle());
                // upcomingMoviesGrid.add(tempLabel, col, row);


                col++;
                if (col >= columnsInGrid) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                System.err.println("NullPointerException al procesar la película: " + (movie != null ? movie.getTitle() : "PELÍCULA NULA"));
                npe.printStackTrace();
            }
        }
        if (pageSubList.isEmpty()) {
            System.out.println("No hay películas 'Próximamente' para mostrar en la página " + pageIndex + " con los filtros actuales.");
            // Opcionalmente, mostrar un mensaje en la UI:
            // Label noMoviesLabel = new Label("No hay películas para mostrar con los filtros actuales.");
            // upcomingMoviesGrid.add(noMoviesLabel, 0, 0);
        }
    }
}
