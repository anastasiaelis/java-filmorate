package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor

public class FilmController {
    private final FilmService filmService;
    public static final String DEFAULT_VALUE_COUNT = "10";
    public static final LocalDate DATELATEST = LocalDate.of(1895, 12, 28);

    @GetMapping("/{id}")

    @ResponseStatus(HttpStatus.OK)
    public Film findById(@PathVariable Integer id) {
        log.info("получен запрос получения фильма по ID: {}", id);
        Film film1 = filmService.getFilmById(id);
        log.info("получен фильм: {}", film1);
        if (film1 == null) {
            throw new FilmNotFoundException("Не найден ID: " + id);
        }
        return film1;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> findAll() {
        log.info("получен запрос получения списка всех фильмов.");
        List<Film> allFilms = filmService.get();
        log.info("получен список всех фильмов, кол-во: {}", allFilms.size());
        return allFilms;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.info("получен запрос создания фильма: {}", film.getName());
        filmService.create(film);
        if (film.getReleaseDate().isBefore(DATELATEST)) {
            throw new ValidationException("Дата не может быть раньше " + DATELATEST, BAD_REQUEST);
        }
        log.info("Фильм добавлен");
        return film;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("получен запрос обновления фильма: {}", film.getName());
        filmService.update(film);
        log.info("Фильм обновлен");
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularMovies(@RequestParam(defaultValue = DEFAULT_VALUE_COUNT) Integer count) {
        return filmService.getPopularMovies(count);
    }
}