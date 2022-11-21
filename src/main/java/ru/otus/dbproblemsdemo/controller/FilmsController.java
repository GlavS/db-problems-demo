package ru.otus.dbproblemsdemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.dbproblemsdemo.services.FilmService;

@Controller
@RequiredArgsConstructor
public class FilmsController {

    private final FilmService filmService;

    @GetMapping("/")
    public String filmsListPage(Model model) {
        long before = System.currentTimeMillis();
        var films = filmService.findAll();
        long after = System.currentTimeMillis();

        model.addAttribute("duration", (after - before) / 1000);
        model.addAttribute("films", films);
        return "filmsList";
    }

}
