package be.vdab.cinefest.repositories;

import be.vdab.cinefest.domain.Reservatie;
import be.vdab.cinefest.dto.ReservatieMetFilmTitel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReservatieRepository {
    private final JdbcTemplate template;

    public ReservatieRepository(JdbcTemplate template) {
        this.template = template;
    }

    private final RowMapper<ReservatieMetFilmTitel> reservatieMetFilmTitelMapper = (result, rowNum) ->
        new ReservatieMetFilmTitel(result.getLong("id"), result.getString("titel"),
                result.getInt("plaatsen"),
                result.getObject("besteld", LocalDateTime.class));
    public long create(Reservatie reservatie){
        var sql = """
                insert into reservaties(filmId, emailAdres, plaatsen, besteld)
                values(?, ?, ?, ?)
                """;
        var keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            var statement = connection.prepareStatement(sql,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setLong(1, reservatie.getFilmId());
            statement.setString(2, reservatie.getEmailAdres());
            statement.setInt(3, reservatie.getPlaatsen());
            statement.setObject(4, reservatie.getBesteld());
            return statement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }
    public List<ReservatieMetFilmTitel> findByEmail(String email){
        var sql = """
                select reservaties.id, titel, plaatsen, besteld
                from reservaties
                inner join films
                on reservaties.filmId = films.id
                where emailAdres = ?
                order by id DESC
                """;
        return template.query(sql, reservatieMetFilmTitelMapper, email);
    }
}
