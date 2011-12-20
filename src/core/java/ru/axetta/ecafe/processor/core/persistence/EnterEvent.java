/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 08.09.11
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */
public class EnterEvent {

    private CompositeIdOfEnterEvent compositeIdOfEnterEvent;
    private String enterName;
    private String turnstileAddr;
    private int passDirection;
    private int eventCode;
    private Long idOfCard;
    private Long idOfClient;
    private Long idOfTempCard;
    private Date evtDateTime;
    private Long idOfVisitor;
    private String visitorFullName;
    private Integer docType;
    private String docSerialNum;
    private Date issueDocDate;
    private Date visitDateTime;

    public EnterEvent() {
        // For Hibernate
    }

    public EnterEvent(CompositeIdOfEnterEvent compositeIdOfEnterEvent, String enterName, String turnstileAddr, int passDirection, int eventCode, Date evtDateTime) {
        this.compositeIdOfEnterEvent = compositeIdOfEnterEvent;
        this.enterName = enterName;
        this.turnstileAddr = turnstileAddr;
        this.passDirection = passDirection;
        this.eventCode = eventCode;
        this.evtDateTime = evtDateTime;
    }

    public CompositeIdOfEnterEvent getCompositeIdOfEnterEvent() {
        return compositeIdOfEnterEvent;
    }

    public void setCompositeIdOfEnterEvent(CompositeIdOfEnterEvent compositeIdOfEnterEvent) {
        this.compositeIdOfEnterEvent = compositeIdOfEnterEvent;
    }

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public String getTurnstileAddr() {
        return turnstileAddr;
    }

    public void setTurnstileAddr(String turnstileAddr) {
        this.turnstileAddr = turnstileAddr;
    }

    public int getPassDirection() {
        return passDirection;
    }

    public void setPassDirection(int passDirection) {
        this.passDirection = passDirection;
    }

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfTempCard() {
        return idOfTempCard;
    }

    public void setIdOfTempCard(Long idOfTempCard) {
        this.idOfTempCard = idOfTempCard;
    }

    public Date getEvtDateTime() {
        return evtDateTime;
    }

    public void setEvtDateTime(Date evtDateTime) {
        this.evtDateTime = evtDateTime;
    }

    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

    public void setIdOfVisitor(Long idOfVisitor) {
        this.idOfVisitor = idOfVisitor;
    }

    public String getVisitorFullName() {
        return visitorFullName;
    }

    public void setVisitorFullName(String visitorFullName) {
        this.visitorFullName = visitorFullName;
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public String getDocSerialNum() {
        return docSerialNum;
    }

    public void setDocSerialNum(String docSerialNum) {
        this.docSerialNum = docSerialNum;
    }

    public Date getIssueDocDate() {
        return issueDocDate;
    }

    public void setIssueDocDate(Date issueDocDate) {
        this.issueDocDate = issueDocDate;
    }

    public Date getVisitDateTime() {
        return visitDateTime;
    }

    public void setVisitDateTime(Date visitDateTime) {
        this.visitDateTime = visitDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EnterEvent that = (EnterEvent) o;

        if (!compositeIdOfEnterEvent.equals(that.compositeIdOfEnterEvent)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return compositeIdOfEnterEvent.hashCode();
    }

    @Override
    public String toString() {
        return "EnterEvent{" + "compositeIdOfEnterEvent=" + compositeIdOfEnterEvent + ", enterName='" + enterName + '\''
                + ", turnstileAddr='" + turnstileAddr + '\'' + ", passDirection=" + passDirection + ", eventCode="
                + eventCode + ", idOfCard=" + idOfCard + ", idOfClient=" + idOfClient + ", idOfTempCard=" + idOfTempCard
                + ", evtDateTime=" + evtDateTime + ", idOfVisitor=" + idOfVisitor + ", visitorFullName='"
                + visitorFullName + '\'' + ", docType=" + docType + ", docSerialNum='" + docSerialNum + '\''
                + ", issueDocDate=" + issueDocDate + ", visitDateTime=" + visitDateTime + '}';
    }
}
