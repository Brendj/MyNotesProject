/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.CycleDiagramExt;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.06.14
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public class CycleDiagram implements Serializable{

    static class CycleDiagramCompareByUpdateDate implements Comparator<CycleDiagram> {

        @Override
        public int compare(CycleDiagram o1, CycleDiagram o2) {
            return o1.updateDate.compareTo(o2.updateDate);
        }
    }

    public static CycleDiagramCompareByUpdateDate buildUpdateDateComparator(){
        return new CycleDiagramCompareByUpdateDate();
    }

    private Long globalId;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String sunday;
    private Date dateActivationDiagram;
    private String stateDiagram;
    private Long mondayPrice;
    private Long tuesdayPrice;
    private Long wednesdayPrice;
    private Long thursdayPrice;
    private Long fridayPrice;
    private Long saturdayPrice;
    private Long sundayPrice;
    private Date updateDate;

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

    public String getDiagramNumber(){
        return String.format("Циклограмма_%d", globalId);
    }

    public CycleDiagram(CycleDiagramExt cycleDiagramExt) {
        this.globalId = cycleDiagramExt.getGlobalId();
        this.monday = cycleDiagramExt.getMonday();
        this.tuesday = cycleDiagramExt.getTuesday();
        this.wednesday = cycleDiagramExt.getWednesday();
        this.thursday = cycleDiagramExt.getThursday();
        this.friday = cycleDiagramExt.getFriday();
        this.saturday = cycleDiagramExt.getSaturday();
        this.sunday = cycleDiagramExt.getSunday();
        this.dateActivationDiagram = cycleDiagramExt.getDateActivationDiagram();
        switch (cycleDiagramExt.getStateDiagram()){
            case 0:  this.stateDiagram = "Заблокирована"; break;
            case 1:  this.stateDiagram = "Ожидает активации"; break;
            case 2:  this.stateDiagram = "Активная"; break;
            default: this.stateDiagram = "";
        }
        this.mondayPrice = cycleDiagramExt.getMondayPrice();
        this.tuesdayPrice = cycleDiagramExt.getTuesdayPrice();
        this.wednesdayPrice = cycleDiagramExt.getWednesdayPrice();
        this.thursdayPrice = cycleDiagramExt.getThursdayPrice();
        this.fridayPrice = cycleDiagramExt.getFridayPrice();
        this.saturdayPrice = cycleDiagramExt.getSaturdayPrice();
        this.sundayPrice = cycleDiagramExt.getSundayPrice();
        this.updateDate = cycleDiagramExt.getUpdateDate();
    }

    public Long getGlobalId() {
        return globalId;
    }

    public String getMonday() {
        return monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public String getFriday() {
        return friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public Date getDateActivationDiagram() {
        return dateActivationDiagram;
    }

    public String getStateDiagram() {
        return stateDiagram;
    }

    public Long getMondayPrice() {
        return mondayPrice;
    }

    public Long getTuesdayPrice() {
        return tuesdayPrice;
    }

    public Long getWednesdayPrice() {
        return wednesdayPrice;
    }

    public Long getThursdayPrice() {
        return thursdayPrice;
    }

    public Long getFridayPrice() {
        return fridayPrice;
    }

    public Long getSaturdayPrice() {
        return saturdayPrice;
    }

    public Long getSundayPrice() {
        return sundayPrice;
    }

    public Date getUpdateDate() {
        return updateDate;
    }
}
