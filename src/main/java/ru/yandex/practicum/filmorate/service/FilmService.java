package ru.yandex.practicum.filmorate.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;

    private final UserService userService;

    @Autowired
    public FilmService(InMemoryFilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film film) {
        boolean isPresent = false;
        for (Film filmInStorage : storage.get()) {
            if (filmInStorage.getId() == film.getId()) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            log.error("Фильм c id={} не найден", film.getId());
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", film.getId()));
        }
        return storage.update(film);
    }

    public List<Film> get() {
        return storage.get();
    }

    public Film getFilmById(int id) {
        return storage.getFilmById(id);
    }

    public void addLike(int filmId, long userId) {
        userService.getUserById(userId);
        storage.getFilmById(filmId).getLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(int filmId, long userId) {
        userService.getUserById(userId);
        storage.getFilmById(filmId).getLikes().remove(userId);
        log.info("Пользователь {} удалил лайк фильма {}", userId, filmId);
    }

    public List<Film> getPopularMovies(int count) {
        return storage.get()
                .stream()
                .sorted(Comparator.comparing(Film::numberOfLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}