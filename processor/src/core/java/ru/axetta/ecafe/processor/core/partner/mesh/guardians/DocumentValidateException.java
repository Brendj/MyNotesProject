package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class DocumentValidateException extends Exception {

    private Long documentTypeId;


    public DocumentValidateException(String message, Long documentId) {
        super(message);
        this.documentTypeId = documentId;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }
}
