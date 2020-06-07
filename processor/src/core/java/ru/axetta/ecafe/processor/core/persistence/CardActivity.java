/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by nuc on 17.04.2020.
 */
public class CardActivity {
    private Long idOfCardActivity;
    private Long idOfCard;
    private Date lastUpdate;
    private CardActivityType type;

    public CardActivity() {

    }

    public String toString() {
        return idOfCard.toString();
    }

    public CardActivity(long idOfCard, CardActivityType type) {
        this.idOfCard = idOfCard;
        this.type = type;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Long getIdOfCardActivity() {
        return idOfCardActivity;
    }

    public void setIdOfCardActivity(Long idOfCardActivity) {
        this.idOfCardActivity = idOfCardActivity;
    }

    public CardActivityType getType() {
        return type;
    }

    public void setType(CardActivityType type) {
        this.type = type;
    }
}
