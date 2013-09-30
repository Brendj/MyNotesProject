/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.items;

import ru.axetta.ecafe.processor.core.persistence.EnterEvent;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 30.09.13
 * Time: 12:30
 */

public class ClientPassItem {

    private String orgName;
    private Date enterTime;
    private String enterName;
    private String direction;

    public ClientPassItem(EnterEvent event) {
        this.orgName = event.getOrg().getShortName();
        this.enterTime = event.getEvtDateTime();
        this.enterName = event.getEnterName();
        this.direction = getDirection(event.getPassDirection());
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
            case EnterEvent.DETECTED_INSIDE:
                return "обнаружен на подносе карты внутри здания";
            default:
                return "Ошибка обратитесь администратору";
        }
    }
}
