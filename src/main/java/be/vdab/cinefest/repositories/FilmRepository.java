package be.vdab.cinefest.repositories;

import be.vdab.cinefest.domain.Film;
import be.vdab.cinefest.exceptions.FilmNietGevondenException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
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
    public List<Film> findAll(){
        var sql = """
                select id, titel, jaar, vrijePlaatsen, aankoopprijs
                from films
                order by titel;
                """;
        return template.query(sql, filmRowMapper);
    }
    public List<Film> findByJaar(int jaar){
        var sql = """
                select id, titel, jaar, vrijePlaatsen, aankoopprijs
                from films
                where jaar = ?
                order by titel;
                """;
        return template.query(sql, filmRowMapper, jaar);
    }
    public void delete(long id){
        var sql = """
                delete from films
                where id = ?
                """;
        template.update(sql, id);
    }
    public long create(Film film){
        var sql = """
                insert into films (titel, jaar, vrijePlaatsen, aankoopPrijs)
                values (?, ?, ?, ?)
                """;
        var keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            var statement = connection.prepareStatement(sql,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, film.getTitel());
            statement.setInt(2, film.getJaar());
            statement.setInt(3, film.getVrijePlaatsen());
            statement.setBigDecimal(4, film.getAankoopprijs());
            return statement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }
    public void update(long id, String titel){
        var sql = """
                update films
                set titel = ?
                where id = ?;
                """;
        if (template.update(sql, titel, id) == 0){
            throw new FilmNietGevondenException(id);
        }
    }


}
