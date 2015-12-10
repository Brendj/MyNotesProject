/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 30.11.15
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public class EnterEventManualItem {

    private String enterName;
    private Date evtDateTime;
    private Long idOfOrg;
    private Long idOfClient;

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public Date getEvtDateTime() {
        return evtDateTime;
    }

    public void setEvtDateTime(Date evtDateTime) {
        this.evtDateTime = evtDateTime;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }
}
