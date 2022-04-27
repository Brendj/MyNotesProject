package ru.axetta.ecafe.processor.web.internal;

import java.util.List;

public class DocumentResponse extends ResponseItem{

    private List<DocumentItem> documentItems;
    private Long idDocument;

    public DocumentResponse(Long idDocument) {
        this.code = OK;
        this.message = OK_MESSAGE;
        this.idDocument = idDocument;
    }

    public DocumentResponse(List<DocumentItem> documentItems) {
        this.code = OK;
        this.message = OK_MESSAGE;
        this.documentItems = documentItems;
    }
    public DocumentResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.idDocument = null;
    }

    public DocumentResponse() {
    }

    public List<DocumentItem> getDocumentItems() {
        return documentItems;
    }

    public void setDocumentItems(List<DocumentItem> documentItems) {
        this.documentItems = documentItems;
    }

    public Long getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Long idDocument) {
        this.idDocument = idDocument;
    }

    public static class MeshGuardianError extends Exception {
        public MeshGuardianError(String message) {
            super(message);
        }
    }
}

