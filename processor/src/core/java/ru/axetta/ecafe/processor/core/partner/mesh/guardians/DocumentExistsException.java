package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class DocumentExistsException extends Exception {
    public DocumentExistsException() {
        super();
    }

    public DocumentExistsException(String message) {
        super(message);
    }
}
