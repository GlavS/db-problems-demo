package ru.otus.dbproblemsdemo.services;

import ru.otus.dbproblemsdemo.dto.FilmDto;

import java.util.List;

public interface FilmService {
    List<FilmDto> findAll();
}
