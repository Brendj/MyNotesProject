package ru.axetta.ecafe.processor.core.persistence;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;
import java.util.Objects;

public class DulDetail {
    private Long id;
    private Long idMkDocument;
    private Client client;
    private Long documentTypeId;
    private String series;
    private String number;
    private String subdivisionCode;
    private String issuer;
    private String issued;
    private String expiration;
    private Date createDate;
    private Date lastUpdate;
    private Boolean deleteState;
    private DulGuide dulGuide;

    public DulDetail(Client client, Long documentTypeId, DulGuide dulGuide) {
        this.client = client;
        this.documentTypeId = documentTypeId;
        this.dulGuide = dulGuide;
    }

    public DulDetail(Long documentTypeId, DulGuide dulGuide) {
        this.documentTypeId = documentTypeId;
        this.dulGuide = dulGuide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DulDetail dulDetail = (DulDetail) o;
        return Objects.equals(id, dulDetail.id) && Objects.equals(documentTypeId, dulDetail.documentTypeId)
                && Objects.equals(series, dulDetail.series) && Objects.equals(number, dulDetail.number)
                && Objects.equals(subdivisionCode, dulDetail.subdivisionCode) && Objects.equals(issuer, dulDetail.issuer)
                && Objects.equals(issued, dulDetail.issued) && Objects.equals(expiration, dulDetail.expiration)
                && Objects.equals(deleteState, dulDetail.deleteState);
    }

    public DulDetail() {
    }

    public Boolean getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Boolean deleteState) {
        this.deleteState = deleteState;
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

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSubdivisionCode() {
        return subdivisionCode;
    }

    public void setSubdivisionCode(String subdivisionCode) {
        this.subdivisionCode = subdivisionCode;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DulGuide getDulGuide() {
        return dulGuide;
    }

    public void setDulGuide(DulGuide dulGuide) {
        this.dulGuide = dulGuide;
    }

    public Long getIdMkDocument() {
        return idMkDocument;
    }

    public void setIdMkDocument(Long idMkDocument) {
        this.idMkDocument = idMkDocument;
    }
}
