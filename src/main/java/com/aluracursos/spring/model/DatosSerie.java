package com.aluracursos.spring.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) //para que ingore otros datos, solo necesitamos titulo, totalDeTemporadas, evaluacion

public record DatosSerie(
    @JsonAlias("Title") String titulo,
    @JsonAlias("totalSeasons") Integer totalDeTemporadas,
    @JsonAlias("imdbRating") String evaluacion,
    @JsonAlias("Poster") String poster,
    @JsonAlias("Genre") String genero,
    @JsonAlias("Plot") String sinopsis,
    @JsonAlias("Actors") String actores
){
}
