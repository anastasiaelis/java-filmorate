package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequestMapping
public class MpaController {
    private final MpaStorage storage;

    @Autowired
    public MpaController(MpaStorage storage) {
        this.storage = storage;
    }

    @GetMapping("/mpa/{mpaId}")
    public Mpa getMpa(@PathVariable @Positive int mpaId) {
        return storage.getMpaById(mpaId);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        return storage.get();
    }
}