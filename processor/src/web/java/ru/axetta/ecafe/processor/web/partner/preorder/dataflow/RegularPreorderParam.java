/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * Created by baloun on 28.06.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegularPreorderParam")
public class RegularPreorderParam {

    @XmlAttribute(name = "enabled")
    private Boolean enabled;

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

    @XmlAttribute(name = "startDate")
    @XmlSchemaType(name = "date")
    private Date startDate;
    @XmlAttribute(name = "endDate")
    @XmlSchemaType(name = "date")
    private Date endDate;

    public RegularPreorderParam() {

    }

    public Boolean getMonday() {
        return monday == null ? false : monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday == null ? false : tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return wednesday == null ? false : wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return thursday == null ? false : thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return friday == null ? false : friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Boolean getSaturday() {
        return saturday == null ? false : saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
