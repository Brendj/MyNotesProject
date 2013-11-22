/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
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
        "clientOrderDetailsByAllOrgList"
})
@XmlRootElement(name = "Data")
public class ReportClientOrderDetailsByAllOrgDataInfo {

    @XmlElement
    protected String result;
    @XmlElement
    protected Long code;

    @XmlElement
    protected ClientOrderDetailsByAllOrgList clientOrderDetailsByAllOrgList;

    public ReportClientOrderDetailsByAllOrgDataInfo() {}


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

    public ClientOrderDetailsByAllOrgList getClientOrderDetailsByAllOrgList() {
        return clientOrderDetailsByAllOrgList;
    }

    public void setClientOrderDetailsByAllOrgList(ClientOrderDetailsByAllOrgList clientOrderDetailsByAllOrgList) {
        this.clientOrderDetailsByAllOrgList = clientOrderDetailsByAllOrgList;
    }
}
