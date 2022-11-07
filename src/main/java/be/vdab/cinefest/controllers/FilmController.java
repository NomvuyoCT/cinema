package be.vdab.cinefest.controllers;

import be.vdab.cinefest.domain.Film;
import be.vdab.cinefest.dto.NieuweFilm;
import be.vdab.cinefest.exceptions.FilmNietGevondenException;
import be.vdab.cinefest.services.FilmService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.stream.Stream;

@RestController
@Validated
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
    @GetMapping("films")
    Stream<ZonderAankoopprijs> findAll(){
        return filmService.findAll()
                .stream()
                .map(film -> new ZonderAankoopprijs(film));
    }
    @GetMapping(value = "films", params = "jaar")
    Stream<ZonderAankoopprijs> findByJaar(int jaar){
        return filmService.findByJaar(jaar)
                .stream()
                .map(film -> new ZonderAankoopprijs(film));
    }
    @DeleteMapping("films/{id}")
    void delete(@PathVariable long id){
        filmService.delete(id);
    }

    @PostMapping("films")
    long create(@RequestBody @Valid NieuweFilm nieuweFilm){
        var id = filmService.create(nieuweFilm);
        return id;
    }
    @PatchMapping("films/{id}/titel")
    void update(@PathVariable long id,
                @RequestBody @NotBlank String titel){
        filmService.update(id, titel);
    }

}
