/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.models;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cf_cards")
public class Card {

    @Id
    @Column(name = "idofcard")
    private Long idOfCard;

    @Column(name = "lastupdate", nullable = false)
    private Long lastUpdate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idofclient")
    private Client client;

    @Column(name = "idoforg")
    private Long idOfOrg;

    @Column(name = "lockreason", length = 64)
    private String lockReason;

    @Column(name = "state", nullable = false)
    private Integer state; //TODO переделать маппинг по enum


    @OneToMany(mappedBy="card", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CardSync> cardSyncs;

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        Card card = (Card) o;
        return Objects.equals(getIdOfCard(), card.getIdOfCard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdOfCard());
    }

    public List<CardSync> getCardSyncs() {
        return cardSyncs;
    }

    public void setCardSyncs(List<CardSync> cardSyncs) {
        this.cardSyncs = cardSyncs;
    }
}
