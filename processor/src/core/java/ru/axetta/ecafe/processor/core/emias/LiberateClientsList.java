/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.emias;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LiberateClientsList", propOrder = {
        "guid", "idEventEMIAS", "typeEventEMIAS", "dateLiberate", "startDateLiberate", "endDateLiberate",
        "idEventCancelEMIAS"})
public class LiberateClientsList {

    @XmlElement(required = true)
    private String guid;
    @XmlElement(required = true)
    private Long idEventEMIAS;
    @XmlElement(required = true)
    private Long typeEventEMIAS;
    @XmlElement(required = true)
    private Date dateLiberate;
    private Date startDateLiberate;
    private Date endDateLiberate;
    private Long idEventCancelEMIAS;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getIdEventEMIAS() {
        return idEventEMIAS;
    }

    public void setIdEventEMIAS(Long idEventEMIAS) {
        this.idEventEMIAS = idEventEMIAS;
    }

    public Long getTypeEventEMIAS() {
        return typeEventEMIAS;
    }

    public void setTypeEventEMIAS(Long typeEventEMIAS) {
        this.typeEventEMIAS = typeEventEMIAS;
    }

    public Date getDateLiberate() {
        return dateLiberate;
    }

    public void setDateLiberate(Date dateLiberate) {
        this.dateLiberate = dateLiberate;
    }

    public Date getStartDateLiberate() {
        return startDateLiberate;
    }

    public void setStartDateLiberate(Date startDateLiberate) {
        this.startDateLiberate = startDateLiberate;
    }

    public Date getEndDateLiberate() {
        return endDateLiberate;
    }

    public void setEndDateLiberate(Date endDateLiberate) {
        this.endDateLiberate = endDateLiberate;
    }

    public Long getIdEventCancelEMIAS() {
        return idEventCancelEMIAS;
    }

    public void setIdEventCancelEMIAS(Long idEventCancelEMIAS) {
        this.idEventCancelEMIAS = idEventCancelEMIAS;
    }
}
