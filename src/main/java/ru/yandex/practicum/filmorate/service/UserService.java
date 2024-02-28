package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage storage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage storage, FriendshipStorage friendshipStorage) {
        this.storage = storage;
        this.friendshipStorage = friendshipStorage;
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User user) {
        return storage.update(user);
    }

    public List<User> get() {
        return storage.get();
    }

    public User getUserById(long id) {
        return storage.getUserById(id);
    }

    //    public void addToFriendsList(long userId1, long userId2) {
//        storage.getUserById(userId2);
//        User user = storage.getUserById(userId1);
//        user.getFriends().add(userId2);
//        log.info("Пользователь {} добавил в друзья пользователя {}", userId1, userId2);
//        storage.update(user);
//    }
//
//    public void deleteFromFriendsList(long userId1, long userId2) {
//        User user = storage.getUserById(userId1);
//        user.getFriends().remove(userId2);
//        log.info("Пользователь {} удалил из друзей пользователя {}", userId1, userId2);
//        storage.update(user);
//    }
//
//    public List<User> getFriends(long id) {
//        List<User> friends = new ArrayList<>();
//        for (Long number : storage.getUserById(id).getFriends()) {
//            friends.add(storage.getUserById(number));
//        }
//        return friends;
//    }
//
//    public List<User> getCommonFriends(long userId1, long userId2) {
//        List<User> friends = new ArrayList<>();
//        Set<Long> result = new java.util.HashSet<>(Set.copyOf(storage.getUserById(userId1).getFriends()));
//        result.retainAll(storage.getUserById(userId2).getFriends());
//        for (Long number : result) {
//            friends.add(storage.getUserById(number));
//        }
//        return friends;
//    }
//@Override
    public List<User> listOfMutualFriends(long id, long otherId) { //вывод списка общих друзей
        getUserById(id);
        getUserById(otherId);
        log.info("Получен список общих друзей пользователей с id {} и id {} ", id, otherId);
        return friendshipStorage.allUsersFriends(id).stream()
                .filter(friendshipStorage.allUsersFriends(otherId)::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    /// @Override
    public Collection<Long> addFriend(long userId, long friendId) {  //добавление в друзья
        getUserById(userId);
        getUserById(friendId);
        boolean userFriendOne = friendshipStorage.allUsersFriends(userId).contains(friendId);
        boolean userFriendTwo = friendshipStorage.allUsersFriends(friendId).contains(userId);
        if (!userFriendOne && !userFriendTwo) {
            friendshipStorage.addFriend(userId, friendId);
        } else if (userFriendOne && userFriendTwo) {
            friendshipStorage.updateFriend(userId, friendId, true);
        } else {
            log.debug("Пользовтель {} уже отправил заявку в друзья пользователю {}." +
                    " Дружба неопределённая. ", userId, friendId);
        }
        log.info("Пользователю с id {}  добавлен друг с id {} ", userId, friendId);
        return friendshipStorage.allUsersFriends(userId);
    }

    //@Override
    public void deleteFriend(long userId, long friendId) {  //удаление из друзей
        getUserById(userId);
        getUserById(friendId);
        boolean isUserFriendOne = friendshipStorage.allUsersFriends(userId).contains(friendId);
        boolean isUserFriendTwo = friendshipStorage.allUsersFriends(friendId).contains(userId);
        if (!isUserFriendOne) {
            log.info("Пользовтель {} никогда не был другом пользователя {}. ", userId, friendId);
            throw new UserNotFoundException("Пользователи никогда не были друзьями" + userId + "," + friendId);
        } else if (!isUserFriendTwo) {
            friendshipStorage.deleteFriend(userId, friendId);
        } else {
            if (!friendshipStorage.updateFriend(userId, friendId, false)) {
                friendshipStorage.deleteFriend(friendId, userId);
                friendshipStorage.deleteFriend(userId, friendId);
            }
        }
    }

//    private void validateBeforeAdd(User user) {
//        if (user.getLogin().contains(" ")) {
//            log.error("Логин не может содержать пробелы");
//            throw new ValidationException("Логин не может содержать пробелы");
//        }
//        for (User user1 : usersList()) {
//            if (user1.getEmail().equals(user.getEmail())) {
//                log.error("Пользователь с email = {} уже существует", user.getEmail());
//                throw new ValidationException(format("Пользователь с email = %s уже существует", user.getEmail()));
//            }
//            if (user1.getLogin().equals(user.getLogin())) {
//                log.error("Пользователь с login = {} уже существует", user.getLogin());
//                throw new ValidationException(format("Пользователь с login = %s уже существует", user.getLogin()));
//            }
//        }
//    }
}