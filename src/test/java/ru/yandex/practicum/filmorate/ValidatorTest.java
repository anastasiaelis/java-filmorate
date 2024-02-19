package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Film со слишком ранней датой")
    @Test
    public void createFilmWithTooEarlyReleaseDate() {
        Film film = new Film("name", "description", LocalDate.of(1000, 1, 1), 120);
        ValidationException e = assertThrows(ValidationException.class, () -> Validator.validateFilm(film));
        assertEquals("Дата релиза не должна быть ранее 28.12.1895", e.getMessage());
    }

    @DisplayName("User с пустым именем")
    @Test
    public void createUserWithEmptyName() {
        User user = new User("right@", "login", LocalDate.of(2000, 1, 1));
        Validator.validateUser(user);
        assertEquals("login", user.getName());
    }
}
