package com.example.cinelinces.model;

public class PeliculaActor {
    private int idPelicula;
    private int idActor;
    private String personaje;

    public PeliculaActor() {
    }

    public PeliculaActor(int idPelicula, int idActor, String personaje) {
        this.idPelicula = idPelicula;
        this.idActor = idActor;
        this.personaje = personaje;
    }

    // Getters y Setters
    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public int getIdActor() {
        return idActor;
    }

    public void setIdActor(int idActor) {
        this.idActor = idActor;
    }

    public String getPersonaje() {
        return personaje;
    }

    public void setPersonaje(String personaje) {
        this.personaje = personaje;
    }
}