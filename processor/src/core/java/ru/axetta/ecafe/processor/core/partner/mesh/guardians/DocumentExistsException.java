package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class DocumentExistsException extends Exception {
    public DocumentExistsException() {
        super();
    }

    private Long idOfClient;

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public DocumentExistsException(String message) {
        super(message);
    }

    public DocumentExistsException(String message, Long idOfClient) {
        super(message);
        this.idOfClient = idOfClient;
    }
}
