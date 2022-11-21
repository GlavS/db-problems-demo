package ru.otus.dbproblemsdemo.repository.jdbc.nplus1;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.dbproblemsdemo.models.Actor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@ConditionalOnProperty(prefix = "app", name = "repositoryMode", havingValue = "jdbc(n+1)")
@RequiredArgsConstructor
@Repository
public class ActorRepositoryJdbc {

    private final NamedParameterJdbcOperations jdbc;

    public Set<Actor> findAllByFilmId(long filmId) {
        return new HashSet<>(jdbc.query("select [id], [name] " +
                        "from [actors] a inner join films_actors fa on fa.film_id = :film_id and fa.actor_id = a.id",
                Map.of("film_id", filmId), new ActorsRowMapper()));
    }

    private static class ActorsRowMapper implements RowMapper<Actor> {
        @Override
        public Actor mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Actor(rs.getLong("id"), rs.getString("name"));
        }
    }
}
