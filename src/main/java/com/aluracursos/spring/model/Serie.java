package com.aluracursos.spring.model;

import java.util.List;
import java.util.OptionalDouble;

import com.aluracursos.spring.service.ChatGPT;
import jakarta.persistence.*;

@Entity
@Table(name="series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(unique = true)
    private String titulo;
    private Integer totalDeTemporadas;
    private Double evaluacion;
    private String poster;
    @Enumerated(EnumType.STRING)
    private Category genero;
    private String sinopsis;
    private String actores;
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER) //one Serie to many episodios
    private List<Episodio> episodios;

    //aqui vamos crear un constructor predeterminado
    public Serie(){}

    //creamos constructor
    public Serie(DatosSerie datosSerie) {
        this.titulo = datosSerie.titulo();
        this.totalDeTemporadas = datosSerie.totalDeTemporadas();
        this.evaluacion = OptionalDouble.of(Double.valueOf(datosSerie.evaluacion())).orElse(0); //aqui convertimos en Double (es como un if else)
        this.poster = datosSerie.poster();
        //con categoy vamos hacer un tratamiento en clase Category.java:
        this.genero = Category.fromString(datosSerie.genero().split(",")[0].trim());
        this.actores = datosSerie.actores();
        this.sinopsis = datosSerie.sinopsis();// ChatGPT.obtenerTraduccion(datosSerie.sinopsis()); para agregar traduccion

    }

    //toString de Serie
    @Override
    public String toString() {
        return "Serie{" +
                "titulo='" + titulo + '\'' +
                ", genero=" + genero +
                ", totalDeTemporadas=" + totalDeTemporadas +
                ", evaluacion=" + evaluacion +
                ", poster='" + poster + '\'' +
                ", sinopsis='" + sinopsis + '\'' +
                ", actores='" + actores + '\'' +
                ", episodios='" + episodios + '\'' +
                '}';
    }
    //getters and setters
    public Long getId() {return Id;
    }

    public void setId(Long id) {Id = id;
    }

    public List<Episodio> getEpisodios() {return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e->e.setSerie(this));
        this.episodios = episodios;
    }

    public String getTitulo() {
        return titulo;}

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalDeTemporadas() {
        return totalDeTemporadas;
    }

    public void setTotalDeTemporadas(Integer totalDeTemporadas) {
        this.totalDeTemporadas = totalDeTemporadas;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Category getGenero() {
        return genero;
    }

    public void setGenero(Category genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }
}
