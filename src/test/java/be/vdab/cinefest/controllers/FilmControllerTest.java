package be.vdab.cinefest.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Sql("/films.sql")
@AutoConfigureMockMvc
class FilmControllerTest extends AbstractTransactionalJUnit4SpringContextTests {
    private final String FILMS = "films";
    private final String RESERVATIES = "reservaties";
    private final MockMvc mockMvc;

    public FilmControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
    private long idVanTestFilm1(){
        return jdbcTemplate.queryForObject(
                "select id from films where titel = 'test1'", Long.class
        );
    }
    private final static Path TEST_RESOURCES = Path.of("src/test/resources");

    @Test
    void findById() throws Exception {
        var id = idVanTestFilm1();
        mockMvc.perform(get("/films/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("id").value(id),
                        jsonPath("titel").value("test1")
                );
    }
    @Test
    void findByOnbekendeIdGeeftNotFoundTerug() throws Exception{
        mockMvc.perform(get("/films/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("length()").value(countRowsInTable(FILMS))
                );
    }

    @Test
    void findByJaar() throws Exception {
        mockMvc.perform(get("/films")
                .param("jaar", "2000"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("length()").value(
                                countRowsInTableWhere(FILMS, "jaar = 2000")
                        )
                );
    }

    @Test
    void deleteVerwijdertEenFilm() throws Exception {
        var id = idVanTestFilm1();
        mockMvc.perform(delete("/films/{id}", id))
                .andExpect(status().isOk());
        assertThat(countRowsInTableWhere(FILMS, "id = " + id)).isZero();
    }

    @Test
    void create() throws Exception {
        var jsonData = Files.readString(TEST_RESOURCES.resolve("correcteFilm.json"));
        var responseBody = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(countRowsInTableWhere(FILMS,
                "titel = 'test3' and id = " + responseBody)).isOne();
    }
    @ParameterizedTest
    @ValueSource(strings = {"filmMetLegeTitel.json", "filmZonderTitel.json",
    "filmZonderJaar.json", "filmMetJaarBuitenGrenzen.json"})
    void createMetVerkeerdeDataMislukt(String bestandsNaam) throws Exception{
        var jsonData = Files.readString(TEST_RESOURCES.resolve(bestandsNaam));
        var responseBody = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isBadRequest());
        //his json files and record validation are different. pls see his answer too.
    }

    @Test
    void patchVerandertTitelVanFilm() throws Exception {
        var id = idVanTestFilm1();
        mockMvc.perform(patch("/films/{id}/titel", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("test7"))
                .andExpect(status().isOk());
        assertThat(countRowsInTableWhere(FILMS,
                "titel = 'test7' and id = " + id)).isOne();
    }

    @Test
    void patchMetOnbestaandeIdMislukt() throws Exception {
        mockMvc.perform(patch("/films/{id}/titel", Long.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("gewijzigd"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void patchMetLegeTitelMislukt(String jsonData) throws Exception {
        var id  = idVanTestFilm1();
        mockMvc.perform(patch("/films/{id}/titel", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reserveer() throws Exception {
        var id = idVanTestFilm1();
        var jsonData = Files.readString(TEST_RESOURCES.resolve("correcteReservatie.json"));
        var responseBody =
                mockMvc.perform(post("/films/{id}/reservaties", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        assertThat(countRowsInTableWhere(RESERVATIES,
                "emailAdres = 'test.email@test.com' and id = " + responseBody)).isOne();
        assertThat(countRowsInTableWhere(FILMS,
                "vrijePlaatsen = 2 and id = " + id));
    }

    @ParameterizedTest
    @ValueSource(strings = {"reservatieMetNegatievePlaatsen.json", "reservatieZonderPlaatsen.json",
    "reservatieZonderEmailAdres.json", "reservatieMetVerkeerdeEmailAdres.json"})
    void reservatieMetVerkeerdeDataMislukt(String bestandsNaam) throws Exception {
        var id = idVanTestFilm1();
        var jsonData = Files.readString(TEST_RESOURCES.resolve(bestandsNaam));
        mockMvc.perform(post("/films/{id}/reservaties", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reservatieMetOnbestaandeIdMislukt() throws Exception {
        var jsonData =
                Files.readString(TEST_RESOURCES.resolve("correcteReservatie.json"));
        mockMvc.perform(post("/films/{id}/reservaties", Long.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isNotFound());
    }

    @Test
    void reservatieMetTeVeelPlaatsenMislukt() throws Exception {
        var id = idVanTestFilm1();
        var jsonData =
                Files.readString(TEST_RESOURCES.resolve("reservatieMetTeVeelPlaatsen.json"));
        mockMvc.perform(post("/films/{id}/reservaties", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isConflict());
    }
}