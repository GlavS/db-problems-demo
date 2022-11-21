package ru.otus.dbproblemsdemo.repository.jdbc.nplus1;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.dbproblemsdemo.models.Film;
import ru.otus.dbproblemsdemo.models.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jdbc(n+1)")
@RequiredArgsConstructor
@Repository
public class ReviewRepositoryJdbc {

    private final NamedParameterJdbcOperations jdbc;

    public Set<Review> findAllByFilm(Film film) {
        return new HashSet<>(jdbc.query("select [id], [review_text] " +
                        "from [reviews] r where r.film_id = :id",
                Map.of("id", film.getId()), new ReviewsRowMapper(film)));
    }

    @RequiredArgsConstructor
    private static class ReviewsRowMapper implements RowMapper<Review> {
        private final Film film;

        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Review(rs.getLong("id"), film, rs.getString("review_text"));
        }
    }
}
