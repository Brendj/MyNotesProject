
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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestionaryList", propOrder = {
    "q"
})
public class QuestionaryList {

    @XmlElement(name = "Q")
    protected List<QuestionaryItem> q;

    public List<QuestionaryItem> getQ() {
        if (q == null) {
            q = new ArrayList<QuestionaryItem>();
        }
        return this.q;
    }

}
