package be.vdab.cinefest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public record NieuweFilm(@NotBlank String titel, @Min(1900) @Max(9999) int jaar) {
}
