package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage storage;

    @Autowired
    public FilmController(FilmStorage storage) {
        this.storage = storage;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        Validator.validateFilm(film);
        return storage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        Validator.validateFilm(film);
        return storage.update(film);
    }

    @GetMapping
    public List<Film> get() {
        return storage.get();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return storage.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        storage.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        storage.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularMovies(@RequestParam(required = false, defaultValue = "2") int count) {
        return storage.getTopLikedFilms(count);
    }
}