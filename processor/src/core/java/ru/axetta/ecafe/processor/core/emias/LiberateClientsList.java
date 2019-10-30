/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.emias;

import java.util.Date;

public class LiberateClientsList {
    private String guid;
    private Long idEventEMIAS;
    private Long typeEventEMIAS;
    private Date dateLiberate;
    private Date startDateLiberate;
    private Date endDateLiberate;

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
}
