package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> get() {
        String sqlQuery = "select id, name from genre order by id";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "select id, name from genre where id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")), id);
        if (genres.size() != 1) {
            log.error("Жанр с id={} не найден", id);
            throw new GenreNotFoundException(String.format("Жанр с id=%d не найден", id));
        }
        return genres.get(0);
    }
}