package be.vdab.cinefest.controllers;

import be.vdab.cinefest.domain.Film;
import be.vdab.cinefest.domain.Reservatie;
import be.vdab.cinefest.dto.NieuweFilm;
import be.vdab.cinefest.exceptions.FilmNietGevondenException;
import be.vdab.cinefest.services.FilmService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.stream.Stream;

@RestController
@Validated
@RequestMapping("films")
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
    private record ToevoegReservatie(
            @NotNull @Email String emailAdres,
            @JsonProperty(required = true) @Positive int plaatsen
    ){}
    @GetMapping("totaalVrijePlaatsen")
    int  findTotaalVrijePlaatsen(){
        return filmService.findTotaalVrijePlaatsen();
    }
    @GetMapping("{id}")
    ZonderAankoopprijs findById(@PathVariable long id){
        return filmService.findById(id)
                .map(film -> new ZonderAankoopprijs(film))
                .orElseThrow(() -> new FilmNietGevondenException(id));
    }
    @GetMapping
    Stream<ZonderAankoopprijs> findAll(){
        return filmService.findAll()
                .stream()
                .map(film -> new ZonderAankoopprijs(film));
    }
    @GetMapping(params = "jaar")
    Stream<ZonderAankoopprijs> findByJaar(int jaar){
        return filmService.findByJaar(jaar)
                .stream()
                .map(film -> new ZonderAankoopprijs(film));
    }
    @DeleteMapping("{id}")
    void delete(@PathVariable long id){
        filmService.delete(id);
    }

    @PostMapping
    long reserveer(@RequestBody @Valid NieuweFilm nieuweFilm){
        var id = filmService.reserveer(nieuweFilm);
        return id;
    }
    @PatchMapping("{id}/titel")
    void update(@PathVariable long id,
                @RequestBody @NotBlank String titel){
        filmService.update(id, titel);
    }
    @PostMapping("{id}/reservaties")
    long reserveer(@PathVariable long id,
                   @RequestBody @Valid ToevoegReservatie toevoegReservatie){
        var reservatie = new Reservatie(id,
                toevoegReservatie.emailAdres(), toevoegReservatie.plaatsen());
        return filmService.reserveer(reservatie);
    }

}
