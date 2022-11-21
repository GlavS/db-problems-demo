package ru.otus.dbproblemsdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Base64Utils;
import ru.otus.dbproblemsdemo.models.Actor;
import ru.otus.dbproblemsdemo.models.Film;

import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDto {
    private long id;
    private String title;
    private String description;
    private String director;
    private String genre;
    private String actors;
    private String poster;

    public static FilmDto fromDomainObject(Film film) {
        return new FilmDto(film.getId(),
                film.getTitle(),
                film.getDescription().substring(0, 300) + "...",
                film.getDirector().getName(),
                film.getGenre().getName(),
                film.getActors().stream()
                        .map(Actor::getName)
                        .collect(Collectors.joining(", ")),
                Base64Utils.encodeToString(film.getPoster()));
    }
}
