package ru.yandex.practicum.filmorate.model;
//import jdk.jfr.DataAmount;
import lombok.Builder;
import lombok.Data;

//import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data

@Builder

public class Film {
    private int id;
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    private Set<Long> likes = new HashSet<>();

    public int numberOfLikes() {
        return likes.size();
    }

}