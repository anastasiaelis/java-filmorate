package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class Validator {


    public static void validateFilm(Film film) {
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
    }

    public static void validateUser(User user) {
        if (!user.getEmail().contains("@")) {
            log.error("Email пользователя пуст или не содержит @");
            throw new ValidationException("Email должен содержать @ и не быть пустым");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин пользователя пуст или содержит пробелы");
            throw new ValidationException("Логин не должен быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, будет использован логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения пользователя в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

  /*  public static void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза фильма раньше допустимого порога");
            throw new ValidationException("Дата релиза не должна быть ранее 28.12.1895");
        }
    }

    public static void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank() ||
                user.getName().contains(" ") || user.getLogin().contains(" ")) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, будет использован логин");
        }
    }*/
}