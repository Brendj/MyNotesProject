package ru.iteco.meshsync.mesh.service.logic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.iteco.client.model.PersonDocument;

import java.io.Serializable;
import java.util.Date;

public class DocumentDTO implements Serializable {
    private Integer documentType;
    private String series;
    private String number;
    private Long idMKDocument;
    private String subdivisionCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
    private Date expiration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
    private Date issuedDate;

    private String issuer;

    public static DocumentDTO build(PersonDocument pd) throws Exception {
        DocumentDTO d = new DocumentDTO();

        d.documentType = pd.getDocumentTypeId();
        d.series = pd.getSeries();
        d.number = pd.getNumber();
        d.idMKDocument = pd.getId();
        if(pd.getIssuer() != null) {
            d.setIssuer(pd.getIssuer());
        }
        if(pd.getIssued() != null) {
            d.setIssuedDate(DateUtils.parseSimpleDate(pd.getIssued().toString()));
        }
        if(pd.getSubdivisionCode() != null) {
            d.setSubdivisionCode(pd.getSubdivisionCode());
        }
        if(pd.getExpiration() != null) {
            d.setExpiration(DateUtils.parseSimpleDate(pd.getExpiration().toString()));
        }

        return d;
    }

    public Integer getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
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

    public Long getIdMKDocument() {
        return idMKDocument;
    }

    public void setIdMKDocument(Long idMKDocument) {
        this.idMKDocument = idMKDocument;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubdivisionCode() {
        return subdivisionCode;
    }

    public void setSubdivisionCode(String subdivisionCode) {
        this.subdivisionCode = subdivisionCode;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
