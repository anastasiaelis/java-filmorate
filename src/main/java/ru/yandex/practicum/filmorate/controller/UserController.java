package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.service.UserService;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        Validator.validateUser(user);
        log.info("создан пользователь {}", user.getName());
        return service.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        Validator.validateUser(user);
        log.info("обновлен пользователь {}", user.getName());
        return service.update(user);
    }

    @GetMapping
    public List<User> get() {
        log.info("получен запрос на список пользователей");
        return service.get();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        log.info("получен запрос получения пользователя по ид");
        return service.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        service.addToFriendsList(id, friendId);
        log.info("добавлен друг");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        service.deleteFromFriendsList(id, friendId);
        log.info("удален друг");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.info("получен запрос получения списка друзей");
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("получен запрос получения списка общих друзей");
        return service.getCommonFriends(id, otherId);
    }
}