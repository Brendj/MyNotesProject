/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */
/**
 * Created by a.voinov on 03.05.2021.
 */
package ru.axetta.ecafe.processor.web.partner.emias;

public class ExemptionVisitingDay {
    private String date;
    private Boolean agreed;

    public ExemptionVisitingDay(String date, Boolean agreed)
    {
        this.date = date;
        this.agreed = agreed;
    }

    public ExemptionVisitingDay()
    { }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getAgreed() {
        return agreed;
    }

    public void setAgreed(Boolean agreed) {
        this.agreed = agreed;
    }
}
