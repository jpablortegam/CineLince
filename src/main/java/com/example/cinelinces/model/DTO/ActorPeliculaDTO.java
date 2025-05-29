package com.example.cinelinces.model.DTO;

public class ActorPeliculaDTO {
    private String nombreActor;
    private String personaje;

    public ActorPeliculaDTO(String nombreActor, String personaje) {
        this.nombreActor = nombreActor;
        this.personaje = personaje;
    }

    // Getters
    public String getNombreActor() {
        return nombreActor;
    }

    public String getPersonaje() {
        return personaje;
    }

    // Setters (optional, if you only set values via constructor)
    public void setNombreActor(String nombreActor) {
        this.nombreActor = nombreActor;
    }

    public void setPersonaje(String personaje) {
        this.personaje = personaje;
    }

    // toString can be useful for debugging or simple display
    @Override
    public String toString() {
        return nombreActor + " como " + personaje;
    }
}