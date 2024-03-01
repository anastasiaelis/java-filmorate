package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        int id = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).intValue();
        log.info("Фильм {} был добавлен", id);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sqlQuery = "insert into film_genre (film_id, genre_id) values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery, id, genre.getId());
            }
        }
        return getFilmById(id);
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET " +
                "film_name = ?, film_description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                " WHERE film_id = ?";
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sqlQuery = "insert into film_genre (film_id, genre_id) values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
        log.info("Фильм {} был обновлен", film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> get() {
        String sqlQuery = "select * from film";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "select * from film where id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToFilm(rs), id);
        if (films.size() != 1) {
            log.error("Фильм с id={} не найден", id);
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", id));
        }
        return films.get(0);
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
//        Mpa mpav = Mpa.builder()
//                .id(rs.getInt("mpa.id"))
//                .build();
//        return Film.builder()
//                .id((int) rs.getLong("id"))
//                .name(rs.getString("name"))
//                .description(rs.getString("description"))
//                .releaseDate(rs.getDate("releaseDate").toLocalDate())
//                .duration(rs.getInt("duration"))
//                .mpa(mpav)
//                .build();
        int id = (int) rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("name");
        Mpa mpav = Mpa.builder()
                .id(mpaId)
                .name(mpaName)
                .build();

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpav)
                .build();
    }
}