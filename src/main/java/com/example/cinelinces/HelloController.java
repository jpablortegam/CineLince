// com/example/cinelinces/HelloController.java
package com.example.cinelinces;

import com.example.cinelinces.model.Movie;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class HelloController {

    @FXML private Pagination paginationCartelera;
    @FXML private Pagination paginationUpcoming;

    private List<Movie> carteleraList = new ArrayList<>();
    private List<Movie> upcomingList  = new ArrayList<>();

    // Mostrar 3 cards por página
    private static final int ITEMS_PER_PAGE = 3;

    @FXML
    public void initialize() {
        loadDummyData();  // TODO: reemplaza por tu carga real

        paginationCartelera.setPageCount(calcPages(carteleraList));
        paginationUpcoming .setPageCount(calcPages(upcomingList));

        paginationCartelera.setPageFactory(pi -> createPageForList(carteleraList, pi));
        paginationUpcoming .setPageFactory(pi -> createPageForList(upcomingList,  pi));
    }

    private void loadDummyData() {
        for (int i = 1; i <= 9; i++) {
            carteleraList.add(new Movie("Peli " + i, "https://via.placeholder.com/240x360"));
            upcomingList .add(new Movie("Next " + i, "https://via.placeholder.com/240x360"));
        }
    }

    private int calcPages(List<?> list) {
        return (int) Math.ceil((double) list.size() / ITEMS_PER_PAGE);
    }

    private Node createPageForList(List<Movie> list, int pageIndex) {
        int start = pageIndex * ITEMS_PER_PAGE;
        int end   = Math.min(start + ITEMS_PER_PAGE, list.size());
        List<Movie> sub = list.subList(start, end);

        HBox row = new HBox(30);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(10, 0, 10, 0));
        for (Movie m : sub) {
            row.getChildren().add(buildMovieCard(m));
        }
        return row;
    }

    private Node buildMovieCard(Movie movie) {
        // 1) Imagen más grande
        ImageView iv = new ImageView(new Image(movie.getPosterUrl(), 240, 360, true, true));
        Rectangle clip = new Rectangle(240, 360);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        iv.setClip(clip);

        // 2) Degradado en la parte inferior
        Rectangle gradient = new Rectangle(240, 100);
        gradient.setTranslateY(130);
        gradient.setFill(new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.color(0,0,0,0)),
                new Stop(1, Color.color(0,0,0,0.8))
        ));

        // 3) Título
        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("card-title");
        StackPane.setAlignment(title, Pos.BOTTOM_CENTER);
        StackPane.setMargin(title, new Insets(0, 12, 12, 12));

        // 4) Montaje
        StackPane card = new StackPane(iv, gradient, title);
        card.getStyleClass().add("movie-card");
        return card;
    }
}
