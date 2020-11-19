/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.smartwatch.IJsonBase;

import java.util.LinkedList;
import java.util.List;

public class JsonPurchaseDetail implements IJsonBase {
    private Result result;
    private List<JsonPurchaseDetailItem> items;

    public JsonPurchaseDetail(){
        this.result = new Result();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<JsonPurchaseDetailItem> getItems() {
        if(this.items == null){
            this.items = new LinkedList<JsonPurchaseDetailItem>();
        }
        return items;
    }

    public void setItems(List<JsonPurchaseDetailItem> items) {
        this.items = items;
    }
}
