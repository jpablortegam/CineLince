package com.example.cinelinces.services;

import com.example.cinelinces.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieService {

    /**
     * Simula la obtención de próximos estrenos.
     */
    public List<Movie> fetchUpcoming() {
        List<Movie> list = new ArrayList<>();
        list.add(new Movie("Dune: Parte Dos (2025)",   "/com/example/images/posters/placeholder.png"));
        list.add(new Movie("Avatar: El camino del agua (2025)", "/com/example/images/posters/placeholder.png"));
        list.add(new Movie("Misión Imposible 8 (2025)", "/com/example/images/posters/placeholder.png"));
        list.add(new Movie("Star Wars: Rogue Squadron (2025)",   "/com/example/images/posters/placeholder.png"));
        // añade más si quieres…
        return list;
    }

    /**
     * Simula la obtención de películas actualmente en cartelera.
     */
    public List<Movie> fetchNowPlaying() {
        List<Movie> list = new ArrayList<>();
        list.add(new Movie("Top Gun: Maverick (2022)", "/com/example/images/posters/placeholder.png"));
        list.add(new Movie("Spider-Man: No Way Home (2021)", "/com/example/images/posters/placeholder.png"));
        list.add(new Movie("Doctor Strange in the Multiverse of Madness (2022)", "/com/example/images/posters/placeholder.png"));
        list.add(new Movie("Jurassic World: Dominion (2022)", "/com/example/images/posters/placeholder.png"));
        // …
        return list;
    }
}
