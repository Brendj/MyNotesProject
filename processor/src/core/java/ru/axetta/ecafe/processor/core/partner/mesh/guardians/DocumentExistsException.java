package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class DocumentExistsException extends Exception {
    public DocumentExistsException() {
        super();
    }

    private Long idOfClient;
    private Long documentTypeId;

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public DocumentExistsException(String message) {
        super(message);
    }

    public DocumentExistsException(String message, Long idOfClient, Long documentTypeId) {
        super(message);
        this.idOfClient = idOfClient;
        this.documentTypeId = documentTypeId;
    }
}
