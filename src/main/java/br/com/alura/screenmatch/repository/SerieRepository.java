package br.com.alura.screenmatch.repository;

import org.springframework.data.jpa.repository.Query;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    /*@Query("SELECT s FROM Serie s LEFT JOIN FETCH s.episodios WHERE s.id = :id")
    Optional<Serie> findByIdWithEpisodes(@Param("id") Long id);

    @Query("SELECT s FROM Serie s LEFT JOIN FETCH s.episodios WHERE UPPER(s.titulo) LIKE UPPER(CONCAT('%', :titulo, '%'))")
    List<Serie> findByTituloContainingIgnoreCaseWithEpisodes(@Param("titulo") String titulo);*/
}
