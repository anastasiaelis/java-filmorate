package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    private final int duration;
    private Set<Long> likes = new HashSet<>();
    private final String name;
    private final String description;
    private final LocalDate releaseDate;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public int numberOfLikes() {
        return likes.size();
    }
}