package ru.otus.dbproblemsdemo.repository.jdbc.multiply;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.dbproblemsdemo.models.*;
import ru.otus.dbproblemsdemo.repository.FilmRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jdbc(multiply)")
@RequiredArgsConstructor
@Repository
public class FilmRepositoryJdbc implements FilmRepository {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Film> findAll() {

        return jdbc.query(
                "select f.[id] [id], " +
                        "f.[title] [title], " +
                        "f.[description] [description] , " +
                        "d.[id] [director_id], " +
                        "d.[name] [director], " +
                        "g.[id] [genre_id], " +
                        "g.[name] [genre], " +
                        "a.[id] [actor_id], " +
                        "a.[name] [actor], " +
                        "r.[id] [review_id], " +
                        "r.[review_text] [review], " +
                        "f.[poster] [poster] " +
                        "from [films] f left join " +
                        "[genres] g on f.[genre_id] = g.[id] left join " +
                        "[directors] d on f.[director_id] = d.[id] left join " +
                        "[films_actors] fa on f.[id] = fa.[film_id] left join " +
                        "[actors] a on fa.[actor_id] = a.[id] left join " +
                        "[reviews] r on f.id = r.[film_id]", new FilmsResultSetExtractor());
    }

    private static class FilmsResultSetExtractor implements ResultSetExtractor<List<Film>> {
        @Override
        public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
            var filmsByIdMap = new HashMap<Long, Film>();
            var actorsIdsByFilmIdSet = new HashSet<String>();
            var reviewsIdsByFilmIdSet = new HashSet<String>();
            while (rs.next()) {
                var filmId = rs.getLong("id");
                filmsByIdMap.computeIfAbsent(filmId, id -> resultSetToFilm(id, rs));
                var film = filmsByIdMap.get(filmId);

                var actorId = rs.getLong("actor_id");
                if (actorsIdsByFilmIdSet.add(film.getId() + "_" + actorId)) {
                    film.getActors().add(new Actor(actorId, rs.getString("actor")));
                }

                var reviewId = rs.getLong("review_id");
                if (reviewsIdsByFilmIdSet.add(film.getId() + "_" + actorId)) {
                    film.getReviews().add(new Review(reviewId, film, rs.getString("review")));
                }
            }

            return new ArrayList<>(filmsByIdMap.values());
        }

        private Film resultSetToFilm(long id, ResultSet rs) {
            try {
                return new Film(id, rs.getString("title"), rs.getString("description"), rs.getBytes("poster"),
                        new Director(rs.getLong("director_id"), rs.getString("director")),
                        new Genre(rs.getLong("genre_id"), rs.getString("genre")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
