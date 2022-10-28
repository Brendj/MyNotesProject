package ru.axetta.ecafe.processor.web.internal;

import java.util.List;

public class DocumentResponse extends ResponseItem{

    private List<DocumentResponseItem> documentItems;

    public DocumentResponse(List<DocumentResponseItem> documentItems) {
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

    public List<DocumentResponseItem> getDocumentItems() {
        return documentItems;
    }

    public void setDocumentItems(List<DocumentResponseItem> documentItems) {
        this.documentItems = documentItems;
    }

    public static class MeshGuardianError extends Exception {
        public MeshGuardianError(String message) {
            super(message);
        }
    }
}

