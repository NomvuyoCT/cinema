package be.vdab.cinefest.controllers;

import be.vdab.cinefest.services.FilmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }
    @GetMapping("films/totaalVrijePlaatsen")
    int  findTotaalVrijePlaatsen(){
        return filmService.findTotaalVrijePlaatsen();
    }
}
