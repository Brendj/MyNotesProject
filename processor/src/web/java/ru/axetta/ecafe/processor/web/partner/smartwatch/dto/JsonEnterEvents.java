/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.smartwatch.IJsonBase;

import java.util.LinkedList;
import java.util.List;

public class JsonEnterEvents implements IJsonBase {
    private Result result;
    private List<JsonEnterEventItem> items;

    public JsonEnterEvents(){
        this.result = new Result();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<JsonEnterEventItem> getItems() {
        if(items == null){
            items = new LinkedList<JsonEnterEventItem>();
        }
        return items;
    }

    public void setItems(List<JsonEnterEventItem> items) {
        this.items = items;
    }
}
