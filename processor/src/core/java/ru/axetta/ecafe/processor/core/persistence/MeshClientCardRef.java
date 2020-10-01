/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.Objects;

//Связь между клиентом и его картами в МЭШ
public class MeshClientCardRef {
    private Long idOfCard;
    private Card card;
    private Client client;
    private Integer idOfRefInExternalSystem;
    private Date createDate;
    private Date lastUpdate;
    private Boolean isSend = false;

    public MeshClientCardRef(){
    }

    public static MeshClientCardRef build(Card card, Integer idOfRefInExternalSystem){
        Date now = new Date();
        MeshClientCardRef refCardClient = new MeshClientCardRef();
        refCardClient.setCard(card);
        refCardClient.setClient(card.getClient());
        if(idOfRefInExternalSystem != null) {
            refCardClient.setIdOfRefInExternalSystem(idOfRefInExternalSystem);
            refCardClient.setSend(true);
        }
        refCardClient.setCreateDate(now);
        refCardClient.setLastUpdate(now);

        return refCardClient;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfRef) {
        this.idOfCard = idOfRef;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeshClientCardRef ref = (MeshClientCardRef) o;
        return Objects.equals(idOfCard, ref.idOfCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfCard);
    }
}
