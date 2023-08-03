package ru.practicum.shareit.item.exception;

public class ItemException extends RuntimeException {
    public ItemException() {
        super();
    }

    public ItemException(String message) {
        super(message);
    }

    public ItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
