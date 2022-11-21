package ru.otus.dbproblemsdemo.repository.jdbc.nplus1;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.dbproblemsdemo.models.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jdbc(n+1)")
@RequiredArgsConstructor
@Repository
public class DirectorRepositoryJdbc {

    private final NamedParameterJdbcOperations jdbc;

    public Director findById(long id) {
        return jdbc.queryForObject("select [id], [name] from [directors] where id = :id ",
                Map.of("id", id), new DirectorsRowMapper());
    }

    private static class DirectorsRowMapper implements RowMapper<Director> {
        @Override
        public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Director(rs.getLong("id"), rs.getString("name"));
        }
    }
}
