/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.smartwatch.IJsonBase;

import java.util.LinkedList;
import java.util.List;

public class JsonPurchases implements IJsonBase {
    private Result result;
    private List<JsonOrder> items;

    public JsonPurchases(){
        this.result = new Result();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<JsonOrder> getItems() {
        if(this.items == null){
            this.items = new LinkedList<JsonOrder>();
        }
        return items;
    }

    public void setItems(List<JsonOrder> items) {
        this.items = items;
    }
}
