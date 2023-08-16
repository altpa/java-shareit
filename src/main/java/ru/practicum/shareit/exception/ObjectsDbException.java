package ru.practicum.shareit.exception;

public class ObjectsDbException extends RuntimeException {
    public ObjectsDbException() {
        super();
    }

    public ObjectsDbException(String message) {
        super(message);
    }

    public ObjectsDbException(String message, Throwable cause) {
        super(message, cause);
    }
}
