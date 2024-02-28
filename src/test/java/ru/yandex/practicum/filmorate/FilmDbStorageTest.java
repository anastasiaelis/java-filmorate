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
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmById() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(5,"useremailru" ,"loginnnbn", LocalDate.of(1990, 1, 1),2,new HashSet<Long>(),new Mpa(5,"G"),new HashSet<Genre>());
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
       // Film newFilm2 = new Film(54,"use55remailru" ,"loginnnbn", LocalDate.of(1990, 1, 1),2,new HashSet<Long>(),new Mpa(1,"G"),new HashSet<Genre>());
        //FilmDbStorage filmStorage2 = new FilmDbStorage(jdbcTemplate);
        // вызываем тестируемый метод
        Film savedFilm = filmStorage.getFilmById(5);
       // Film savedFilm2 = filmStorage2.getFilmById(2);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }

   /* @Test
    public void testUpdateFilmId() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(1,"useremailru" ,"loginnnbn", LocalDate.of(1990, 1, 1),2,new HashSet<Long>(),new Mpa(1,"G"),new HashSet<Genre>());
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        filmStorage.create(newFilm);

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.getFilmById(1);
        Film updatedFilm = new Film(1,"useremailB" ,"loginnnkjkmjnjbn", LocalDate.of(1990, 1, 1),2,new HashSet<Long>(),new Mpa(1,"G"),new HashSet<Genre>());
        filmStorage.update(updatedFilm);
        Film savedUpdatedFilm = filmStorage.getFilmById(1);

        // проверяем утверждения
        assertThat(savedUpdatedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(updatedFilm);        // и сохраненного пользователя - совпадают
    }      */
}
