package be.vdab.cinefest.repositories;

import be.vdab.cinefest.domain.Film;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FilmRepository {
    private final JdbcTemplate template;

    public FilmRepository(JdbcTemplate template) {
        this.template = template;
    }

    private final RowMapper<Film> filmRowMapper = (result, rowNum) ->
            new Film(result.getLong("id"), result.getString("titel"),
                    result.getInt("jaar"), result.getInt("vrijePlaatsen"),
                    result.getBigDecimal("aankoopprijs"));
    public int findTotaalVrijePlaatsen(){
        var sql = """
                select sum(vrijePlaatsen)  
                from films
                """;
        return template.queryForObject(sql, Integer.class);
    }
    public Optional<Film> findById(long id){
        var sql = """
                select id, titel, jaar, vrijePlaatsen, aankoopprijs
                from films
                where id = ?
                """;
        try {
            return Optional.of(template.queryForObject(sql, filmRowMapper, id));
        } catch (IncorrectResultSizeDataAccessException ex){
            return Optional.empty();
        }
    }
}
