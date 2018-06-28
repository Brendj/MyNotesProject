/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

public class CardResponseItem extends ResponseItem {
    private Long idOfCard;

    public CardResponseItem(Long idOfCard) {
        this.code = OK;
        this.message = OK_MESSAGE;
        this.idOfCard = idOfCard;
    }

    public CardResponseItem(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }
}
