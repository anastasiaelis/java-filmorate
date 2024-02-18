package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
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
    public User create(@Valid @RequestBody User user) {
        User user1 = userService.create(user);
        if (!user1.getEmail().contains("@")) {
            log.error("Email пользователя пуст или не содержит @");
            throw new ValidationException("Email должен содержать @ и не быть пустым");
        }
        if (user1.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин пользователя пуст или содержит пробелы");
            throw new ValidationException("Логин не должен быть пустым или содержать пробелы");
        }
        if (user1.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, будет использован логин");
        }
        if (user1.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения пользователя в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
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