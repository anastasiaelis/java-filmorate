package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.controller.FilmController;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class FilmControllerTest {
    FilmController filmController;
        Film film = Film.builder()
                .name("film name")
                .description("film description")
                .releaseDate(LocalDate.of(1997, 3, 24))
                .duration(100)
                .build();

        @BeforeEach
        void beforeEach() {
            InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
            InMemoryUserStorage userStorage = new InMemoryUserStorage();
            UserService userService = new UserService(userStorage);

            FilmService filmService = new FilmService(filmStorage, userService);
            filmController = new  FilmController(filmService);
        }

        @Test
        void createStandart() {
            filmController.create(film);
            assertEquals(List.of(film).toArray().length, 1);
        }

        @Test
        void createFailDate() {
            Film film2 = Film.builder()
                    .name("film name")
                    .description("film description")
                    .releaseDate(LocalDate.of(1097, 3, 24))
                    .duration(100)
                    .build();
            Throwable exception = assertThrows(ValidationException.class, () -> filmController.create(film2));
            assertEquals(exception.getMessage(), "Дата релиза не должна быть ранее 28.12.1895");
        }
}