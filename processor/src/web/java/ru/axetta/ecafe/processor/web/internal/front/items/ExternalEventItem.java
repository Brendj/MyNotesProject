/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 30.11.15
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalEventItem")
public class ExternalEventItem {

    @XmlAttribute(name = "enterName")
    private String enterName;
    @XmlAttribute(name = "evtDateTime")
    private Date evtDateTime;
    @XmlAttribute(name = "idOfClient")
    private Long idOfClient;
    @XmlAttribute(name = "type")
    private Integer type;
    @XmlAttribute(name = "orgName")
    private String orgName;

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

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

}
