package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        log.info("получен запрос получения пользователя по ID: {}", id);
        User user1 = userService.getUserById(id);
        if (user1 == null) {
            throw new UserNotFoundException("Не найден ID: " + id);
        }
        log.info("получен пользователь: {}", user1);
        return user1;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("получен запрос получения списка всех пользователей.");
        return userService.get();
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User user1 = userService.create(user);
        if (user1.getName() == null || user.getName().isEmpty()) {
            user1.setName(user.getLogin());
        }
        user1.setId(id++);
        users.put(user1.getId(), user1);
        log.info("Пользователь создан");
        return ResponseEntity.ok(user);
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