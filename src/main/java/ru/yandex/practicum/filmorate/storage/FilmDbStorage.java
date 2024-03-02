package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

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
        String sqlQuery = "INSERT INTO film(film_name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null) {
            addGenres(film.getId(), film.getGenres());
        }

        return getFilmById(film.getId());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE film SET film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";

        int rowsUpdated = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            stmt.setInt(6, film.getId());
            return stmt;
        });

        if (rowsUpdated == 0) {
            throw new FilmNotFoundException("Unable to update film.");
        }

        if (film.getGenres() != null) {
            deleteFilmGenres(film.getId());
            addGenres(film.getId(), film.getGenres());
        }

        return getFilmById(film.getId());
    }

    @Override
    public List<Film> get() {
        return null;
    }

    @Override
    public void remove(Film film) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ?";
        int rowsUpdated = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, film.getId());
            return stmt;
        });

        if (rowsUpdated == 0) {
            throw new ValidationException("Unable to remove user.");
        }
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.film_id as id, f.film_name as name, f.description as description, f.duration as duration, " +
                "f.release_date as release_date, f.mpa_id as mpa_id, m.mpa_name as mpa_name " +
                "FROM film AS f, mpa AS m WHERE f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sqlQuery, FILM_ROW_MAPPER);
        films.forEach(film -> {
            film.setGenres(getFilmGenres(film.getId()));
        });
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        try {
            String sqlQuery = "SELECT f.film_id as id, f.film_name as name, f.description as description, f.duration as duration, " +
                    "f.release_date as release_date, f.mpa_id as mpa_id, m.mpa_name as mpa_name " +
                    "FROM film AS f, mpa AS m WHERE f.film_id = ? AND f.mpa_id = m.mpa_id";
            Film film = jdbcTemplate.queryForObject(sqlQuery, FILM_ROW_MAPPER, id);
            if (film != null) {
                film.setGenres(getFilmGenres(id));
            }
            return film;
        } catch (EmptyResultDataAccessException ex) {
            throw new FilmNotFoundException("Failed to get film by this id.");
        }
    }

    @Override
    public List<Genre> getFilmGenres(Integer filmId) {
        String sqlQuery = "SELECT g.genre_id as id, g.genre_name as name FROM genre AS g INNER JOIN film_genre AS fg ON fg.genre_id = g.genre_id WHERE fg.film_id = ? ORDER BY g.genre_id";
        return jdbcTemplate.query(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, filmId);
            return stmt;
        }, GENRE_ROW_MAPPER);
    }

    private void deleteFilmGenres(Integer filmId) {
        String sqlQueryDeleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryDeleteGenres);
            stmt.setInt(1, filmId);
            return stmt;
        });
    }

    @Override
    public Film updateFilmGenres(Film film) {
        deleteFilmGenres(film.getId());
        addGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public void addLike(Integer userId, Integer filmId) {
        String sqlQuery = "INSERT INTO film_like(user_id, film_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setInt(1, userId);
            stmt.setInt(2, filmId);
            return stmt;
        });
    }

    @Override
    public void removeLike(Integer userId, Integer filmId) {
        String sqlQuery = "DELETE FROM film_like WHERE user_id = ? and film_id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, userId);
            stmt.setInt(2, filmId);
            return stmt;
        });
    }

    @Override
    public List<Film> getTopLikedFilms(Integer count) {

        if (count != null && count < 0) {
            throw new ValidationException("Negative value count is not allowed.");
        }

        String sqlQuery = "SELECT f.film_id as id, f.film_name as name, f.description as description, f.duration as duration, " +
                "f.release_date as release_date, f.mpa_id as mpa_id, m.mpa_name as mpa_name, count(fl.user_Id) as likes_count " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_like as fl " +
                "ON f.film_id = fl.film_id GROUP BY f.film_id ORDER BY likes_count DESC";

        if (count != null && count > 0) {
            sqlQuery += " limit " + count;
        }
        List<Film> films = jdbcTemplate.query(sqlQuery, FILM_ROW_MAPPER);
        films.forEach(film -> {
            film.setGenres(getFilmGenres(film.getId()));
        });
        return films;
    }

    @Override
    public Mpa getMpa(Integer id) {
        try {
            String sqlQuery = "SELECT mpa_id, mpa_name FROM mpa WHERE mpa_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, MPA_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new MpaNotFoundException("Failed to get mpa by this id.");
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, MPA_ROW_MAPPER);
    }

    @Override
    public Genre getGenre(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM genre WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, GENRE_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new FilmNotFoundException("Failed to get film by this id.");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genre";
        return jdbcTemplate.query(sqlQuery, GENRE_ROW_MAPPER);
    }

    private void addGenres(Integer filmId, List<Genre> genres) {
        genres = genres.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(Genre::getId))), ArrayList::new));

        String sqlQuery = "INSERT INTO film_genre(film_id, genre_id) " +
                "VALUES (?, ?)";
        genres.forEach(genre -> {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setInt(1, filmId);
                stmt.setInt(2, genre.getId());
                return stmt;
            });
        });
    }

    private static final RowMapper<Film> FILM_ROW_MAPPER = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
        return film;
    };
    private static final RowMapper<Genre> GENRE_ROW_MAPPER = (rs, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    };
    private static final RowMapper<Mpa> MPA_ROW_MAPPER = (rs, rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        return mpa;
    };

}