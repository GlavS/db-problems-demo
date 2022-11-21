package ru.otus.dbproblemsdemo.repository.jdbc.gibrid;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.dbproblemsdemo.models.Actor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jdbc(gibrid)")
@RequiredArgsConstructor
@Repository
public class ActorRepositoryJdbc {

    private final NamedParameterJdbcOperations jdbc;

    public List<Actor> findAllUsed() {
        return jdbc.query("select distinct [id], [name] " +
                        "from [actors] a inner join films_actors fa on fa.actor_id = a.id", new ActorsRowMapper());
    }

    private static class ActorsRowMapper implements RowMapper<Actor> {
        @Override
        public Actor mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Actor(rs.getLong("id"), rs.getString("name"));
        }
    }
}
