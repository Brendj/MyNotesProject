/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.ClientBalanceHold;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 11.10.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestForCashOutList")
public class RequestForCashOutList extends Result {
    @XmlElement(name = "items")
    private RequestForCashOutListItem items;

    public void attachBalanceHoldList(List<ClientBalanceHold> list) {
        items = new RequestForCashOutListItem();
        List<RequestForCashOutItem> items2 = new ArrayList<RequestForCashOutItem>();
        for (ClientBalanceHold clientBalanceHold : list) {
            RequestForCashOutItem item = new RequestForCashOutItem();
            item.setIdOfRequest(clientBalanceHold.getIdOfClientBalanceHold());
            item.setDate(clientBalanceHold.getCreatedDate());
            item.setLastUpdate(clientBalanceHold.getLastUpdate());
            item.setRequestStatus(clientBalanceHold.getRequestStatus().ordinal());
            item.setSum(clientBalanceHold.getHoldSum());
            items2.add(item);
        }
        items.setItems(items2);
    }

    public RequestForCashOutListItem getItems() {
        return items;
    }

    public void setItemList(RequestForCashOutListItem items) {
        this.items = items;
    }
}
