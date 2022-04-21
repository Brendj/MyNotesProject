package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import ru.axetta.ecafe.processor.core.partner.mesh.json.PersonDocument;

public class DocumentResponse extends MeshGuardianResponse {
    private int documentTypeId;
    private String series;
    private String number;

    public DocumentResponse() {
        super();
    }

    public DocumentResponse(Integer code, String message) {
        super(code, message);
    }

    public DocumentResponse(PersonDocument personDocument) {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        this.documentTypeId = personDocument.getDocumentTypeId();
        this.series = personDocument.getSeries();
        this.number = personDocument.getNumber();
    }

    /*public DocumentResponse okResponse() {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        return this;
    }*/

    public DocumentResponse internalErrorResponse() {
        this.setCode(INTERNAL_ERROR_CODE);
        this.setMessage(INTERNAL_ERROR_MESSAGE);
        return this;
    }

    /*public DocumentResponse mkErrorResponse() {
        this.setCode(OK_CODE);ClientAuthenticator
        this.setMessage(OK_MESSAGE);
        return this;
    }*/

    public int getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(int documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
