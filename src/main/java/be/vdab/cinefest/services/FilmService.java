package be.vdab.cinefest.services;

import be.vdab.cinefest.domain.Film;
import be.vdab.cinefest.domain.Reservatie;
import be.vdab.cinefest.dto.NieuweFilm;
import be.vdab.cinefest.dto.ReservatieMetFilmTitel;
import be.vdab.cinefest.exceptions.FilmNietGevondenException;
import be.vdab.cinefest.repositories.FilmRepository;
import be.vdab.cinefest.repositories.ReservatieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FilmService {
    private final FilmRepository filmRepository;
    private final ReservatieRepository reservatieRepository;

    public FilmService(FilmRepository filmRepository, ReservatieRepository reservatieRepository) {
        this.filmRepository = filmRepository;

        this.reservatieRepository = reservatieRepository;
    }
    public int findTotaalVrijePlaatsen(){
        return filmRepository.findTotaalVrijePlaatsen();
    }
    public Optional<Film> findById(long id){
        return filmRepository.findById(id);
    }
    public List<Film> findAll(){
        return filmRepository.findAll();
    }
    public List<Film> findByJaar(int jaar){
        return filmRepository.findByJaar(jaar);
    }
    @Transactional
    public void delete(long id){
        filmRepository.delete(id);
    }
    @Transactional
    public long reserveer(NieuweFilm nieuweFilm){
        /*var vrijePlaatsen = 0;
        var aankoopPrijs = BigDecimal.ZERO;*/
        var film = new Film(nieuweFilm.titel(), nieuweFilm.jaar(), 0, BigDecimal.ZERO);
        return filmRepository.create(film);
    }
    @Transactional

    public void update(long id, String titel){
        filmRepository.update(id, titel);
    }
    @Transactional
    public long reserveer(Reservatie reservatie){
        var filmId = reservatie.getFilmId();
        var film = filmRepository.findAndLockById(filmId)
                .orElseThrow(() -> new FilmNietGevondenException(filmId));
        film.reserveer(reservatie.getPlaatsen());
        filmRepository.updateVrijePlaatsen(filmId, film.getVrijePlaatsen());
        return reservatieRepository.create(reservatie);
    }



}
