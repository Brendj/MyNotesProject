/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import javax.xml.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.01.13
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "result",
        "code",
        "report"
})
@XmlRootElement(name = "Data")
public class GenerateReportResult {

    @XmlElement
    protected String result;

    @XmlElement
    protected Long code;

    @XmlElement
    protected byte[] report;

    public GenerateReportResult() {}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public byte[] getReport() {
        return report;
    }

    public void setReport(byte[] report) {
        this.report = report;
    }
}
