package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @DisplayName("Действия с пользователями")
    @Test
    public void interactWithUsersTest() {
        User user1 = new User("email1@com", "login1", "name1",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        userStorage.create(user1);
        assertEquals(new User(1, "email1@com", "login1", "name1",
                LocalDate.of(2000, 1, 1), new HashSet<>()), userStorage.getUserById(1));
        User updatedUser = new User(1, "newemail1@com", "newlogin1", "newname1",
                LocalDate.of(2005, 1, 1), new HashSet<>());
        userStorage.update(updatedUser);
        assertEquals(updatedUser, userStorage.getUserById(updatedUser.getId()));
        User user2 = new User("email2@com", "login2", "name2",
                LocalDate.of(2001, 1, 1), new HashSet<>());
        userStorage.create(user2);
        assertEquals(List.of(new User(1, "newemail1@com",
                "newlogin1", "newname1", LocalDate.of(2005, 1, 1),
                new HashSet<>()), new User(2, "email2@com", "login2", "name2",
                LocalDate.of(2001, 1, 1), new HashSet<>())), userStorage.get());
    }

    @DisplayName("Действия с фильмами")
    @Test
    public void interactWithFilmsTest() {
        Film film1 = new Film(1, "name1", "description1", LocalDate.of(2000, 1, 1),
                100, new HashSet<>(), new Mpa(1, "G"), new HashSet<>());
        filmStorage.create(film1);

        assertEquals(new Film(1, "name1", "description1", LocalDate.of(2000, 1, 1),
                100, new HashSet<>(), new Mpa(1, "G"), new HashSet<>()), filmStorage.getFilmById(1));

        Film updatedFilm = new Film(1, "newname1", "newdescription1", LocalDate.of(2001, 1, 1),
                101, new HashSet<>(), new Mpa(2, "PG"), new HashSet<>());
        filmStorage.update(updatedFilm);

        assertEquals(updatedFilm, filmStorage.getFilmById(updatedFilm.getId()));
        Film film2 = new Film(2, "name2", "description2", LocalDate.of(2002, 1, 1),
                102, new HashSet<>(), new Mpa(1, "G"), new HashSet<>());
        filmStorage.create(film2);
        assertEquals(List.of(new Film(1, "newname1", "newdescription1",
                LocalDate.of(2001, 1, 1), 101, new HashSet<>(), new Mpa(2, "PG"),
                new HashSet<>()), new Film(2, "name2", "description2",
                LocalDate.of(2002, 1, 1), 102, new HashSet<>(),
                new Mpa(1, "G"), new HashSet<>())), filmStorage.get());
    }

}