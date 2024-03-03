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
        String sqlQuery = "select mpa_id, mpa_name from mpa order by mpa_id";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlQuery = "select mpa_id, mpa_name from mpa where mpa_id = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")), id);
        if (mpas.size() != 1) {
            log.error("Рейтинг с id={} не найден", id);
            throw new MpaNotFoundException(String.format("Рейтинг с id=%d не найден", id));
        }
        return mpas.get(0);
    }
}