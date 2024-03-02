package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Genre {

    private int film_id;
    private int genre_id;
    private String  genre_name;


    public Genre() {

    }
}