/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

import ru.axetta.ecafe.processor.core.service.geoplaner.JsonEnterEventInfo;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import java.util.LinkedList;
import java.util.List;

public class JsonEnterEvents {
    private Result result;
    private List<JsonEnterEventInfo> items;

    public JsonEnterEvents(){
        this.result = new Result();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<JsonEnterEventInfo> getItems() {
        if(items == null){
            items = new LinkedList<JsonEnterEventInfo>();
        }
        return items;
    }

    public void setItems(List<JsonEnterEventInfo> items) {
        this.items = items;
    }
}
