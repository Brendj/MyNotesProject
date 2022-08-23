package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.partner.mesh.guardians.PersonResponse;

public class DocumentUpdateResponse extends ResponseItem{

    public DocumentUpdateResponse okResponse() {
        this.code = OK;
        this.message = OK_MESSAGE;
        return this;
    }
    public DocumentUpdateResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public DocumentUpdateResponse() {
    }

}

