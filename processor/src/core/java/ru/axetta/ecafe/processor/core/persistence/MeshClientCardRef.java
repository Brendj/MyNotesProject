/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

//Связь между клиентом и его картами в МЭШ
public class MeshClientCardRef {
    private Long idOfRef;
    private Card card;
    private Client client;
    private Integer idOfRefInExternalSystem;
    private Boolean deleteState = false;
    private Date createDate;
    private Date lastUpdate;
    private Boolean isSend = false;

    public MeshClientCardRef(){
    }

    public static MeshClientCardRef build(Card card, Client client, Integer idOfRefInExternalSystem){
        Date now = new Date();
        MeshClientCardRef refCardClient = new MeshClientCardRef();
        refCardClient.setCard(card);
        refCardClient.setClient(client);
        if(idOfRefInExternalSystem != null) {
            refCardClient.setIdOfRefInExternalSystem(idOfRefInExternalSystem);
            refCardClient.setSend(true);
        }
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

    public Integer getIdOfRefInExternalSystem() {
        return idOfRefInExternalSystem;
    }

    public void setIdOfRefInExternalSystem(Integer idOfRefInExternalSystem) {
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

    public Boolean getSend() {
        return isSend;
    }

    public void setSend(Boolean send) {
        isSend = send;
    }
}
