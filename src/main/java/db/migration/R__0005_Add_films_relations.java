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
import static db.migration.R__0004_Add_films.FILMS_COUNT;

@SuppressWarnings("unchecked")
public class R__0005_Add_films_relations extends BaseJavaMigration {

    public static final int REVIEWS_COUNT = 150;
    public static final int ACTORS_IN_FILM_COUNT = 10;

    @Override
    public void migrate(Context context) throws Exception {
        DataSource ds = new SingleConnectionDataSource(context.getConnection(), true);
        NamedParameterJdbcOperations jdbc = new NamedParameterJdbcTemplate(ds);

        insertFilmsRelations(jdbc);
    }

    private void insertFilmsRelations(NamedParameterJdbcOperations jdbc) {
        List<Map<String, Object>> paramsForActors = new ArrayList<>(FILMS_COUNT);
        List<Map<String, Object>> paramsForReviews = new ArrayList<>(FILMS_COUNT);
        int j = 1;
        for (int i = 1; i <= FILMS_COUNT; i++) {
            for (int k = 1; k <= ACTORS_IN_FILM_COUNT; k++) {
                paramsForActors.add(new MapSqlParameterSource("filmId", i).addValue("actorId", j).getValues());
                j = j >= ACTORS_COUNT? 1: j + 1;
            }
            for (int k = 1; k <= REVIEWS_COUNT; k++) {
                paramsForReviews.add(new MapSqlParameterSource("filmId", i)
                        .addValue("reviewText", String.format("Review #%d_%d", i, k)).getValues());
            }
        }
        jdbc.batchUpdate("insert into films_actors (film_id, actor_id) values (:filmId, :actorId)",
                paramsForActors.toArray(new Map[0]));

        jdbc.batchUpdate("insert into reviews (film_id, review_text) values (:filmId, :reviewText)",
                paramsForReviews.toArray(new Map[0]));
    }
}
