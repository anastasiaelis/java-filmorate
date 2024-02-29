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
        getFilmById(film.getId());
        String sqlQuery = "update film set name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? where id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        sqlQuery = "delete from film_like where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            sqlQuery = "insert into film_like (film_id, user_id) values (?, ?)";
            for (Long id : film.getLikes()) {
                jdbcTemplate.update(sqlQuery, film.getId(), id);
            }
        }
        sqlQuery = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            sqlQuery = "insert into film_genre (film_id, genre_id) values (?, ?)";
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
        String sqlQuery = "select user_id from film_like where film_id = ?";
        Set<Long> likes = new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Long.class, rs.getInt("id")));
        sqlQuery = "select name from mpa where id = ?";
        String mpaName = jdbcTemplate.queryForObject(sqlQuery, String.class, rs.getInt("mpa_id"));
        sqlQuery = "select genre_id from film_genre where film_id = ? order by genre_id";
        Set<Integer> genresId = Set.copyOf(jdbcTemplate.queryForList(sqlQuery, Integer.class, rs.getInt("id")));
        Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
        sqlQuery = "select name from genre where id = ?";
        for (Integer id : genresId) {
            genres.add(new Genre(id, jdbcTemplate.queryForObject(sqlQuery, String.class, id)));
        }
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .likes(likes)
                .mpa(new Mpa(rs.getInt("mpa_id"), mpaName))
                .genres(genres)
                .build();
    }
}