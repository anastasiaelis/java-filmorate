package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public Film create(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Фильм {} был добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.info("Фильм {} был обновлен", film.getName());
        return film;
    }

    @Override
    public List<Film> get() {
        return List.copyOf(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            log.error("Фильм с id={} не найден", id);
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", id));
        }
        return films.get(id);
    }
}