package be.vdab.cinefest.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Sql("/films.sql")
@AutoConfigureMockMvc
class FilmControllerTest extends AbstractTransactionalJUnit4SpringContextTests {
    private final String FILMS = "films";
    private final MockMvc mockMvc;

    public FilmControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
    private long idVanTestFilm1(){
        return jdbcTemplate.queryForObject(
                "select id from films where titel = 'test1'", Long.class
        );
    }

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
}