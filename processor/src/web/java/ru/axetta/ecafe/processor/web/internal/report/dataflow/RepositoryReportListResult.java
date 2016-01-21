/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 19.01.16
 * Time: 11:44
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
/*@XmlType(name = "", propOrder = {
        "repositoryReportItems",
        "result",
        "code"
})
@XmlRootElement(name = "Data")*/
public class RepositoryReportListResult {

    @XmlElement
    //protected List<RepositoryReportItem> repositoryReportItems;
    protected RepositoryReportItems repositoryReportItems;

    @XmlElement
    protected String result;

    @XmlElement
    protected Long code;

    public RepositoryReportListResult() {}

    public RepositoryReportItems getRepositoryReportItems() {
        return repositoryReportItems;
    }

    public void setRepositoryReportItems(RepositoryReportItems repositoryReportItems) {
        this.repositoryReportItems = repositoryReportItems;
    }

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
}
