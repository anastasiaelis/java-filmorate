package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.practicum.filmorate.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;
    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        Validator.validateFilm(film);
        log.info("Создан фильм: {}", film.getName());
        return service.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        Validator.validateFilm(film);
        log.info("Обновлен фильм: {}", film.getName());
        return service.update(film);
    }

    @GetMapping
    public List<Film> get() {
        log.info("получен запрос получения списка фильмов");
        return service.get();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("получен запрос получения фильма по ИД");
        return service.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int filmId, @PathVariable long userId) {
        log.info("поставлен лайк");
        service.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") int filmId, @PathVariable long userId) {
        log.info("удален лайк");
        service.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularMovies(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("получен запрос на список популярных фильмов");
        return service.getPopularMovies(count);
    }
}