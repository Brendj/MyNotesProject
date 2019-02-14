/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

public class JsonLocations {
    private Result result;
    private JsonLocationsInfo locations;

    public JsonLocations(){
        result = new Result();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public JsonLocationsInfo getLocations() {
        return locations;
    }

    public void setLocations(JsonLocationsInfo locations) {
        this.locations = locations;
    }
}
