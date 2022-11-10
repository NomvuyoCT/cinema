package be.vdab.cinefest.domain;

import be.vdab.cinefest.exceptions.OnvoldoendeVrijePlaatsenException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class FilmTest {

    @Test
    void reserveren() {
        var film = new Film("The good nurse", 2022, 4, BigDecimal.TEN);
        film.reserveer(2);
        assertThat(film.getVrijePlaatsen()).isEqualTo(2);
    }

    @Test
    void reserverenMetOnvoldoendeAantalPlaatsenMislukt() {
        var film = new Film("Wild is the wind", 2022, 4, BigDecimal.TEN);
        assertThatExceptionOfType(OnvoldoendeVrijePlaatsenException.class).isThrownBy(() ->
                film.reserveer(5));
    }
}