package ru.practicum.shareit.error.exceptions;

import java.util.function.Supplier;

public class NotFoundParameterException extends Exception implements Supplier<NotFoundParameterException> {
    public NotFoundParameterException(String message) {
        super(message);
    }

    @Override
    public NotFoundParameterException get() {
        return new NotFoundParameterException(getMessage());
    }
}
