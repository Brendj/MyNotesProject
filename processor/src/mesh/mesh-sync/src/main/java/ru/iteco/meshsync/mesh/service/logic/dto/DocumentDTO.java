package ru.iteco.meshsync.mesh.service.logic.dto;

import ru.iteco.client.model.PersonDocument;

import java.io.Serializable;
import java.util.Date;

public class DocumentDTO implements Serializable {
    private Integer documentType;
    private String series;
    private String number;
    private Long idMKDocument;
    private Date issuedDate;
    private String issuer;

    public static DocumentDTO build(PersonDocument pd) throws Exception {
        DocumentDTO d = new DocumentDTO();

        d.documentType = pd.getDocumentTypeId();
        d.series = pd.getSeries();
        d.number = pd.getNumber();
        d.idMKDocument = pd.getId();
        d.setIssuer(pd.getIssuer());
        d.setIssuedDate(DateUtils.parseSimpleDate(pd.getIssued().toString()));

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
}
