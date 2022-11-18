package be.vdab.cinefest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public record ReservatieMetFilmTitel(@Positive long id, @NotBlank String titel,
                                     @Positive int aantalPlaatsen, LocalDateTime besteld) {
}
