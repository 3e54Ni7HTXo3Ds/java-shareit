package ru.practicum.shareit.error.exceptions;

import java.util.function.Supplier;

public class UpdateException extends Exception implements Supplier<UpdateException> {
    public UpdateException(String message) {
        super(message);
    }

    @Override
    public UpdateException get() {
        return  new UpdateException(getMessage());
    }
}
