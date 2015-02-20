/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 16.02.15
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPublicationListSimpleResponse", propOrder = {
        "_return"
})
public class GetPublicationListSimpleResponse {
    @XmlElement(name = "return")
    protected PublicationListResult _return;

    public PublicationListResult getReturn() {
        return _return;
    }

    public void setReturn(PublicationListResult value) {
        this._return = value;
    }
}
