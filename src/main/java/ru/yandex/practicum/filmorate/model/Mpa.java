package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Data
@AllArgsConstructor

public class Mpa {
    @Getter
    @NotBlank
    @NotNull
    @Positive
    private Integer id;
    @NotBlank
    private String name;

    public Mpa() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mpa)) return false;
        Mpa mpa = (Mpa) o;
        return id.equals(mpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}