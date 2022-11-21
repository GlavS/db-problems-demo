package ru.otus.dbproblemsdemo.repository.jpa.nplus1;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.dbproblemsdemo.models.Film;
import ru.otus.dbproblemsdemo.repository.FilmRepository;

@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jpa(n+1)")
public interface FilmRepositoryJpa extends FilmRepository, JpaRepository<Film, Long> {
}
