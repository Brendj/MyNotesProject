/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import ru.axetta.ecafe.processor.core.daoservices.order.items.ClientReportItem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientOrderDetailsByAllOrgList", propOrder = {
        "c"
})
public class ClientOrderDetailsByAllOrgList {
    @XmlElement(name = "C")
    protected List<ClientReportItem> c;

    public List<ClientReportItem> getC() {
        if (c == null) {
            c = new ArrayList<ClientReportItem>();
        }
        return c;
    }

    public void setC(List<ClientReportItem> c) {
        this.c = c;
    }
}
