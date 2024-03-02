package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    List<Film> get();

    Film getFilmById(int id);

    void remove(Film film);

    List<Film> getAll();

    List<Genre> getFilmGenres(Integer filmId);

    Film updateFilmGenres(Film film);

    void addLike(Integer userId, Integer filmId);

    void removeLike(Integer userId, Integer filmId);

    List<Film> getTopLikedFilms(Integer count);

    Mpa getMpa(Integer id);

    List<Mpa> getAllMpa();

    Genre getGenre(Integer id);

    List<Genre> getAllGenres();
}