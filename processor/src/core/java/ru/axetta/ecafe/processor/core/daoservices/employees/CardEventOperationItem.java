/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.employees;

import ru.axetta.ecafe.processor.core.persistence.EnterEvent;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.08.13
 * Time: 18:42
 * To change this template use File | Settings | File Templates.
 */
public class CardEventOperationItem {

    private Long idOfVisitor;
    private Date operationDate;
    private OrgItem organization;
    private String passDirection;

    public CardEventOperationItem(Long idOfVisitor, Date operationDate, Integer passdirection, Long idOfOrg, String shortName, Integer refectoryType) {
        this.idOfVisitor = idOfVisitor;
        this.operationDate = operationDate;
        this.organization = new OrgItem(idOfOrg, shortName, refectoryType);
        switch (passdirection){
            case EnterEvent.ENTRY: passDirection="вход";  break;
            case EnterEvent.EXIT: passDirection="выход";  break;
            case EnterEvent.PASSAGE_IS_FORBIDDEN: passDirection="проход запрещен"; break;
            case EnterEvent.TURNSTILE_IS_BROKEN: passDirection="взлом турникета"; break;
            case EnterEvent.EVENT_WITHOUT_PASSAGE: passDirection="событие без прохода"; break;
            case EnterEvent.PASSAGE_RUFUSAL: passDirection="отказ от прохода"; break;
            case EnterEvent.RE_ENTRY: passDirection="повторный вход";  break;
            case EnterEvent.RE_EXIT: passDirection="повторный выход"; break;
            case EnterEvent.DETECTED_INSIDE: passDirection="обнаружен на подносе карты внутри здания"; break;
            case EnterEvent.CHECKED_BY_TEACHER_EXT: passDirection="отмечен в классном журнале через внешнюю систему"; break;
            case EnterEvent.CHECKED_BY_TEACHER_INT: passDirection="отмечен учителем внутри здания"; break;
            default: passDirection="Ошибка обратитесь администратору";
        }
    }

    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

    public void setIdOfVisitor(Long idOfVisitor) {
        this.idOfVisitor = idOfVisitor;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public OrgItem getOrganization() {
        return organization;
    }

    public void setOrganization(OrgItem organization) {
        this.organization = organization;
    }

    public String getPassDirection() {
        return passDirection;
    }

}
