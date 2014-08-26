/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
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
public class ClientSummaryExtListResult extends Result {
    public List<ClientSummaryExt> clientSummary;


    public ClientSummaryExtListResult(List<ClientSummaryExt> clientSummary, Long resultCode, String description) {
        this.clientSummary = clientSummary;
        this.resultCode = resultCode;
        this.description = description;
    }
    public ClientSummaryExtListResult() {}
}
