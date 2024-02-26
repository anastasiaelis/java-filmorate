package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class validatorClass {

    @DisplayName("Film без имени")
    @Test
    public void createFilmWithNoName() {
        Film film = new Film(null, "description", LocalDate.of(2000, 1, 1),
                120, new HashSet<>(), new Mpa(1, "Комедия"), new HashSet<>());
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
    }

    @DisplayName("Film с длинным описанием")
    @Test
    public void createFilmWithTooBigDescription() {
        Film film = new Film("name", "description".repeat(50), LocalDate.of(2000, 1, 1),
                120, new HashSet<>(), new Mpa(1, "Комедия"), new HashSet<>());
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Описание фильма не может превышать 200 символов", e.getMessage());
    }

    @DisplayName("Film со слишком ранней датой")
    @Test
    public void createFilmWithTooEarlyReleaseDate() {
        Film film = new Film("name", "description", LocalDate.of(1000, 1, 1),
                120, new HashSet<>(), new Mpa(1, "Комедия"), new HashSet<>());
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Дата релиза не должна быть ранее 28.12.1895", e.getMessage());
    }

    @DisplayName("Film с неположительной продолжительностью")
    @Test
    public void createFilmWithoutPositiveDuration() {
        Film film = new Film("name", "description", LocalDate.of(2000, 1, 1),
                -1, new HashSet<>(), new Mpa(1, "Комедия"), new HashSet<>());
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Продолжительность фильма должна быть положительна", e.getMessage());
    }

    @DisplayName("User с некорректным email")
    @Test
    public void createUserWithInvalidEmail() {
        User user = new User("wrong", "login", "name",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateUser(user));
        assertEquals("Email должен содержать @ и не быть пустым", e.getMessage());
    }

    @DisplayName("User с некорректным логином")
    @Test
    public void createUserWithInvalidLogin() {
        User user = new User("right@email.com", "log in", "name",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateUser(user));
        assertEquals("Логин не должен быть пустым или содержать пробелы", e.getMessage());
    }

    @DisplayName("User с пустым именем")
    @Test
    public void createUserWithEmptyName() {
        User user = new User("right@email.com", "login", null,
                LocalDate.of(2000, 1, 1), new HashSet<>());
        Validator.validateUser(user);
        assertEquals("login", user.getName());
    }

    @DisplayName("User с некорректной датой рождения")
    @Test
    public void createUserWithInvalidBirthday() {
        User user = new User("right@email.com", "login", "name",
                LocalDate.of(3000, 1, 1), new HashSet<>());
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateUser(user));
        assertEquals("Дата рождения не может быть в будущем", e.getMessage());
    }
}