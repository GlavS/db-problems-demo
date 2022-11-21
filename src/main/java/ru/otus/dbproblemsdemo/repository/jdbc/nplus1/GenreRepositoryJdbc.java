package ru.otus.dbproblemsdemo.repository.jdbc.nplus1;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.dbproblemsdemo.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jdbc(n+1)")
@RequiredArgsConstructor
@Repository
public class GenreRepositoryJdbc {

    private final NamedParameterJdbcOperations jdbc;

    public Genre findById(long id) {
        return jdbc.queryForObject("select [id], [name] from [genres] where id = :id ",
                Map.of("id", id), new GenresRowMapper());
    }

    private static class GenresRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(rs.getLong("id"), rs.getString("name"));
        }
    }
}
