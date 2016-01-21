/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.report;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 18.01.16
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class ReportParameter {

    private String parameterName;
    private String parameterValue;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }
}
