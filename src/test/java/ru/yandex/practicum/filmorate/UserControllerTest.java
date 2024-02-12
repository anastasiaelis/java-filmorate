package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    UserController userController;
    User user = new User("right@", "log in", LocalDate.of(2000, 1, 1));
    User user2 = new User("", "", LocalDate.of(2000, 1, 1));

    @BeforeEach
    void beforeEach() {

        userController = new UserController();
    }

    @Test
    void createStandart() {
        userController.create(user);
        assertEquals(List.of(user).toArray().length, 1);
    }

    @Test
    void createFailLogin() {
        ///user.setName("");
        //userController.create(user2);
        //assertEquals(user2.getEmail(), user2.getEmail());
        Throwable exception = assertThrows(ValidationException.class, () -> userController.create(user2));
        assertEquals(exception.getMessage(), "Дата не может быть ранее 1895-12-28");
        System.out.println(user2.getEmail());
    }
}
