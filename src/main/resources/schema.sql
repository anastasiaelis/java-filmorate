CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL,
    login VARCHAR NOT NULL,
    name VARCHAR,
    birthday DATE
);
CREATE TABLE IF NOT EXISTS genre (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name VARCHAR NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS film (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name VARCHAR NOT NULL,
    description VARCHAR,
    release_date DATE,
    duration INTEGER,
    mpa_id INTEGER REFERENCES mpa (mpa_id)
);
CREATE TABLE IF NOT EXISTS film_genre (
    film_id INTEGER REFERENCES film (film_id),
    id INTEGER REFERENCES genre (id),
    PRIMARY KEY(film_id, id)
);
CREATE TABLE IF NOT EXISTS film_like (
    film_id INTEGER REFERENCES film (film_id),
    user_id INTEGER REFERENCES users (user_id),
    PRIMARY KEY(film_id, user_id)
);
create table IF NOT EXISTS friendship
(
    user_id   INTEGER NOT NULL
    references users (user_id) ON DELETE CASCADE,
    friend_id INTEGER not null
    references users (user_id) ON DELETE CASCADE,
    STATUS    BOOLEAN not null,
    primary key (user_id, friend_id)
    );
