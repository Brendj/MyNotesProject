package ru.iteco.meshsync.error;

public class UnknownCatalogException extends ApplicationException {
    public UnknownCatalogException() {
        super();
    }

    public UnknownCatalogException(String message) {
        super(message);
    }

    public UnknownCatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}
