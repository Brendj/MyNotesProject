/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.models;

import javax.persistence.*;

@Entity
@Table(name = "cf_cr_cardactionclient")
public class CardActionClient{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcardactionclient")
    private Long idcardactionclient;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idcardactionrequest")
    private CardActionRequest cardActionRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idofclient")
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idclientchild")
    private Client clientChild;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idofcard")
    private Card card;

    @Column(name = "comment")
    private String comment;

    @Column(name = "oldcardstate", nullable = false)
    private Integer oldcardstate;


    public CardActionClient() {
        // for Hibernate
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

    public CardActionRequest getCardActionRequest() {
        return cardActionRequest;
    }

    public void setCardActionRequest(CardActionRequest cardActionRequest) {
        this.cardActionRequest = cardActionRequest;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getIdcardactionclient() {
        return idcardactionclient;
    }

    public void setIdcardactionclient(Long idcardactionclient) {
        this.idcardactionclient = idcardactionclient;
    }

    public Integer getOldcardstate() {
        return oldcardstate;
    }

    public void setOldcardstate(Integer oldcardstate) {
        this.oldcardstate = oldcardstate;
    }
}
