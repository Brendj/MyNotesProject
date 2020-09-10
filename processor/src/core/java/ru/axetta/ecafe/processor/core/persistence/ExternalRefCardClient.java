/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

//Связь между клиентом и его картами в МЭШ
public class ExternalRefCardClient {
    private Long idOfRef;
    private Card card;
    private Client client;
    private Long idOfRefInExternalSystem;
    private Boolean deleteState = false;
    private Date createDate;
    private Date lastUpdate;

    public ExternalRefCardClient(){

    }

    public static ExternalRefCardClient build(Card card, Client client, Long idOfRefInExternalSystem){
        Date now = new Date();
        ExternalRefCardClient refCardClient = new ExternalRefCardClient();
        refCardClient.setCard(card);
        refCardClient.setClient(client);
        refCardClient.setIdOfRefInExternalSystem(idOfRefInExternalSystem);
        refCardClient.setCreateDate(now);
        refCardClient.setLastUpdate(now);

        return refCardClient;
    }

    public Long getIdOfRef() {
        return idOfRef;
    }

    public void setIdOfRef(Long idOfRef) {
        this.idOfRef = idOfRef;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getIdOfRefInExternalSystem() {
        return idOfRefInExternalSystem;
    }

    public void setIdOfRefInExternalSystem(Long idOfRefInExternalSystem) {
        this.idOfRefInExternalSystem = idOfRefInExternalSystem;
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
}
