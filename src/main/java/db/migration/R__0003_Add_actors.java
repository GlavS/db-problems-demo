package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static db.migration.R__0004_Add_films.ACTORS_COUNT;

@SuppressWarnings("unchecked")
public class R__0003_Add_actors extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DataSource ds = new SingleConnectionDataSource(context.getConnection(), true);
        NamedParameterJdbcOperations jdbc = new NamedParameterJdbcTemplate(ds);

        insertActors(jdbc);
    }

    private void insertActors(NamedParameterJdbcOperations jdbc) {
        List<Map<String, Object>> params = new ArrayList<>(ACTORS_COUNT);
        for (int i = 1; i <= ACTORS_COUNT; i++) {
            String actorName = String.format("Фильмовый актер #%d", i);
            params.add(new MapSqlParameterSource("actorName", actorName).getValues());
        }
        jdbc.batchUpdate("insert into actors (name) values (:actorName)", params.toArray(new Map[0]));
    }
}
