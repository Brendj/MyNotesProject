package ru.axetta.ecafe.processor.web.internal;

public class DocumentDeleteResponse extends ResponseItem{

    public DocumentDeleteResponse okResponse() {
        this.code = OK;
        this.message = OK_MESSAGE;
        return this;
    }
    public DocumentDeleteResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public DocumentDeleteResponse() {
    }

}

