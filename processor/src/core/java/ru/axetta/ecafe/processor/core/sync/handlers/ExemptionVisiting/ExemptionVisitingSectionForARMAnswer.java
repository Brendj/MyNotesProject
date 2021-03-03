/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ExemptionVisitingSectionForARMAnswer implements AbstractToElement {

    private List<ExemptionVisitingSyncFromAnswerARMPOJO> items;
    private Long maxVersion;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResExemptionVisiting");
        element.setAttribute("V", maxVersion.toString());
        for (ExemptionVisitingSyncFromAnswerARMPOJO item : getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<ExemptionVisitingSyncFromAnswerARMPOJO> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        else {
            Collections.sort(items, new Comparator<ExemptionVisitingSyncFromAnswerARMPOJO>() {
                public int compare(ExemptionVisitingSyncFromAnswerARMPOJO o1, ExemptionVisitingSyncFromAnswerARMPOJO o2) {
                    return o1.getIdEventEMIAS().compareTo(o2.getIdEventEMIAS());
                }
            });
        }
        return items;
    }

    public void setItems(List<ExemptionVisitingSyncFromAnswerARMPOJO> items) {
        this.items = items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }
}