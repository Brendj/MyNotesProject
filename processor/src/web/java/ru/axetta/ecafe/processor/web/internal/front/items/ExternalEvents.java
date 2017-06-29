/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by i.semenov on 29.06.2017.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalEvents")
public class ExternalEvents {
    @XmlElement(name="event")
    private List<ExternalEventItem> externalEvents;

    public List<ExternalEventItem> getExternalEvents() {
        return externalEvents;
    }

    public void setExternalEvents(List<ExternalEventItem> externalEvents) {
        this.externalEvents = externalEvents;
    }
}
