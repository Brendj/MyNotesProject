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
    @XmlAttribute(name = "daysToForbidChangeInPos")
    protected Integer daysToForbidChangeInPos; // Количество рабочих дней блокировки баланса с учетом стоимости питания, отмеченного в циклограмме
    @XmlAttribute(name = "sixWorkWeek")
    protected  Boolean sixWorkWeek; // Шестидневный план рабочих дней
    @XmlAttribute(name = "dayCreateVP")
    protected Integer dayCreateVP;  //Количество дней, на которые создаются заявки вариативного питания
    @XmlAttribute(name = "hoursForbidVP")
    protected Integer hoursForbidVP;  //Количество часов, в течение которых запрещено редактировать заявки вариативного питания
    @XmlAttribute(name = "hoursForbidPP")
    private Integer hoursForbidPP;  //Количество часов, в течение которых запрещено редактировать заявки по предзаказам

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

    public Integer getDaysToForbidChangeInPos() {
        return daysToForbidChangeInPos;
    }

    public void setDaysToForbidChangeInPos(Integer daysToForbidChangeInPos) {
        this.daysToForbidChangeInPos = daysToForbidChangeInPos;
    }

    public Boolean getSixWorkWeek() {
        return sixWorkWeek;
    }

    public void setSixWorkWeek(Boolean sixWorkWeek) {
        this.sixWorkWeek = sixWorkWeek;
    }

    public Integer getDayCreateVP() {
        return dayCreateVP;
    }

    public void setDayCreateVP(Integer dayCreateVP) {
        this.dayCreateVP = dayCreateVP;
    }

    public Integer getHoursForbidVP() {
        return hoursForbidVP;
    }

    public void setHoursForbidVP(Integer hoursForbidVP) {
        this.hoursForbidVP = hoursForbidVP;
    }

    public Integer getHoursForbidPP() {
        return hoursForbidPP;
    }

    public void setHoursForbidPP(Integer hoursForbidPP) {
        this.hoursForbidPP = hoursForbidPP;
    }
}
