package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequestMapping
public class GenreController {
    private final GenreStorage storage;

    @Autowired
    public GenreController(GenreStorage storage) {
        this.storage = storage;
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenre(@PathVariable @Positive int genreId) {
        return storage.getGenreById(genreId);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return storage.get();
    }
}