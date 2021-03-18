/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class FullExemptionVisitingAnswerForARM implements AbstractToElement {

    private List<ExemptionVisitingSyncFromAnswerARMPOJO> itemsArm;
    private Long maxVersionArm;
    private List<ExemptionVisitingSyncPOJO> items;

    @Override
    public Element toElement(Document document) throws Exception {
        return null;
    }

    public List<ExemptionVisitingSyncFromAnswerARMPOJO> getItemsArm() {
        if (itemsArm == null)
            itemsArm = new LinkedList<>();
        return itemsArm;
    }

    public void setItemsArm(List<ExemptionVisitingSyncFromAnswerARMPOJO> itemsArm) {
        this.itemsArm = itemsArm;
    }

    public Long getMaxVersionArm() {
        return maxVersionArm;
    }

    public void setMaxVersionArm(Long maxVersionArm) {
        this.maxVersionArm = maxVersionArm;
    }

    public List<ExemptionVisitingSyncPOJO> getItems() {
        if (items == null)
            items = new LinkedList<>();
        return items;
    }

    public void setItems(List<ExemptionVisitingSyncPOJO> items) {
        this.items = items;
    }
}