/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 03.02.12
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class ClientSummaryBaseListResult extends Result {
    private List<ClientSummaryBase> clientSummary;


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
}
