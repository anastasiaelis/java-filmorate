package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ValidatorTest {

    @Test
    public void createFilmWithNoName() {
        Film film = new Film(null, "description", LocalDate.of(2000, 1, 1), 120);
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
    }

    @Test
    public void createFilmWithTooBigDescription() {
        Film film = new Film("name", "description".repeat(50), LocalDate.of(2000, 1, 1), 120);
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Описание фильма не может превышать 200 символов", e.getMessage());
    }

    @Test
    public void createFilmWithTooEarlyReleaseDate() {
        Film film = new Film("name", "description", LocalDate.of(1000, 1, 1), 120);
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Дата релиза не должна быть ранее 28.12.1895", e.getMessage());
    }

    @Test
    public void createFilmWithoutPositiveDuration() {
        Film film = new Film("name", "description", LocalDate.of(2000, 1, 1), -1);
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Продолжительность фильма должна быть больше нуля", e.getMessage());
    }

    @Test
    public void createUserWithInvalidEmail() {
        User user = new User("wrong", "login", LocalDate.of(2000, 1, 1));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateUser(user));
        assertEquals("Email должен содержать @ и не быть пустым", e.getMessage());
    }

    @Test
    public void createUserWithInvalidLogin() {
        User user = new User("right@", "log in", LocalDate.of(2000, 1, 1));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateUser(user));
        assertEquals("Логин не должен быть пустым или содержать пробелы", e.getMessage());
    }

    @Test
    public void createUserWithEmptyName() {
        User user = new User("right@", "login", LocalDate.of(2000, 1, 1));
        Validator.validateUser(user);
        assertEquals("login", user.getLogin());
    }

    @Test
    public void createUserWithInvalidBirthday() {
        User user = new User("right@", "login", LocalDate.of(3000, 1, 1));
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateUser(user));
        assertEquals("Дата рождения не может быть в будущем", e.getMessage());
    }
}
