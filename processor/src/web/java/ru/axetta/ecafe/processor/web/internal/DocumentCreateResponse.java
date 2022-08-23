package ru.axetta.ecafe.processor.web.internal;

import java.util.List;

public class DocumentCreateResponse extends ResponseItem{

    private Long idDocument;

    public DocumentCreateResponse(Long idDocument) {
        this.code = OK;
        this.message = OK_MESSAGE;
        this.idDocument = idDocument;
    }
    public DocumentCreateResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public DocumentCreateResponse() {
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

