package ru.yandex.practicum.filmorate.model;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Validated
public class Film implements Serializable {
    @Positive
    private int id;

    @NotBlank(message = "Film name is either empty or null.")
    private String name;

    @NotNull
    @Size(max = 200, message = "Film description is longer than 200 chars.")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Film duration is negative.")
    private int duration;
    private List<Genre> genres;

    private Mpa mpa;

    private Set<Integer> likes = new HashSet<>();

    public Film() {
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, List<Genre> genres, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }

    public Film(String name, String description, LocalDate releaseDate, int duration, List<Genre> genres, Mpa mpa) {
        this(name, description, releaseDate, duration, mpa);
        this.genres = genres;
    }

    public Film(String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this(name, description, releaseDate, duration);
        this.mpa = mpa;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this(name, description, releaseDate, duration);
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id && duration == film.duration && name.equals(film.name) &&
                description.equals(film.description) && releaseDate.equals(film.releaseDate) &&
                Objects.equals(likes, film.likes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, releaseDate, duration, likes);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Set<Integer> getLikes() {
        return likes;
    }

    public void setLikes(Set<Integer> likes) {
        this.likes = likes;
    }

    public void addLike(Integer userId) {
        likes.add(userId);
    }

    public void removeLike(Integer userId) {
        likes.remove(userId);
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Mpa getMpa() {
        return mpa;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setMpa(Mpa mpa) {
        this.mpa = mpa;
    }


    public int numberOfLikes() {
        return likes.size();
    }
}