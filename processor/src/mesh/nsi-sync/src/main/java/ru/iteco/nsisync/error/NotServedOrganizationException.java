package ru.iteco.nsisync.error;

public class NotServedOrganizationException extends ApplicationException {
    public NotServedOrganizationException() {
        super();
    }

    public NotServedOrganizationException(String message) {
        super(message);
    }

    public NotServedOrganizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
