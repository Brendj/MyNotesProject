/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;

import java.util.HashMap;
import java.util.List;

public class SBMSKSummaryResponse extends OnlinePaymentProcessor.PayResponse {
    private Long code;
    private String message;
    private List<SBMSKClientSummaryBase> clientList;


    public SBMSKSummaryResponse(int protoVersion, boolean bCheckOnly, int resultCode, String resultDescription, Long tspContragentId, Long clientId, String paymentId,
            Long balance, Long subBalance1, String clientFirstName, String clientSurname, String clientSecondName, Long cardPrintedNo,
            HashMap<String, String> addInfo, List<SBMSKClientSummaryBase> clientList) {
        super(protoVersion, bCheckOnly, resultCode, resultDescription, tspContragentId, clientId, paymentId,
                balance, subBalance1, clientFirstName, clientSurname, clientSecondName, cardPrintedNo, addInfo);
        this.clientList = clientList;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SBMSKClientSummaryBase> getClientList() {
        return clientList;
    }

    public void setClientList(List<SBMSKClientSummaryBase> clientList) {
        this.clientList = clientList;
    }
}
