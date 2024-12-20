package com.aluracursos.spring.repository;

import com.aluracursos.spring.model.Category;
import com.aluracursos.spring.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.aluracursos.spring.model.Episodio;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie); //repository para buscar serie por nombre de nuestra lista
    //findBy titulo, Contains ? , IgnoreCase - ignore lower and Upper case

    List<Serie> findTop5ByOrderByEvaluacionDesc();

    List<Serie> findByGenero(Category category);

    //List<Serie> findByTotalDeTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalDeTemporadas, Double evaluacion); //Derived Query

    //@Query( value = " SELECT * FROM series WHERE series.total_de_temporadas <= 3 AND series.evaluacion >= 8", nativeQuery = true) //para usar Native Query
    //List<Serie> seriesPorTemporadasYEvaluacion();

    @Query("SELECT s FROM Serie s WHERE s.totalDeTemporadas <= :totalDeTemporadas AND s.evaluacion >= :evaluacion") //JPQL que es mas generalisado
    List<Serie> seriesPorTemporadasYEvaluacion(int totalDeTemporadas, Double evaluacion);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")
    List<Episodio> episodioPorNombre(String nombreEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);

}
