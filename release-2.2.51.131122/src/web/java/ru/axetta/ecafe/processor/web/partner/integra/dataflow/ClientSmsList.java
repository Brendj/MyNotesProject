/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 01.08.12
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientSmsList", propOrder = {
        "s"
})
public class ClientSmsList {
    @XmlElement(name = "S")
    protected List<Sms> s;

    public List<Sms> getS() {
        if (s == null) {
            s = new ArrayList<Sms>();
        }
        return this.s;
    }
}
