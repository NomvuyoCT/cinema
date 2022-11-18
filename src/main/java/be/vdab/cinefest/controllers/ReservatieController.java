package be.vdab.cinefest.controllers;


import be.vdab.cinefest.domain.Reservatie;
import be.vdab.cinefest.dto.ReservatieMetFilmTitel;
import be.vdab.cinefest.services.FilmService;
import be.vdab.cinefest.services.ReservatieService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@RestController
@RequestMapping("reservaties")
public class ReservatieController {
    private final ReservatieService reservatieService;

    public ReservatieController(ReservatieService reservatieService) {
        this.reservatieService = reservatieService;
    }

    @GetMapping(params = "emailAdres")
    Stream<ReservatieMetFilmTitel> findByEmail(String emailAdres){
        return reservatieService.findReservaties(emailAdres)
                .stream();
    }

}
