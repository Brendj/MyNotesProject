/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.14
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CycleDiagramExt")
public class CycleDiagramExt {

    @XmlElement(name = "GlobalId")
    private Long globalId;
    @XmlElement(name = "Monday")
    private String monday;
    @XmlElement(name = "Tuesday")
    private String tuesday;
    @XmlElement(name = "Wednesday")
    private String wednesday;
    @XmlElement(name = "Thursday")
    private String thursday;
    @XmlElement(name = "Friday")
    private String friday;
    @XmlElement(name = "Saturday")
    private String saturday;
    @XmlElement(name = "Sunday")
    private String sunday;
    @XmlElement(name = "DateActivationDiagram")
    @XmlSchemaType(name = "dateTime")
    private Date dateActivationDiagram;
    @XmlElement(name = "StateDiagram")
    private int stateDiagram;
    @XmlElement(name = "MondayPrice")
    private Long mondayPrice;
    @XmlElement(name = "TuesdayPrice")
    private Long tuesdayPrice;
    @XmlElement(name = "WednesdayPrice")
    private Long wednesdayPrice;
    @XmlElement(name = "ThursdayPrice")
    private Long thursdayPrice;
    @XmlElement(name = "FridayPrice")
    private Long fridayPrice;
    @XmlElement(name = "SaturdayPrice")
    private Long saturdayPrice;
    @XmlElement(name = "SundayPrice")
    private Long sundayPrice;
    @XmlElement(name = "UpdateDate")
    @XmlSchemaType(name = "dateTime")
    private Date updateDate;
    @XmlElement(name = "ChangesPlace")
    private Boolean changesPlace;

    public String getDayValue(int dayNumber) {
        switch (dayNumber) {
            case 1:
                return monday;
            case 2:
                return tuesday;
            case 3:
                return wednesday;
            case 4:
                return thursday;
            case 5:
                return friday;
            case 6:
                return saturday;
            case 7:
                return sunday;
            default:
                return null;
        }
    }

    public void setDayValue(int dayNumber, String value) {
        switch (dayNumber) {
            case 1:
                monday = value;
                break;
            case 2:
                tuesday = value;
                break;
            case 3:
                wednesday = value;
                break;
            case 4:
                thursday = value;
                break;
            case 5:
                friday = value;
                break;
            case 6:
                saturday = value;
                break;
            case 7:
                sunday = value;
                break;
        }
    }

    public CycleDiagramExt() {}

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public Date getDateActivationDiagram() {
        return dateActivationDiagram;
    }

    public void setDateActivationDiagram(Date dateActivationDiagram) {
        this.dateActivationDiagram = dateActivationDiagram;
    }

    public int getStateDiagram() {
        return stateDiagram;
    }

    public void setStateDiagram(int stateDiagram) {
        this.stateDiagram = stateDiagram;
    }

    public Long getMondayPrice() {
        return mondayPrice;
    }

    public void setMondayPrice(Long mondayPrice) {
        this.mondayPrice = mondayPrice;
    }

    public Long getTuesdayPrice() {
        return tuesdayPrice;
    }

    public void setTuesdayPrice(Long tuesdayPrice) {
        this.tuesdayPrice = tuesdayPrice;
    }

    public Long getWednesdayPrice() {
        return wednesdayPrice;
    }

    public void setWednesdayPrice(Long wednesdayPrice) {
        this.wednesdayPrice = wednesdayPrice;
    }

    public Long getThursdayPrice() {
        return thursdayPrice;
    }

    public void setThursdayPrice(Long thursdayPrice) {
        this.thursdayPrice = thursdayPrice;
    }

    public Long getFridayPrice() {
        return fridayPrice;
    }

    public void setFridayPrice(Long fridayPrice) {
        this.fridayPrice = fridayPrice;
    }

    public Long getSaturdayPrice() {
        return saturdayPrice;
    }

    public void setSaturdayPrice(Long saturdayPrice) {
        this.saturdayPrice = saturdayPrice;
    }

    public Long getSundayPrice() {
        return sundayPrice;
    }

    public void setSundayPrice(Long sundayPrice) {
        this.sundayPrice = sundayPrice;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getChangesPlace() {
        return changesPlace;
    }

    public void setChangesPlace(Boolean changesPlace) {
        this.changesPlace = changesPlace;
    }
}
