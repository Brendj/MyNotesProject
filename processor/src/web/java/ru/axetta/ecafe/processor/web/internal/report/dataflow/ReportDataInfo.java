/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import ru.axetta.ecafe.processor.web.internal.report.ResultEnum;

import javax.activation.DataHandler;
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
        "file"
})
@XmlRootElement(name = "Data")
public class ReportDataInfo {

    @XmlElement
    protected String result;

    @XmlElement
    protected Integer code;

    @XmlElement
    @XmlMimeType("application/octet-stream")
    protected DataHandler file;

    public ReportDataInfo() {
    }

    public ReportDataInfo(DataHandler file, ResultEnum result) {
        this.file = file;
        this.result = result.toString();
        this.code = result.getValue();
    }

    public Integer getCode() {
        return code;
    }

    public DataHandler getFile() {
        return file;
    }

    public void setFile(DataHandler file) {
        this.file = file;
    }

    public String getResult() {
        return result;
    }

}
