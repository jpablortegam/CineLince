package com.example.cinelinces.services;

import com.example.cinelinces.model.Movie;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class MovieService {

    /**
     * Simula la obtención de próximos estrenos.
     */
    public List<Movie> fetchUpcoming() {
        List<Movie> list = new ArrayList<>();

        list.add(new Movie(
                "Dune: Parte Dos (2025)",
                "Continuación de la épica aventura de Paul Atreides en el desierto de Arrakis.",
                155,
                Arrays.asList("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
                "/com/example/images/posters/placeholder.png",
                Arrays.asList("Ciencia ficción", "Aventura")   // ← aquí
        ));


        list.add(new Movie(
                "Dune: Parte Dos (2025)",
                "Continuación de la épica aventura de Paul Atreides en el desierto de Arrakis.",
                155,
                Arrays.asList("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
                "/com/example/images/posters/placeholder.png",
                Arrays.asList("Ciencia ficción", "Aventura")   // ← aquí
        ));


        list.add(new Movie(
                "Dune: Parte Dos (2025)",
                "Continuación de la épica aventura de Paul Atreides en el desierto de Arrakis.",
                155,
                Arrays.asList("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
                "/com/example/images/posters/placeholder.png",
                Arrays.asList("Ciencia ficción", "Aventura")   // ← aquí
        ));

        list.add(new Movie(
                "Dune: Parte Dos (2025)",
                "Continuación de la épica aventura de Paul Atreides en el desierto de Arrakis.",
                155,
                Arrays.asList("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
                "/com/example/images/posters/placeholder.png",
                Arrays.asList("Ciencia ficción", "Aventura")   // ← aquí
        ));


        return list;
    }

    /**
     * Simula la obtención de películas actualmente en cartelera.
     */
    public List<Movie> fetchNowPlaying() {
        List<Movie> list = new ArrayList<>();

        list.add(new Movie(
                "Dune: Parte Dos (2025)",
                "Continuación de la épica aventura de Paul Atreides en el desierto de Arrakis.",
                155,
                Arrays.asList("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
                "/com/example/images/posters/placeholder.png",
                Arrays.asList("Ciencia ficción", "Aventura")   // ← aquí
        ));


        list.add(new Movie(
                "Dune: Parte Dos (2025)",
                "Continuación de la épica aventura de Paul Atreides en el desierto de Arrakis.",
                155,
                Arrays.asList("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
                "/com/example/images/posters/placeholder.png",
                Arrays.asList("Ciencia ficción", "Aventura")   // ← aquí
        ));

        list.add(new Movie(
                "Dune: Parte Dos (2025)",
                "Continuación de la épica aventura de Paul Atreides en el desierto de Arrakis.",
                155,
                Arrays.asList("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
                "/com/example/images/posters/placeholder.png",
                Arrays.asList("Ciencia ficción", "Aventura")   // ← aquí
        ));

        list.add(new Movie(
                "Dune: Parte Dos (2025)",
                "Continuación de la épica aventura de Paul Atreides en el desierto de Arrakis.",
                155,
                Arrays.asList("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
                "/com/example/images/posters/placeholder.png",
                Arrays.asList("Ciencia ficción", "Aventura")   // ← aquí
        ));


        return list;
    }
}
