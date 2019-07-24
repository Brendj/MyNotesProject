/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import java.util.LinkedList;
import java.util.List;

public class ResponseAverage implements IfpsapiBase{
    private Result result;
    private String serverTimestamp;
    private List<AverageItem> average;


    public ResponseAverage(){
        this.result = new Result();
        this.average = new LinkedList<AverageItem>();
    }
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getserverTimestamp() {
        return serverTimestamp;
    }

    public void setserverTimestamp(String dateserverTimestamp) {
        this.serverTimestamp = dateserverTimestamp;
    }

    public List<AverageItem> getAverage() {
        return average;
    }

    public void setAverage(List<AverageItem> average) {
        this.average = average;
    }
}
