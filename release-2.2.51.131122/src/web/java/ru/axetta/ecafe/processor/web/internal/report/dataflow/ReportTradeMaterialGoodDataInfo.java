/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import javax.xml.bind.annotation.*;
import java.util.List;

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
        "tradeMaterialGoodList"
})
@XmlRootElement(name = "Data")
public class ReportTradeMaterialGoodDataInfo {

    @XmlElement
    protected String result;
    @XmlElement
    protected Long code;
    @XmlElement
    protected TradeMaterialGoodList tradeMaterialGoodList;

    public ReportTradeMaterialGoodDataInfo() {}


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

    public TradeMaterialGoodList getTradeMaterialGoodList() {
        return tradeMaterialGoodList;
    }

    public void setTradeMaterialGoodList(TradeMaterialGoodList tradeMaterialGoodList) {
        this.tradeMaterialGoodList = tradeMaterialGoodList;
    }
}
