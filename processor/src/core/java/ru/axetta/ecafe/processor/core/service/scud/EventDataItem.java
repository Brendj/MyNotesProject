/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.scud;

import ru.axetta.ecafe.processor.core.persistence.EnterEvent;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EventDataItem {
    private String organizationUid; // OGRN of Org
    private String systemUid;       // not define
    private String readerUid;       // turnstile
    private String studentUid;      // GUID od client
    private String cardUid;         // CardNo in HEX
    private String directionType;   // Enter or Exit
    private Date eventDate;

    private BigInteger idOfEnterEvent; // For update records
    private BigInteger idOfOrg;

    public static final List<Integer> enterCodes = Arrays.asList(
            EnterEvent.ENTRY,                   // вход
            EnterEvent.RE_ENTRY,                // повторный вход
            EnterEvent.TURNSTILE_IS_BROKEN,     // взлом турникета
            EnterEvent.DETECTED_INSIDE,         // обнаружен на подносе карты внутри здания
            EnterEvent.CHECKED_BY_TEACHER_EXT,  // отмечен в классном журнале через внешнюю систему
            EnterEvent.CHECKED_BY_TEACHER_INT,  // отмечен учителем внутри здания
            EnterEvent.ENTRY_WITHOUT_CARD       // проход без карты
    );

    public EventDataItem(String organizationUid, String systemUid, String readerUid, String studentUid,
            String cardUid, Integer direction, Date eventDate, BigInteger idOfEnterEvent, BigInteger idOfOrg){
        this.organizationUid = organizationUid;
        this.systemUid = systemUid == null? "1" : studentUid;
        this.readerUid = readerUid == null? "1" : readerUid;
        this.studentUid = studentUid;
        this.cardUid = cardUid;
        this.directionType = enterCodes.contains(direction) ? "input" : "output";
        this.eventDate = eventDate;
        this.idOfEnterEvent = idOfEnterEvent;
        this.idOfOrg = idOfOrg;
    }

    public EventDataItem(String organizationUid, String systemUid, String readerUid, String studentUid,
            Long cardNo, Boolean direction, Date eventDate, BigInteger idOfEnterEvent, BigInteger idOfOrg){
        this.organizationUid = organizationUid;
        this.systemUid = systemUid == null? "1" : studentUid;
        this.readerUid = readerUid == null? "1" : readerUid;
        this.studentUid = studentUid;
        this.cardUid = Long.toHexString(cardNo).toUpperCase();
        this.directionType = direction ? "input" : "output";
        this.eventDate = eventDate;
        this.idOfEnterEvent = idOfEnterEvent;
        this.idOfOrg = idOfOrg;
    }

    public String getOrganizationUid() {
        return organizationUid;
    }

    public void setOrganizationUid(String organizationUid) {
        this.organizationUid = organizationUid;
    }

    public String getSystemUid() {
        return systemUid;
    }

    public void setSystemUid(String systemUid) {
        this.systemUid = systemUid;
    }

    public String getReaderUid() {
        return readerUid;
    }

    public void setReaderUid(String readerUid) {
        this.readerUid = readerUid;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }

    public String getCardUid() {
        return cardUid;
    }

    public void setCardUid(String cardUid) {
        this.cardUid = cardUid;
    }

    public String getDirectionType() {
        return directionType;
    }

    public void setDirectionType(String directionType) {
        this.directionType = directionType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public BigInteger getIdOfEnterEvent() {
        return idOfEnterEvent;
    }

    public void setIdOfEnterEvent(BigInteger idOfEnterEvent) {
        this.idOfEnterEvent = idOfEnterEvent;
    }

    public BigInteger getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(BigInteger idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
