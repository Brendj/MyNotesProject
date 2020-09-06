package ru.iteco.meshsync.error;

public class EducationNotFoundException extends ApplicationException {
    public EducationNotFoundException() {
        super();
    }

    public EducationNotFoundException(String message) {
        super(message);
    }

    public EducationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
