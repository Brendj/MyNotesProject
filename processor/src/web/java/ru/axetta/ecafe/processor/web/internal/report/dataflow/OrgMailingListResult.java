/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import javax.xml.bind.annotation.*;

/**
 * User: akmukov
 * Date: 13.02.16
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrgMailingListResult", propOrder = {
        "result",
        "code",
        "mailingList"
})
@XmlRootElement()
public class OrgMailingListResult {
    @XmlElement
    protected String result;
    @XmlElement
    protected Long code;
    @XmlElement
    protected String mailingList;

    public OrgMailingListResult() {}

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

    public String getMailingList() {
        return mailingList;
    }

    public void setMailingList(String mailingList) {
        this.mailingList = mailingList;
    }
}
