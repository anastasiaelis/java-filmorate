package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
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
    public List<Film> findAll() {
        log.info("получен запрос получения списка всех фильмов.");
        List<Film> allFilms = filmService.get();
        log.info("получен список всех фильмов, кол-во: {}", allFilms.size());
        return allFilms;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("получен запрос создания фильма: {}", film.getName());
        filmService.create(film);
        if (film.getReleaseDate().isBefore(DATELATEST)) {
            throw new ValidationException("Дата не может быть раньше " + DATELATEST);
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Превышена максимальная длина описания фильма");
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза фильма раньше допустимого порога");
            throw new ValidationException("Дата релиза не должна быть ранее 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма - не положительное число");
            throw new ValidationException("Продолжительность фильма должна быть положительна");
        }
        log.info("Фильм добавлен");
        return film;
    }

    @PutMapping
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
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularMovies(@RequestParam(defaultValue = DEFAULT_VALUE_COUNT) Integer count) {
        return filmService.getPopularMovies(count);
    }

    @ExceptionHandler

    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка");
    }

}