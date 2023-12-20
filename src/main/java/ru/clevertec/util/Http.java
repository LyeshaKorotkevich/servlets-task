package ru.clevertec.util;

import lombok.Getter;

@Getter
public enum Http {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    NOT_FOUND(404);

    private final int code;

    Http(int code) {
        this.code = code;
    }
}
