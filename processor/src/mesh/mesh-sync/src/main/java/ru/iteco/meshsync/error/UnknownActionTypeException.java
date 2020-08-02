package ru.iteco.meshsync.error;

public class UnknownActionTypeException extends ApplicationException {
    public UnknownActionTypeException() {
        super();
    }

    public UnknownActionTypeException(String message) {
        super(message);
    }

    public UnknownActionTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
