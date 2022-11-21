package ru.otus.dbproblemsdemo.repository.jpa.gibrid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.dbproblemsdemo.models.Film;
import ru.otus.dbproblemsdemo.repository.FilmRepository;

import java.util.List;

@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jpa(gibrid)")
public interface FilmRepositoryJpa extends FilmRepository, JpaRepository<Film, Long> {

    @EntityGraph(attributePaths = {"director", "genre"})
    List<Film> findAll();
}
