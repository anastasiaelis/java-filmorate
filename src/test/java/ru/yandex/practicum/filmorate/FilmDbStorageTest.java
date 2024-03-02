package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmById() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1), new HashSet<>());
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(newUser);
        User newUser2 = new User(2, "udser@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1), new HashSet<>());
        userStorage.create(newUser2);
        Mpa apm = new Mpa(1, "G");
        Film newFilm = new Film(1, "useremail.ru", "vanya123", LocalDate.of(1990, 1, 1), 100, new ArrayList<>(), apm);

        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        List<Mpa> c5 = filmStorage.getAllMpa();
        filmStorage.create(newFilm);
        Film upFilm = new Film(1, "", "vanya123", LocalDate.of(1990, 1, 1), 190, new ArrayList<>(), apm);
        Film upFilm2 = new Film(2, "film2", "vmm3", LocalDate.of(1990, 1, 1), 100, new ArrayList<>(), apm);
        Film upFilm3 = new Film(0, "film5", "vya123", LocalDate.of(1990, 1, 1), 100, new ArrayList<>(), apm);
        filmStorage.create(upFilm2);
        filmStorage.create(upFilm3);
        // вызываем тестируемый метод
        filmStorage.update(upFilm);
        Film savedFilm = filmStorage.getFilmById(1);

        filmStorage.getTopLikedFilms(1);

        filmStorage.getFilmById(1);

        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 3);
        filmStorage.addLike(2, 2);
        //filmStorage.addLike(2,1);
        List<Film> ccc = filmStorage.getTopLikedFilms(3);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(upFilm);        // и сохраненного пользователя - совпадают
    }
}
