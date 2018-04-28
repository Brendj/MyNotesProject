package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class CardRequest {
    private Long idOfCardRequest;
    private Integer typeCard;
    private Date createdDate;
    private String mobile;
    private Long version;
    private Client client;
    private Date cardIssueDate;
    private Boolean deletedState;

    public CardRequest() {

    }

    public CardRequest(Client client, Integer typeCard, String mobile, Long version) {
        this.client = client;
        this.typeCard = typeCard;
        this.mobile = mobile;
        this.createdDate = new Date();
        this.deletedState = false;
        this.version = version;
    }

    public Long getIdOfCardRequest() {
        return idOfCardRequest;
    }

    public void setIdOfCardRequest(Long idOfCardRequest) {
        this.idOfCardRequest = idOfCardRequest;
    }

    public Integer getTypeCard() {
        return typeCard;
    }

    public void setTypeCard(Integer typeCard) {
        this.typeCard = typeCard;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getCardIssueDate() {
        return cardIssueDate;
    }

    public void setCardIssueDate(Date cardIssueDate) {
        this.cardIssueDate = cardIssueDate;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }
}
