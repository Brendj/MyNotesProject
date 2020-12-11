/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.smartwatch.IJsonBase;

import java.util.LinkedList;
import java.util.List;

public class JsonTransactions implements IJsonBase {
    private Result result;
    private List<JsonTransaction> items;

    public JsonTransactions(){
        this.result = new Result();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<JsonTransaction> getItems() {
        if(this.items == null){
            this.items = new LinkedList<JsonTransaction>();
        }
        return items;
    }

    public void setItems(List<JsonTransaction> items) {
        this.items = items;
    }
}
