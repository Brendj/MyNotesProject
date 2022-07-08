package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class DocumentIdResponse {

    private Integer documentId;
    private Integer documentType;

    public DocumentIdResponse(Integer documentId, Integer documentType) {
        this.documentId = documentId;
        this.documentType = documentType;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public Integer getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
    }
}
