package ru.practicum.shareit.error.exceptions;

import java.util.function.Supplier;

public class IncorrectParameterException extends Exception implements Supplier<IncorrectParameterException> {
    public IncorrectParameterException(String message) {
        super(message);
    }

    @Override
    public IncorrectParameterException get() {
        return new IncorrectParameterException(getMessage());
    }
}
