/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: a.voinov
 * Date: 2.05.21
 * To change this template use File | Settings | File Templates.
 */
public class EMIASbyDay {

    private Long idOfEmiasDay;
    private Long idEMIAS;
    private Date date;
    private Boolean eat;

   public EMIASbyDay(){}

    public Long getIdOfEmiasDay() {
        return idOfEmiasDay;
    }

    public void setIdOfEmiasDay(Long idOfEmiasDay) {
        this.idOfEmiasDay = idOfEmiasDay;
    }

    public Long getIdEMIAS() {
        return idEMIAS;
    }

    public void setIdEMIAS(Long idEMIAS) {
        this.idEMIAS = idEMIAS;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getEat() {
        return eat;
    }

    public void setEat(Boolean eat) {
        this.eat = eat;
    }
}
