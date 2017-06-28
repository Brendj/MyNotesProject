/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.items;

import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.ExternalEvent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 30.09.13
 * Time: 12:30
 */

public class ClientPassItem implements Comparable {

    private String orgName;
    private Date enterTime;
    private String enterName;
    private String direction;
    private String checker;

    public ClientPassItem(EnterEvent event) {
        this.orgName = event.getOrg().getShortName();
        this.enterTime = event.getEvtDateTime();
        this.enterName = event.getEnterName();
        this.direction = getDirection(event.getPassDirection());
        Long checkerId = event.getChildPassCheckerId();
        if (checkerId != null) {
            this.checker = DAOService.getInstance().getClientFullNameById(checkerId);
        }
    }

    public ClientPassItem(ExternalEvent event) {
        this.orgName = event.getOrgName();
        this.enterTime = event.getEvtDateTime();
        this.enterName = event.getEnterName();
        this.direction = getDirection(EnterEvent.ENTRY);
    }

    @Override
    public int compareTo(Object o) {
        return enterTime.compareTo(((ClientPassItem)o).getEnterTime());
    }

    public String getOrgName() {
        return orgName;
    }

    public Date getEnterTime() {
        return enterTime;
    }

    public String getEnterName() {
        return enterName;
    }

    public String getDirection() {
        return direction;
    }

    public String getChecker() {
        return checker;
    }

    private String getDirection(int direction) {
        switch (direction) {
            case EnterEvent.ENTRY:
                return "вход";
            case EnterEvent.EXIT:
                return "выход";
            case EnterEvent.PASSAGE_IS_FORBIDDEN:
                return "проход запрещен";
            case EnterEvent.TURNSTILE_IS_BROKEN:
                return "взлом турникета";
            case EnterEvent.EVENT_WITHOUT_PASSAGE:
                return "событие без прохода";
            case EnterEvent.PASSAGE_RUFUSAL:
                return "отказ от прохода";
            case EnterEvent.RE_ENTRY:
                return "повторный вход";
            case EnterEvent.RE_EXIT:
                return "повторный выход";
            case EnterEvent.QUERY_FOR_ENTER:
                return "запрос на вход";
            case EnterEvent.QUERY_FOR_EXIT:
                return "запрос на выход";
            case EnterEvent.DETECTED_INSIDE:
                return "обнаружен на подносе карты внутри здания";
            case EnterEvent.CHECKED_BY_TEACHER_EXT:
                return "отмечен в классном журнале через внешнюю систему";
            case EnterEvent.CHECKED_BY_TEACHER_INT:
                return "отмечен учителем внутри здания";
            default:
                return "Ошибка обратитесь администратору";
        }
    }
}
