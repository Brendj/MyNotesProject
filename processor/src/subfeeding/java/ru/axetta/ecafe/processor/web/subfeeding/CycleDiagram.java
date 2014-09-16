/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.CycleDiagramExt;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.06.14
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public class CycleDiagram implements Serializable{

    private Long globalId;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String sunday;
    private Date dateActivationDiagram;
    private Date dateDeactivationDiagram;
    private String stateDiagram;
    private String mondayPrice;
    private String tuesdayPrice;
    private String wednesdayPrice;
    private String thursdayPrice;
    private String fridayPrice;
    private String saturdayPrice;
    private String sundayPrice;
    private Date updateDate;
    private Boolean onChange = false;
    private String changesPlace;

    public String getStartDate(){
        return dateActivationDiagram==null?"":CalendarUtils.dateShortFullYearToString(dateActivationDiagram);
    }

    public String getEndDate(){
        return dateDeactivationDiagram==null?"":CalendarUtils.dateShortFullYearToString(dateDeactivationDiagram);
    }

    public String getActivePeriod(){
        StringBuilder builder = new StringBuilder();
        builder.append(CalendarUtils.dateShortToString(dateActivationDiagram));
        if(dateDeactivationDiagram!=null){
            builder.append(" &mdash; ");
            builder.append(CalendarUtils.dateShortToString(dateDeactivationDiagram));
        }
        return builder.toString();
    }

    public Date getDateDeactivationDiagram() {
        return dateDeactivationDiagram;
    }

    public void setDateDeactivationDiagram(Date dateDeactivationDiagram) {
        this.dateDeactivationDiagram = dateDeactivationDiagram;
    }

    private Long totalSum = 0L;

    public Long getTotalSum() {
        return totalSum;
    }

    public void addTotalSum(Long sum) {
        this.totalSum += sum;
    }

    public Map<Integer, List<String>> splitPlanComplexes() {
        Map<Integer, List<String>> activeComplexes = new HashMap<Integer, List<String>>();
        activeComplexes.put(1, Arrays.asList(StringUtils.split(StringUtils.defaultString(monday), ';')));
        activeComplexes.put(2, Arrays.asList(StringUtils.split(StringUtils.defaultString(tuesday), ';')));
        activeComplexes.put(3, Arrays.asList(StringUtils.split(StringUtils.defaultString(wednesday), ';')));
        activeComplexes.put(4, Arrays.asList(StringUtils.split(StringUtils.defaultString(thursday), ';')));
        activeComplexes.put(5, Arrays.asList(StringUtils.split(StringUtils.defaultString(friday), ';')));
        activeComplexes.put(6, Arrays.asList(StringUtils.split(StringUtils.defaultString(saturday), ';')));
        activeComplexes.put(7, Arrays.asList(StringUtils.split(StringUtils.defaultString(sunday), ';')));
        return activeComplexes;
    }

    public String getWeekPrices(){
        return CurrencyStringUtils.copecksToRubles(totalSum);
    }

    public String getMonthPrices(){
        return CurrencyStringUtils.copecksToRubles(totalSum*4);
    }

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
            case 0:  this.stateDiagram = "Активная"; break;
            case 1:  this.stateDiagram = "Ожидает активации"; break;
            case 2:  this.stateDiagram = "Заблокирована"; break;
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
        this.changesPlace = cycleDiagramExt.getChangesPlace() ? "АРМ Администратора" : "Личный кабинет";
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

    public String getMondayPrice() {
        return mondayPrice;
    }

    public String getTuesdayPrice() {
        return tuesdayPrice;
    }

    public String getWednesdayPrice() {
        return wednesdayPrice;
    }

    public String getThursdayPrice() {
        return thursdayPrice;
    }

    public String getFridayPrice() {
        return fridayPrice;
    }

    public String getSaturdayPrice() {
        return saturdayPrice;
    }

    public String getSundayPrice() {
        return sundayPrice;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public String getChangesPlace() {
        return changesPlace;
    }

    public void setChangesPlace(String changesPlace) {
        this.changesPlace = changesPlace;
    }

    public Boolean getOnChange() {
        return onChange;
    }

    public void setOnChange(Boolean onChange) {
        this.onChange = onChange;
    }
}
