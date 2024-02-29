package ru.yandex.practicum.filmorate.controller;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaStorage storage;

    @Autowired
    public MpaController(MpaStorage storage) {
        this.storage = storage;
    }

    @GetMapping
    public List<Mpa> get() {
        return storage.get();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return storage.getMpaById(id);
    }
}