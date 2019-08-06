/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow.Result;

import java.util.LinkedList;
import java.util.List;

public class ResponseEnterEvent extends Result {
    private List<EnterEventItem> enterEvents;

    public ResponseEnterEvent(){
        this.enterEvents = new LinkedList<EnterEventItem>();
    }

    public List<EnterEventItem> getEnterEvents() {
        return enterEvents;
    }

    public void setEnterEvents(List<EnterEventItem> enterEvents) {
        this.enterEvents = enterEvents;
    }
}