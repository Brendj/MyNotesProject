/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.emias;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class EmiasSectionForARMAnswer implements AbstractToElement {

    private List<EMIASSyncFromAnswerARMPOJO> items;
    private Long maxVersion;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResEMIAS");
        element.setAttribute("V", maxVersion.toString());
        for (EMIASSyncFromAnswerARMPOJO item : getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<EMIASSyncFromAnswerARMPOJO> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        else {
            Collections.sort(items, new Comparator<EMIASSyncFromAnswerARMPOJO>() {
                public int compare(EMIASSyncFromAnswerARMPOJO o1, EMIASSyncFromAnswerARMPOJO o2) {
                    return o1.getIdEventEMIAS().compareTo(o2.getIdEventEMIAS());
                }
            });
        }
        return items;
    }

    public void setItems(List<EMIASSyncFromAnswerARMPOJO> items) {
        this.items = items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }
}