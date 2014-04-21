
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionFeedingListExt", propOrder = {
    "s"
})
public class SubscriptionFeedingListExt {

    @XmlElement(name = "S")
    protected List<SubscriptionFeedingExt> s;

    public List<SubscriptionFeedingExt> getS() {
        if (s == null) {
            s = new ArrayList<SubscriptionFeedingExt>();
        }
        return s;
    }
}
