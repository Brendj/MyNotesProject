/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBase;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBaseListResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 06.03.2018.
 */
public class PreorderClientSummaryBaseListResult extends SudirBaseData {
    private List<PreorderClientSummary> clientSummary;

    /*public PreorderClientSummaryBaseListResult(String token, ClientSummaryBaseListResult clientSummary) {
        super(token);
        this.clientSummary = clientSummary.getClientSummary();
    }*/

    public PreorderClientSummaryBaseListResult(ClientSummaryBaseListResult clientSummary) throws Exception {
        List<PreorderClientSummary> list = new ArrayList<PreorderClientSummary>();
        for (ClientSummaryBase summary : clientSummary.getClientSummary()) {
            PreorderClientSummary preorderClientSummary = new PreorderClientSummary(summary);
            list.add(preorderClientSummary);
        }
        this.clientSummary = list;
    }

    public List<PreorderClientSummary> getClientSummary() {
        return clientSummary;
    }

    public void setClientSummary(List<PreorderClientSummary> clientSummary) {
        this.clientSummary = clientSummary;
    }
}
