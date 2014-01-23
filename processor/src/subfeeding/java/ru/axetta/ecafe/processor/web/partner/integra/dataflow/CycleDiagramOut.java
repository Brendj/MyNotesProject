/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

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

    public CycleDiagramOut() {
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
}
