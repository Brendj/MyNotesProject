/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.core.persistence.RegularPreorder;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by i.semenov on 09.07.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegularPreorder")
public class RegularPreorderItem {
    @XmlAttribute(name = "itemCode")
    private String itemCode;
    @XmlAttribute(name = "idOfComplex")
    private Integer idOfComplex;
    @XmlAttribute(name = "startDate")
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar startDate;
    @XmlAttribute(name = "endDate")
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar endDate;
    @XmlAttribute(name = "price")
    private Long price;
    @XmlAttribute(name = "amount")
    private Integer amount;

    @XmlAttribute(name = "monday")
    private Boolean monday;
    @XmlAttribute(name = "tuesday")
    private Boolean tuesday;
    @XmlAttribute(name = "wednesday")
    private Boolean wednesday;
    @XmlAttribute(name = "thursday")
    private Boolean thursday;
    @XmlAttribute(name = "friday")
    private Boolean friday;
    @XmlAttribute(name = "saturday")
    private Boolean saturday;

    public RegularPreorderItem() {

    }

    public RegularPreorderItem(RegularPreorder regularPreorder) {
        this. itemCode = regularPreorder.getItemCode();
        this.idOfComplex = regularPreorder.getIdOfComplex();
        this.startDate = CalendarUtils.toXmlDateTimeWithTimezoneOffset(regularPreorder.getStartDate());
        this.endDate = CalendarUtils.toXmlDateTimeWithTimezoneOffset(regularPreorder.getEndDate());
        this.price = regularPreorder.getPrice();
        this.amount = regularPreorder.getAmount();
        this.monday = regularPreorder.getMonday();
        this.tuesday = regularPreorder.getTuesday();
        this.wednesday = regularPreorder.getWednesday();
        this.thursday = regularPreorder.getThursday();
        this.friday = regularPreorder.getFriday();
        this.saturday = regularPreorder.getSaturday();
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Integer idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(XMLGregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    public void setEndDate(XMLGregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
