/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.smartwatch.IJsonBase;

import java.util.LinkedList;
import java.util.List;

public class JsonBalanceOperations implements IJsonBase {
    private Result result = new Result();
    private List<JsonBalanceOperationsItem> items;

    @Override
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<JsonBalanceOperationsItem> getItems() {
        if(items == null){
            items = new LinkedList<JsonBalanceOperationsItem>();
        }
        return items;
    }

    public void setItems(List<JsonBalanceOperationsItem> items) {
        this.items = items;
    }
}
