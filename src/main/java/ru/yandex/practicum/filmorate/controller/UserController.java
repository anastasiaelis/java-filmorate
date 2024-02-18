package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        log.info("получен запрос получения пользователя по ID: {}", id);
        User user1 = userService.getUserById(id);
        log.info("получен пользователь: {}", user1);
        return user1;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("получен запрос получения списка всех пользователей.");
        return userService.get();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        User user1 = userService.create(user);
        log.info("Пользователь создан {}", user1.getName());
        return user1;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User user1 = userService.update(user);
        log.info("Пользователь обновлен {}", user1.getName());
        return user1;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Integer friendId) {
        userService.addToFriendsList(id, friendId);
        log.info("Пользователю обновлен друг {}", id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFromFriendsList(id, friendId);
        log.info("Пользователю удален друг {}", id);
    }

    @GetMapping("{id}/friends")
    public List<User> getAllFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @ExceptionHandler
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка");
    }

}