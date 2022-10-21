package ru.axetta.ecafe.processor.web.internal;

import java.util.List;

public class DocumentResponse extends ResponseItem{

    private List<DocumentItem> documentItems;

    public DocumentResponse(List<DocumentItem> documentItems) {
        this.code = OK;
        this.message = OK_MESSAGE;
        this.documentItems = documentItems;
    }
    public DocumentResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public DocumentResponse() {
    }

    public List<DocumentItem> getDocumentItems() {
        return documentItems;
    }

    public void setDocumentItems(List<DocumentItem> documentItems) {
        this.documentItems = documentItems;
    }

    public static class MeshGuardianError extends Exception {
        public MeshGuardianError(String message) {
            super(message);
        }
    }
}

