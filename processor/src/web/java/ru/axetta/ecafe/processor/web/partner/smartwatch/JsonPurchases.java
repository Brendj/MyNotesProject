/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

import ru.axetta.ecafe.processor.core.service.geoplaner.JsonPaymentInfo;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import java.util.LinkedList;
import java.util.List;

public class JsonPurchases {
    private Result result;
    private List<JsonPaymentInfo> items;

    public JsonPurchases(){
        this.result = new Result();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<JsonPaymentInfo> getItems() {
        if(this.items == null){
            this.items = new LinkedList<JsonPaymentInfo>();
        }
        return items;
    }

    public void setItems(List<JsonPaymentInfo> items) {
        this.items = items;
    }
}
