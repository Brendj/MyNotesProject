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
    private Long idOfTempCard;
    private Date evtDateTime;
    private Long idOfVisitor;
    private String visitorFullName;
    private Integer docType;
    private String docSerialNum;
    private Date issueDocDate;
    private Date visitDateTime;
    private Client client;
    private Org org;
    private Long guardianId;
    private Integer childPassChecker;
    private Long childPassCheckerId;
    private ClientGroup clientGroup;
    private Long idOfClientGroup;


    //pass direction
    public static final int ENTRY = 0; // вход
    public static final int EXIT = 1; // выход
    public static final int PASSAGE_IS_FORBIDDEN = 2; // проход запрещен
    public static final int TURNSTILE_IS_BROKEN = 3; // взлом турникета
    public static final int EVENT_WITHOUT_PASSAGE = 4; // событие без прохода
    public static final int PASSAGE_RUFUSAL = 5; // отказ от прохода
    public static final int RE_ENTRY = 6; // повторный вход
    public static final int RE_EXIT = 7; // повторный выход
    public static final int QUERY_FOR_ENTER = 8; // запрос на вход
    public static final int QUERY_FOR_EXIT = 9; // запрос на выход
    public static final int DIRECTION_ENTER = 0;
    public static final int DIRECTION_EXIT = 1;
    public static final int DETECTED_INSIDE = 100; // обнаружен на подносе карты внутри здания
    public static final int CHECKED_BY_TEACHER_EXT = 101; //отмечен в классном журнале через внешнюю систему
    public static final int CHECKED_BY_TEACHER_INT = 102; //отмечен учителем внутри здания
    public static final int ENTRY_WITHOUT_CARD = 112; // проход без карты
    public static final int BLACK_LIST = 202; // Посетитель из черного списка

    //eventcode
   //

    public static boolean isEntryOrExitEvent(int passDirection){
        return isEntryOrReEntryEvent(passDirection) || isExitOrReExitEvent(passDirection);
    }

    public static boolean isEntryOrReEntryEvent(int passDirection){
        return passDirection==ENTRY || passDirection==RE_ENTRY;
    }

    public static boolean isExitOrReExitEvent(int passDirection){
        return passDirection==EXIT || passDirection==RE_EXIT;
    }

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

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(Long guardianId) {
        this.guardianId = guardianId;
    }

    public Integer getChildPassChecker() {
        return childPassChecker;
    }

    public void setChildPassChecker(Integer childPassChecker) {
        this.childPassChecker = childPassChecker;
    }

    public Long getChildPassCheckerId() {
        return childPassCheckerId;
    }

    public void setChildPassCheckerId(Long childPassCheckerId) {
        this.childPassCheckerId = childPassCheckerId;
    }

    public ClientGroup getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(ClientGroup clientGroup) {
        this.clientGroup = clientGroup;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
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

        return compositeIdOfEnterEvent.equals(that.compositeIdOfEnterEvent);

    }

    @Override
    public int hashCode() {
        return compositeIdOfEnterEvent.hashCode();
    }

    @Override
    public String toString() {
        return "EnterEvent{" + "compositeIdOfEnterEvent=" + compositeIdOfEnterEvent + ", enterName='" + enterName + '\''
                + ", turnstileAddr='" + turnstileAddr + '\'' + ", passDirection=" + passDirection + ", eventCode="
                + eventCode + ", idOfCard=" + idOfCard  + ", client=" + client + ", idOfTempCard=" + idOfTempCard
                + ", evtDateTime=" + evtDateTime + ", idOfVisitor=" + idOfVisitor + ", visitorFullName='"
                + visitorFullName + '\'' + ", docType=" + docType + ", docSerialNum='" + docSerialNum + '\''
                + ", issueDocDate=" + issueDocDate + ", visitDateTime=" + visitDateTime + ", guardianId=" + guardianId
                + ", childPassChecker=" + childPassChecker + '\'' + ", childPassCheckerId=" + childPassCheckerId + '\'' + '}';
    }
}
