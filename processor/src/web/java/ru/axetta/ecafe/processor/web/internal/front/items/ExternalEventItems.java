/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created by i.semenov on 29.06.2017.
 */
public class ExternalEventItems {
    private ExternalEvents externalEvents;
    private ManualEvents manualEvents;

    public ExternalEvents getExternalEvents() {
        return externalEvents == null ? new ExternalEvents() : externalEvents;
    }

    public void setExternalEvents(ExternalEvents externalEvents) {
        this.externalEvents = externalEvents;
    }

    public ManualEvents getManualEvents() {
        return manualEvents;
    }

    public void setManualEvents(ManualEvents manualEvents) {
        this.manualEvents = manualEvents;
    }
}
