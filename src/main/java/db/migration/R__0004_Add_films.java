package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Objects.isNull;

@SuppressWarnings("unchecked")
public class R__0004_Add_films extends BaseJavaMigration {

    public static final int FILMS_COUNT = 150;
    public static final int ACTORS_COUNT = 100;
    public static final int GENRES_COUNT = 4;
    public static final int DIRECTORS_COUNT = 4;

    private final Random random = new Random();

    @Override
    public void migrate(Context context) throws Exception {
        DataSource ds = new SingleConnectionDataSource(context.getConnection(), true);
        NamedParameterJdbcOperations jdbc = new NamedParameterJdbcTemplate(ds);

        insertFilms(jdbc);
    }

    private void insertFilms(NamedParameterJdbcOperations jdbc) {
        List<Map<String, Object>> params = new ArrayList<>(FILMS_COUNT);
        byte[] poster;
        try(InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("circles_on_a_water.png")) {
            if (isNull(resourceAsStream)) {
                return;
            }
            poster = resourceAsStream.readAllBytes();
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        for (int i = 1; i <= FILMS_COUNT; i++) {
            String filmTitle = String.format("Круги на воде #%d", i);
            String filmDescription = filmTitle.repeat( 2000 / filmTitle.length());
            long genreId = random.nextInt(GENRES_COUNT - 1) + 1;
            long directorId = random.nextInt(DIRECTORS_COUNT - 1) + 1;
            params.add(new MapSqlParameterSource("title", filmTitle)
                    .addValue("genreId", genreId)
                    .addValue("directorId", directorId)
                    .addValue("description", filmDescription)
                    .addValue("poster", poster)
                    .getValues()
            );
        }
        jdbc.batchUpdate("insert into films (title, genre_id, director_id, description, poster) " +
                        "values (:title, :genreId, :directorId, :description, :poster)",
                params.toArray(new Map[0]));
    }
}
