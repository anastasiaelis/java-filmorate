package ru.yandex.practicum.filmorate.exception;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ValidationException extends RuntimeException {
    private final String message;
}