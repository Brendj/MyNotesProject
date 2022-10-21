package ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class DocumentInfo {
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
