/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;

public class SBMSKSummaryRequest extends OnlinePaymentProcessor.PayRequest {
    private String mobile;

    public SBMSKSummaryRequest(int protoVersion, long contragentId, Long tspContragentId, int paymentMethod, long clientId,
            String paymentId, String paymentAdditionalId, long sum, boolean bNegativeSum, String mobile) throws Exception {
        super(protoVersion, true, contragentId, tspContragentId, paymentMethod, clientId,
                paymentId, paymentAdditionalId, sum, bNegativeSum);
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
