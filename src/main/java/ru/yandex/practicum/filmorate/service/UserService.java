package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User user) {
        if (getUserById(user.getId()) == null) {
            log.error("Пользователь c id={} не найден", user.getId());
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", user.getId()));
        }
        return storage.update(user);
    }

    public List<User> get() {
        return storage.get();
    }

    public User getUserById(long id) {
        return storage.getUserById(id);
    }

    public void addToFriendsList(long userId1, long userId2) {
        storage.getUserById(userId2);
        storage.getUserById(userId1).getFriends().add(userId2);
        storage.getUserById(userId2).getFriends().add(userId1);
        log.info("Пользователи {} и {} теперь друзья", userId1, userId2);
    }

    public void deleteFromFriendsList(long userId1, long userId2) {
        storage.getUserById(userId2).getFriends().remove(userId1);
        storage.getUserById(userId1).getFriends().remove(userId2);
        log.info("Пользователи {} и {} больше не друзья", userId1, userId2);
    }

    public List<User> getFriends(long id) {
        List<User> friends = new ArrayList<>();
        for (Long number : storage.getUserById(id).getFriends()) {
            friends.add(storage.getUserById(number));
        }
        return friends;
    }

    public List<User> getCommonFriends(long userId1, long userId2) {
        List<User> friends = new ArrayList<>();
        Set<Long> result = new java.util.HashSet<>(Set.copyOf(storage.getUserById(userId1).getFriends()));
        result.retainAll(storage.getUserById(userId2).getFriends());
        for (Long number : result) {
            friends.add(storage.getUserById(number));
        }
        return friends;
    }
}