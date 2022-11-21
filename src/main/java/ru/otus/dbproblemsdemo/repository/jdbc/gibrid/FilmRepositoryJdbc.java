package ru.otus.dbproblemsdemo.repository.jdbc.gibrid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.dbproblemsdemo.models.Actor;
import ru.otus.dbproblemsdemo.models.Director;
import ru.otus.dbproblemsdemo.models.Film;
import ru.otus.dbproblemsdemo.models.Genre;
import ru.otus.dbproblemsdemo.repository.FilmRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jdbc(gibrid)")
@RequiredArgsConstructor
@Repository
public class FilmRepositoryJdbc implements FilmRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final ActorRepositoryJdbc actorsRepository;
    private final ReviewRepositoryJdbc reviewsRepository;

    @Override
    public List<Film> findAll() {
        var films = jdbc.query(
                "select f.[id] [id], " +
                        "f.[title] [title], " +
                        "f.[description] [description], " +
                        "f.[poster] [poster], " +
                        "d.[id] [director_id], " +
                        "d.[name] [director], " +
                        "g.[id] [genre_id], " +
                        "g.[name] [genre] " +
                        "from [films] f left join " +
                        "[genres] g on f.[genre_id] = g.[id] left join " +
                        "[directors] d on f.[director_id] = d.[id]",
                new FilmsRowMapper()
        );
        var reviews = reviewsRepository.findAllForFilms(films);
        films.forEach(film -> film.setReviews(reviews.stream()
                .filter(review -> review.getFilm().getId() == film.getId())
                .collect(Collectors.toSet())
        ));

        var actorsById = actorsRepository.findAllUsed().stream().collect(Collectors.toMap(Actor::getId, Function.identity()));
        var filmsById = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        var relations = findAllFilmAuthorRelations();
        relations.forEach(relation -> {
            var film = filmsById.get(relation.getFilmId());
            var actor = actorsById.get(relation.getActorId());
            if (isNull(film) || isNull(actor)) {
                throw new RuntimeException(String.format("Bad relation. FilmId: %d, actorId: %d",
                        relation.getFilmId(), relation.getActorId()));
            }
            film.getActors().add(actor);
        });

        return films;
    }

    private List<FilmAuthorRelation> findAllFilmAuthorRelations() {
        return jdbc.query("select [film_id], [actor_id] " +
                "from [actors] a inner join films_actors fa on fa.actor_id = a.id", new FilmAuthorRelationRowMapper());
    }

    private static class FilmsRowMapper implements RowMapper<Film> {

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Film(rs.getLong("id"), rs.getString("title"), rs.getString("description"),
                    rs.getBytes("poster"),
                    new Director(rs.getLong("director_id"), rs.getString("director")),
                    new Genre(rs.getLong("genre_id"), rs.getString("genre")),
                    new HashSet<>(),
                    new HashSet<>()
            );
        }
    }

    private static class FilmAuthorRelationRowMapper implements RowMapper<FilmAuthorRelation> {
        @Override
        public FilmAuthorRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmAuthorRelation(rs.getLong("film_id"), rs.getLong("actor_id"));
        }
    }

    @Data
    @AllArgsConstructor
    private static class FilmAuthorRelation{
        private long filmId;
        private long actorId;
    }
}
