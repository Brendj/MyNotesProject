/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.visitordogm;

import ru.axetta.ecafe.processor.core.persistence.EnterEvent;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 12.10.16
 * Time: 10:37
 */
public class CardEventOperationItem {

    private Date operationDate;
    private OrgItem organization;
    private String passDirection;

    public CardEventOperationItem(Date operationDate, Integer passdirection, Long idOfOrg, String shortName, Integer refectoryType) {
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
