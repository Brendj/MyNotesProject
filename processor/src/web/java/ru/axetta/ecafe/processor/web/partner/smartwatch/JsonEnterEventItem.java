/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

public class JsonEnterEventItem {
    private Long evtDateTime;
    private Integer direction;
    private String cardType;
    private String client;

    public Long getEvtDateTime() {
        return evtDateTime;
    }

    public void setEvtDateTime(Long evtDateTime) {
        this.evtDateTime = evtDateTime;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
