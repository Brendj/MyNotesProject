/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.smartwatch.IJsonBase;

import java.util.LinkedList;
import java.util.List;

public class JsonLocations implements IJsonBase {
    private Result result;
    private List<JsonLocationsInfo> locations;

    public JsonLocations(){
        result = new Result();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<JsonLocationsInfo> getLocations() {
        if(locations == null){
            locations = new LinkedList<JsonLocationsInfo>();
        }
        return locations;
    }

    public void setLocations(List<JsonLocationsInfo> locations) {
        this.locations = locations;
    }
}
