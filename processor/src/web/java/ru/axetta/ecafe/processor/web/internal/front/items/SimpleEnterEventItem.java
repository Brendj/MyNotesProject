/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import ru.axetta.ecafe.processor.core.persistence.EnterEvent;

/**
 * Created by i.semenov on 20.09.2017.
 */
public class SimpleEnterEventItem {
    private Long idOfClient;
    private Long evtDateTime;
    private Integer passDirection;

    public SimpleEnterEventItem() {

    }

    public SimpleEnterEventItem(EnterEvent ee) {
        this.idOfClient = ee.getClient().getIdOfClient();
        this.evtDateTime = ee.getEvtDateTime().getTime();
        this.passDirection = ee.getPassDirection();
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getEvtDateTime() {
        return evtDateTime;
    }

    public void setEvtDateTime(Long evtDateTime) {
        this.evtDateTime = evtDateTime;
    }

    public Integer getPassDirection() {
        return passDirection;
    }

    public void setPassDirection(Integer passDirection) {
        this.passDirection = passDirection;
    }
}
