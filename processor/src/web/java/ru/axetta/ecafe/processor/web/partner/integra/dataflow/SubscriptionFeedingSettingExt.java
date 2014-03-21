/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionFeedingSettingExt{

    @XmlAttribute(name = "dayRequest")
    protected Integer dayRequest; // Количество дней, на которые оформляются заявки на поставку

    @XmlAttribute(name = "dayDeActivate")
    protected Integer dayDeActivate;   // Количество дней, пропустив которые, клиент приостанавливает свою подписку
    @XmlAttribute(name = "enableFeeding")
    protected Boolean enableFeeding;   // Включить автоматическую приостановку/возобновление подписок на услугу АП в зависимости от посещения учреждения
    @XmlAttribute(name = "dayForbidChange")
    protected Integer dayForbidChange; // Количество дней, в течение которых запрещено редактировать заявки

    public Integer getDayRequest() {
        return dayRequest;
    }

    public void setDayRequest(Integer dayRequest) {
        this.dayRequest = dayRequest;
    }

    public Integer getDayDeActivate() {
        return dayDeActivate;
    }

    public void setDayDeActivate(Integer dayDeActivate) {
        this.dayDeActivate = dayDeActivate;
    }

    public Boolean getEnableFeeding() {
        return enableFeeding;
    }

    public void setEnableFeeding(Boolean enableFeeding) {
        this.enableFeeding = enableFeeding;
    }

    public Integer getDayForbidChange() {
        return dayForbidChange;
    }

    public void setDayForbidChange(Integer dayForbidChange) {
        this.dayForbidChange = dayForbidChange;
    }
}
