package ru.otus.dbproblemsdemo.repository.jdbc.gibrid;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.dbproblemsdemo.models.Film;
import ru.otus.dbproblemsdemo.models.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jdbc(gibrid)")
@RequiredArgsConstructor
@Repository
public class ReviewRepositoryJdbc {

    private final NamedParameterJdbcOperations jdbc;

    public List<Review> findAllForFilms(List<Film> films) {
        return jdbc.query("select [id], [film_id], [review_text] " +
                        "from [reviews] r order by [film_id]", new ReviewsRowMapper(films));
    }

    private static class ReviewsRowMapper implements RowMapper<Review> {
        private final Map<Long, Film> filmsMap;

        private ReviewsRowMapper(List<Film> films) {
            this.filmsMap = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        }

        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            var filmId = rs.getLong("film_id");
            var film = filmsMap.get(filmId);
            if (isNull(film)) {
                throw new RuntimeException(String.format("Commented film with id=%d not found", filmId));
            }
            return new Review(rs.getLong("id"), film, rs.getString("review_text"));
        }
    }
}
