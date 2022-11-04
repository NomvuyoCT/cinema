package be.vdab.cinefest.controllers;

import be.vdab.cinefest.domain.Film;
import be.vdab.cinefest.exceptions.FilmNietGevondenException;
import be.vdab.cinefest.services.FilmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    private record ZonderAankoopprijs(long id, String titel, int jaar, int vrijePlaatsen){
        ZonderAankoopprijs(Film film){
            this(film.getId(), film.getTitel(), film.getJaar(), film.getVrijePlaatsen());
        }
    }
    @GetMapping("films/totaalVrijePlaatsen")
    int  findTotaalVrijePlaatsen(){
        return filmService.findTotaalVrijePlaatsen();
    }
    @GetMapping("films/{id}")
    ZonderAankoopprijs findById(@PathVariable long id){
        return filmService.findById(id)
                .map(film -> new ZonderAankoopprijs(film))
                .orElseThrow(() -> new FilmNietGevondenException(id));
    }
}
