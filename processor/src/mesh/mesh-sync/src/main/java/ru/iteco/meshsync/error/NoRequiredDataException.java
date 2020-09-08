package ru.iteco.meshsync.error;

public class NoRequiredDataException extends ApplicationException {
    public NoRequiredDataException() {
        super();
    }

    public NoRequiredDataException(String message) {
        super(message);
    }

    public NoRequiredDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
