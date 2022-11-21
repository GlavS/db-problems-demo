package ru.otus.dbproblemsdemo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.dbproblemsdemo.dto.FilmDto;
import ru.otus.dbproblemsdemo.models.Film;
import ru.otus.dbproblemsdemo.repository.FilmRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmRepository filmRepository;

    @Override
    public List<FilmDto> findAll() {
        return filmRepository.findAll().stream()
                .map(FilmDto::fromDomainObject)
                .collect(Collectors.toList());
    }
}
