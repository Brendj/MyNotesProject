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

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 17.09.14
 * Time: 16:23
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionFeedingJournalListExt", propOrder = {"s"})

public class SubscriptionFeedingJournalListExt {

    @XmlElement(name = "S")
    protected List<SubscriptionFeedingJournalExt> s;

    public List<SubscriptionFeedingJournalExt> getS() {
        if (s == null) {
            s = new ArrayList<SubscriptionFeedingJournalExt>();
        }
        return s;
    }
}
