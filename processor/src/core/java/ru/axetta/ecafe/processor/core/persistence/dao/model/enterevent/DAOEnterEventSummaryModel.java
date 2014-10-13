/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent;

/**
 * User: shamil
 * Date: 19.09.14
 * Time: 12:57
 */
public class DAOEnterEventSummaryModel {
    private Long idOfClient;
    private Long idOfOrg;
    private Integer passDirection;
    private Integer eventCode;
    private Long idofTempCard;
    private Long evtDateTime;
    private Long idOfVisitor;
    private String visitorFullName;
    private Long idOfClientGroup;
    private String groupname;


    public DAOEnterEventSummaryModel() {
    }

    public DAOEnterEventSummaryModel(Long idOfClient, Long idOfOrg, Integer passDirection, Integer eventCode, Long idofTempCard,
            Long evtDateTime, Long idOfVisitor, String visitorFullName, Long idOfClientGroup) {
        this.idOfClient = idOfClient;
        this.idOfOrg = idOfOrg;
        this.passDirection = passDirection;
        this.eventCode = eventCode;
        this.idofTempCard = idofTempCard;
        this.evtDateTime = evtDateTime;
        this.idOfVisitor = idOfVisitor;
        this.visitorFullName = visitorFullName;
        this.idOfClientGroup = idOfClientGroup;
    }

    public DAOEnterEventSummaryModel(Long idOfClient, Long idOfOrg, Integer passDirection, Integer eventCode,
            Long idofTempCard, Long evtDateTime, Long idOfVisitor, String visitorFullName, Long idOfClientGroup,
            String groupname) {
        this.idOfClient = idOfClient;
        this.idOfOrg = idOfOrg;
        this.passDirection = passDirection;
        this.eventCode = eventCode;
        this.idofTempCard = idofTempCard;
        this.evtDateTime = evtDateTime;
        this.idOfVisitor = idOfVisitor;
        this.visitorFullName = visitorFullName;
        this.idOfClientGroup = idOfClientGroup;
        this.groupname = groupname;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Integer getPassDirection() {
        return passDirection;
    }

    public void setPassDirection(Integer passDirection) {
        this.passDirection = passDirection;
    }

    public Integer getEventCode() {
        return eventCode;
    }

    public void setEventCode(Integer eventCode) {
        this.eventCode = eventCode;
    }

    public Long getIdoftempcard() {
        return idofTempCard;
    }

    public void setIdofTempcard(Long idofTempCard) {
        this.idofTempCard = idofTempCard;
    }

    public Long getEvtdatetime() {
        return evtDateTime;
    }

    public void setEvtdatetime(Long evtDateTime) {
        this.evtDateTime = evtDateTime;
    }

    public Long getIdofvisitor() {
        return idOfVisitor;
    }

    public void setIdofvisitor(Long idOfVisitor) {
        this.idOfVisitor = idOfVisitor;
    }

    public String getVisitorFullName() {
        return visitorFullName;
    }

    public void setVisitorFullName(String visitorFullName) {
        this.visitorFullName = visitorFullName;
    }

    public Long getIdofclientgroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Long getIdofTempCard() {
        return idofTempCard;
    }

    public void setIdofTempCard(Long idofTempCard) {
        this.idofTempCard = idofTempCard;
    }

    public Long getEvtDateTime() {
        return evtDateTime;
    }

    public void setEvtDateTime(Long evtDateTime) {
        this.evtDateTime = evtDateTime;
    }

    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

    public void setIdOfVisitor(Long idOfVisitor) {
        this.idOfVisitor = idOfVisitor;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
}
