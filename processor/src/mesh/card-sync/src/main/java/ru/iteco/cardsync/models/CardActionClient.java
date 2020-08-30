/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.models;

import ru.iteco.cardsync.audit.AuditEntity;
import ru.iteco.cardsync.audit.AuditEntityListener;
import ru.iteco.cardsync.audit.Auditable;
import ru.iteco.cardsync.enums.ActionType;
import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "cf_cr_cardactionclient")
public class CardActionClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "requestid", length = 128, nullable = false)
    private String requestId;

    @ManyToMany(mappedBy = "cardactionclient")
    private Set<CardActionRequest> cardActionRequests;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idofclient")
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idclientchild")
    private Client clientChild;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idofcard")
    private Card card;


    public CardActionClient() {
        // for Hibernate
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Set<CardActionRequest> getCardActionRequests() {
        return cardActionRequests;
    }

    public void setCardActionRequests(Set<CardActionRequest> cardActionRequests) {
        this.cardActionRequests = cardActionRequests;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClientChild() {
        return clientChild;
    }

    public void setClientChild(Client clientChild) {
        this.clientChild = clientChild;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
