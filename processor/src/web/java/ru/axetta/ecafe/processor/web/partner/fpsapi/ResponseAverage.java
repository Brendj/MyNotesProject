/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow.Result;

import java.util.LinkedList;
import java.util.List;

public class ResponseAverage extends Result{
    private List<AverageItem> average;


    public ResponseAverage(){
        this.average = new LinkedList<AverageItem>();
    }

    public List<AverageItem> getAverage() {
        return average;
    }

    public void setAverage(List<AverageItem> average) {
        this.average = average;
    }
}
