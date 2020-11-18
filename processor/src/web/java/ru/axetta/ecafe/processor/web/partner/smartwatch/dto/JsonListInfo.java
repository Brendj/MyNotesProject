/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.smartwatch.IJsonBase;

import java.util.LinkedList;
import java.util.List;

public class JsonListInfo implements IJsonBase {
    private Result result;
    private List<JsonChildrenDataInfoItem> items;

    public JsonListInfo(){
        this.result = new Result();
        this.items = new LinkedList<JsonChildrenDataInfoItem>();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<JsonChildrenDataInfoItem> getItems() {
        if(items == null){
            this.items = new LinkedList<JsonChildrenDataInfoItem>();
        }
        return items;
    }

    public void setItems(List<JsonChildrenDataInfoItem> items) {
        this.items = items;
    }
}
