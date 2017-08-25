/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 27.11.13
 * Time: 13:57
 */

@XmlRootElement(name = "CycleDiagram")
@XmlAccessorType(XmlAccessType.FIELD)
public class CycleDiagramOut extends Result implements Serializable {

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
    private String mondayPrice;
    @XmlElement(name = "TuesdayPrice")
    private String tuesdayPrice;
    @XmlElement(name = "WednesdayPrice")
    private String wednesdayPrice;
    @XmlElement(name = "ThursdayPrice")
    private String thursdayPrice;
    @XmlElement(name = "FridayPrice")
    private String fridayPrice;
    @XmlElement(name = "SaturdayPrice")
    private String saturdayPrice;
    @XmlElement(name = "SundayPrice")
    private String sundayPrice;
    @XmlElement(name = "UpdateDate")
    @XmlSchemaType(name = "dateTime")
    private Date updateDate;
    @XmlElement(name = "ChangesPlace")
    private Boolean changesPlace;
    @XmlElement(name = "StartWeekPosition")
    private Integer startWeekPosition;

    public CycleDiagramOut() {
    }

    public CycleDiagramOut(CycleDiagram cd) {
        this.globalId = cd.getGlobalId();
        this.monday = cd.getMonday();
        this.tuesday = cd.getTuesday();
        this.wednesday = cd.getWednesday();
        this.thursday = cd.getThursday();
        this.friday = cd.getFriday();
        this.saturday = cd.getSaturday();
        this.sunday = cd.getSunday();
        this.dateActivationDiagram = cd.getDateActivationDiagram();
        this.stateDiagram = cd.getStateDiagram().ordinal();
        this.mondayPrice = cd.getMondayPrice();
        this.tuesdayPrice = cd.getTuesdayPrice();
        this.wednesdayPrice = cd.getWednesdayPrice();
        this.thursdayPrice = cd.getThursdayPrice();
        this.fridayPrice = cd.getFridayPrice();
        this.saturdayPrice = cd.getSaturdayPrice();
        this.sundayPrice = cd.getSundayPrice();
        if (cd.getLastUpdate() == null) {
            this.updateDate = cd.getCreatedDate();
        } else {
            this.updateDate = cd.getLastUpdate();
        }

        if (cd.getStaff() == null) {
            this.changesPlace = false;
        } else {
            this.changesPlace = true;
        }
        this.startWeekPosition = cd.getStartWeekPosition();
    }

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

    public String getMondayPrice() {
        return mondayPrice;
    }

    public void setMondayPrice(String mondayPrice) {
        this.mondayPrice = mondayPrice;
    }

    public String getTuesdayPrice() {
        return tuesdayPrice;
    }

    public void setTuesdayPrice(String tuesdayPrice) {
        this.tuesdayPrice = tuesdayPrice;
    }

    public String getWednesdayPrice() {
        return wednesdayPrice;
    }

    public void setWednesdayPrice(String wednesdayPrice) {
        this.wednesdayPrice = wednesdayPrice;
    }

    public String getThursdayPrice() {
        return thursdayPrice;
    }

    public void setThursdayPrice(String thursdayPrice) {
        this.thursdayPrice = thursdayPrice;
    }

    public String getFridayPrice() {
        return fridayPrice;
    }

    public void setFridayPrice(String fridayPrice) {
        this.fridayPrice = fridayPrice;
    }

    public String getSaturdayPrice() {
        return saturdayPrice;
    }

    public void setSaturdayPrice(String saturdayPrice) {
        this.saturdayPrice = saturdayPrice;
    }

    public String getSundayPrice() {
        return sundayPrice;
    }

    public void setSundayPrice(String sundayPrice) {
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

    public Integer getStartWeekPosition() {
        return startWeekPosition;
    }

    public void setStartWeekPosition(Integer startWeekPosition) {
        this.startWeekPosition = startWeekPosition;
    }
}
