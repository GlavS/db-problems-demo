package ru.otus.dbproblemsdemo.repository.jdbc.nplus1;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.dbproblemsdemo.models.Film;
import ru.otus.dbproblemsdemo.repository.FilmRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jdbc(n+1)")
@RequiredArgsConstructor
@Repository
public class FilmRepositoryJdbc implements FilmRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final DirectorRepositoryJdbc directorRepository;
    private final GenreRepositoryJdbc genreRepository;
    private final ActorRepositoryJdbc actorsRepository;
    private final ReviewRepositoryJdbc reviewsRepository;

    @Override
    public List<Film> findAll() {
        return jdbc.query(
                "select f.[id] [id], " +
                        "f.[title] [title], " +
                        "f.[description] [description], " +
                        "f.[poster] [poster], " +
                        "f.[director_id] [director_id], " +
                        "f.[genre_id] [genre_id] " +
                        "from [films] f ",
                new FilmsRowMapper(directorRepository, genreRepository, actorsRepository, reviewsRepository)
        );
    }

    @RequiredArgsConstructor
    private static class FilmsRowMapper implements RowMapper<Film> {

        private final DirectorRepositoryJdbc directorRepository;
        private final GenreRepositoryJdbc genreRepository;
        private final ActorRepositoryJdbc actorsRepository;
        private final ReviewRepositoryJdbc reviewsRepository;

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            var filmId = rs.getLong("id");
            var film = new Film(filmId, rs.getString("title"), rs.getString("description"),
                    rs.getBytes("poster"),
                    directorRepository.findById(rs.getLong("director_id")),
                    genreRepository.findById(rs.getLong("genre_id")),
                    actorsRepository.findAllByFilmId(filmId),
                    null
            );
            film.setReviews(reviewsRepository.findAllByFilm(film));
            return film;
        }
    }
}
