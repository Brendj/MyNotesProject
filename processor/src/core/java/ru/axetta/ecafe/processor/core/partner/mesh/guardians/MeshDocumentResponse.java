package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import ru.axetta.ecafe.processor.core.partner.mesh.json.PersonDocument;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeshDocumentResponse extends MeshGuardianResponse {

    private String documentName;
    private int documentTypeId;
    private String series;
    private String number;
    private Long id;
    private String subdivisionCode;
    private String issuer;
    private Date issued;
    private Date expiration;
    private Date createDate;
    private Date lastUpdate;
    private int validationStateId;

    public MeshDocumentResponse() {
        super();
    }

    public MeshDocumentResponse(Integer code, String message) {
        super(code, message);
    }

    public MeshDocumentResponse(PersonDocument personDocument) throws ParseException {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        this.documentTypeId = personDocument.getDocumentTypeId();
        this.series = personDocument.getSeries();
        this.number = personDocument.getNumber();
        this.id = personDocument.getId().longValue();
        this.subdivisionCode = personDocument.getSubdivisionCode();
        this.issuer = personDocument.getIssuer();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (personDocument.getIssued() != null)
            this.issued = df.parse(personDocument.getIssued());
        if (personDocument.getExpiration() != null)
            this.expiration = df.parse(personDocument.getExpiration());
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        if (personDocument.getCreatedAt() != null)
            this.createDate = df2.parse(personDocument.getCreatedAt());
        if (personDocument.getUpdatedAt() != null)
            this.lastUpdate = df2.parse(personDocument.getUpdatedAt());
        if (personDocument.getValidationStateId() != null)
            this.validationStateId = personDocument.getValidationStateId();
        if (personDocument.getDocumentType() != null)
            this.documentName = personDocument.getDocumentType().getName();
    }

    public MeshDocumentResponse okResponse() {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        return this;
    }

    public MeshDocumentResponse internalErrorResponse() {
        this.setCode(INTERNAL_ERROR_CODE);
        this.setMessage(INTERNAL_ERROR_MESSAGE);
        return this;
    }



    /*public DocumentResponse mkErrorResponse() {
        this.setCode(OK_CODE);ClientAuthenticator
        this.setMessage(OK_MESSAGE);
        return this;
    }*/

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubdivisionCode() {
        return subdivisionCode;
    }

    public void setSubdivisionCode(String subdivisionCode) {
        this.subdivisionCode = subdivisionCode;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Date getIssued() {
        return issued;
    }

    public void setIssued(Date issued) {
        this.issued = issued;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getValidationStateId() {
        return validationStateId;
    }

    public void setValidationStateId(int validationStateId) {
        this.validationStateId = validationStateId;
    }
}
