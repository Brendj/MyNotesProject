/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.web.internal.ResponseItem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckApplicationForFoodResult")
public class CheckApplicationForFoodResult extends Result {

    @XmlElement(name = "applicationExists")
    private Boolean applicationExists;

    public CheckApplicationForFoodResult() {

    }

    public Boolean getApplicationExists() {
        return applicationExists;
    }

    public void setApplicationExists(Boolean applicationExists) {
        this.applicationExists = applicationExists;
    }
}
