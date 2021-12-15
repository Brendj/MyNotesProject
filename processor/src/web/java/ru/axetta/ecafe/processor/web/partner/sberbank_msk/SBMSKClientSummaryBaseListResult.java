/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

import java.util.List;

public class SBMSKClientSummaryBaseListResult {
    private List<SBMSKClientSummaryBase> clientSummary;
    public Long resultCode;
    public String description;

    public SBMSKClientSummaryBaseListResult(List<SBMSKClientSummaryBase> clientSummary, Long resultCode, String description) {
        this.clientSummary = clientSummary;
        this.resultCode = resultCode;
        this.description = description;
    }
    public SBMSKClientSummaryBaseListResult() {}

    public List<SBMSKClientSummaryBase> getClientSummary() {
        return clientSummary;
    }

    public void setClientSummary(List<SBMSKClientSummaryBase> clientSummary) {
        this.clientSummary = clientSummary;
    }
}
