/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 03.02.12
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
@XmlType(propOrder = {
        "clientSummary",
        "resultCode",
        "description"
})

@XmlRootElement(name="clientSummaryBaseListResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClientSummaryBaseListResult {
    private List<ClientSummaryBase> clientSummary;
    public Long resultCode;
    public String description;

    public ClientSummaryBaseListResult(List<ClientSummaryBase> clientSummary, Long resultCode, String description) {
        this.clientSummary = clientSummary;
        this.resultCode = resultCode;
        this.description = description;
    }
    public ClientSummaryBaseListResult() {}

    public List<ClientSummaryBase> getClientSummary() {
        return clientSummary;
    }

    public void setClientSummary(List<ClientSummaryBase> clientSummary) {
        this.clientSummary = clientSummary;
    }

    public Long getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(Long resultCode) {
        this.resultCode = resultCode;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
