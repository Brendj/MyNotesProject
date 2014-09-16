package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;

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

    public CycleDiagramExt(CycleDiagram diagram) {
        this.globalId = diagram.getGlobalId();
        this.monday = diagram.getMonday();
        this.tuesday = diagram.getTuesday();
        this.wednesday = diagram.getWednesday();
        this.thursday = diagram.getThursday();
        this.friday = diagram.getFriday();
        this.saturday = diagram.getSaturday();
        this.sunday = diagram.getSunday();
        this.dateActivationDiagram = diagram.getDateActivationDiagram();
        this.stateDiagram = diagram.getStateDiagram().ordinal();
        this.mondayPrice = diagram.getMondayPrice();
        this.tuesdayPrice = diagram.getTuesdayPrice();
        this.wednesdayPrice = diagram.getWednesdayPrice();
        this.thursdayPrice = diagram.getThursdayPrice();
        this.fridayPrice = diagram.getFridayPrice();
        this.saturdayPrice = diagram.getSaturdayPrice();
        this.sundayPrice = diagram.getSundayPrice();
        if(diagram.getLastUpdate()==null){
            this.updateDate = diagram.getCreatedDate();
        } else {
            this.updateDate = diagram.getLastUpdate();
        }

        if (diagram.getStaff() == null) {
            this.changesPlace = false;
        } else {
            this.changesPlace = true;
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
}
