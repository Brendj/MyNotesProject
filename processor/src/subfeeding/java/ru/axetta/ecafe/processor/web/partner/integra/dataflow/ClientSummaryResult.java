/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 03.02.12
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class ClientSummaryResult {
    public ClientSummaryExt clientSummary;
    public Long resultCode;
    public String description;

    public ClientSummaryResult(ClientSummaryExt clientSummary, Long resultCode, String description) {
        this.clientSummary = clientSummary;
        this.resultCode = resultCode;
        this.description = description;
    }
    public ClientSummaryResult() {}
}
