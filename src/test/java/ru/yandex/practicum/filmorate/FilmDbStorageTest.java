package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

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
        // Подготавливаем данные для теста
        Mpa apm = new Mpa(5, "NC-17");
        Film newFilm = new Film(1, "useremail.ru", "vanya123", LocalDate.of(1990, 1, 1), 100, new HashSet<>(), apm, new ArrayList<>());

        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        List<Genre> c5 = filmStorage.getAllGenres();

        filmStorage.create(newFilm);
        Film upFilm = new Film(1, "", "vanya123", LocalDate.of(1990, 1, 1), 100, new HashSet<>(), apm, new ArrayList<>());
        Film upFilm2 = new Film(2, "film2", "vmm3", LocalDate.of(1990, 1, 1), 100, new HashSet<>(), apm, new ArrayList<>());
        Film upFilm3 = new Film(0, "film5", "vya123", LocalDate.of(1990, 1, 1), 100, new HashSet<>(), apm, new ArrayList<>());
        filmStorage.create(upFilm2);
        filmStorage.create(upFilm3);
        // вызываем тестируемый метод
        Film savedFilm = filmStorage.getFilmById(1);
        filmStorage.update(upFilm);
        filmStorage.getTopLikedFilms(1);
        List<Film> ccc = filmStorage.get();
        filmStorage.getFilmById(1);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }
}
