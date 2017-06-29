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
@XmlType(name = "ManualEvents")
public class ManualEvents {
    @XmlElement(name = "event")
    private List<EnterEventManualItem> enterEventsManual;

    public List<EnterEventManualItem> getEnterEventsManual() {
        return enterEventsManual;
    }

    public void setEnterEventsManual(List<EnterEventManualItem> enterEventsManual) {
        this.enterEventsManual = enterEventsManual;
    }
}
