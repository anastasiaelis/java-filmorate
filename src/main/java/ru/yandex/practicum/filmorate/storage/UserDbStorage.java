package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).longValue();
        log.info("Пользователь {} был создан", id);
        return getUserById(id);
    }

    @Override
    public User update(User user) {
        getUserById(user.getId());
        String sqlQuery = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());
        sqlQuery = "delete from friends where friend_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            sqlQuery = "insert into friend (user_id, friend_id) values (?, ?)";
            for (Long id : user.getFriends()) {
                jdbcTemplate.update(sqlQuery, id, user.getId());
            }
        }
        log.info("Пользователь {} был обновлен", user.getId());
        return getUserById(user.getId());
    }

    // @Override

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if ((user != null) && (friend != null)) {
            boolean status = false;
            if (friend.getFriends().contains(userId)) {
                status = true;  // дружба стала взаимной
                String sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                        "WHERE user_id = ? AND friend_id = ?";
                jdbcTemplate.update(sql, friendId, userId, true, friendId, userId);
            }
            String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, userId, friendId, status);
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if ((user != null) && (friend != null)) {
            String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId);
            if (friend.getFriends().contains(userId)) {
                // дружба стала невзаимной - нужно поменять статус
                sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                        "WHERE user_id = ? AND friend_id = ?";
                jdbcTemplate.update(sql, friendId, userId, false, friendId, userId);
            }
        }
    }

    public List<User> getFriends(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            String sql = "SELECT friend_id, email, login, name, birthday FROM friends" +
                    " INNER JOIN users ON friends.friend_id = users.id WHERE friends.user_id = ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                            rs.getLong("friend_id"),
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getDate("birthday").toLocalDate(),
                            null),
                    userId
            );
        } else {
            return null;
        }
    }


    @Override
    public List<User> get() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public User getUserById(long id) {
        String sqlQuery = "select * from users where id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToUser(rs), id);
        if (users.size() != 1) {
            log.error("Пользователь c id={} не найден", id);
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        return users.get(0);
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        String sqlQuery = "select user_id from friends where friend_id = ?";
        Set<Long> friends = new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Long.class, rs.getInt("id")));
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(friends)
                .build();
    }
}