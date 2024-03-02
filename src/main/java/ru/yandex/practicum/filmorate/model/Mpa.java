package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor

public class Mpa {
    @NotBlank
    @NotNull

    private int id;
    private String name;

    public Mpa() {

    }
}