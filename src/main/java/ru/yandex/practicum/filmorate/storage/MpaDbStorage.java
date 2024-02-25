package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> get() {
        String sqlQuery = "select id, name from mpa order by id";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Mpa(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlQuery = "select id, name from mpa where id = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Mpa(rs.getInt("id"), rs.getString("name")), id);
        if (mpas.size() != 1) {
            log.error("Рейтинг с id={} не найден", id);
            throw new MpaNotFoundException(String.format("Рейтинг с id=%d не найден", id));
        }
        return mpas.get(0);
    }
}