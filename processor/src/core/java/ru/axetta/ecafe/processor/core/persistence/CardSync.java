/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class CardSync {

    private Long idcardsync;
    private Org org;
    private Card card;
    private Long statechange;


    protected CardSync() {
        // For Hibernate only
    }

    public CardSync( Org org, Card card){
        this.org = org;
        this.card = card;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardSync)) {
            return false;
        }
        final CardSync card = (CardSync) o;
        return idcardsync.equals(card.getIdcardsync());
    }

    @Override
    public int hashCode() {
        return idcardsync.hashCode();
    }

    public Long getIdcardsync() {
        return idcardsync;
    }

    public void setIdcardsync(Long idcardsync) {
        this.idcardsync = idcardsync;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Long getStatechange() {
        return statechange;
    }

    public void setStatechange(Long statechange) {
        this.statechange = statechange;
    }
}