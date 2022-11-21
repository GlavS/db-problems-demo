package ru.otus.dbproblemsdemo.repository;

import ru.otus.dbproblemsdemo.models.Film;

import java.util.List;

public interface FilmRepository {
    List<Film> findAll();
}
