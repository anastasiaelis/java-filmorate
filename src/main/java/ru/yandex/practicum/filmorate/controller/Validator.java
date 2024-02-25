package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class Validator {
    public static void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза фильма раньше допустимого порога");
            throw new ValidationException("Дата релиза не должна быть ранее 28.12.1895");
        }
    }

    public static void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()||user.getName().contains(" ")||user.getLogin().contains(" ")) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, будет использован логин");
        }
    }
}