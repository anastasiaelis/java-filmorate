package ru.yandex.practicum.filmorate.storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User create(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Пользователь {} был создан", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("Пользователь {} был обновлен", user.getId());
        return user;
    }

    @Override
    public List<User> get() {
        return List.copyOf(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователь с id={} не найден", id);
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        return users.get(id);
    }
}