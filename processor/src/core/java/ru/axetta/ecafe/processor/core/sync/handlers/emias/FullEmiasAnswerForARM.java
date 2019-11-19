/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.emias;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class FullEmiasAnswerForARM implements AbstractToElement {

    private List<EMIASSyncFromAnswerARMPOJO> itemsArm;
    private Long maxVersionArm;
    private List<EMIASSyncPOJO> items;

    @Override
    public Element toElement(Document document) throws Exception {
        return null;
    }

    public List<EMIASSyncFromAnswerARMPOJO> getItemsArm() {
        if (itemsArm == null)
            itemsArm = new LinkedList<>();
        return itemsArm;
    }

    public void setItemsArm(List<EMIASSyncFromAnswerARMPOJO> itemsArm) {
        this.itemsArm = itemsArm;
    }

    public Long getMaxVersionArm() {
        return maxVersionArm;
    }

    public void setMaxVersionArm(Long maxVersionArm) {
        this.maxVersionArm = maxVersionArm;
    }

    public List<EMIASSyncPOJO> getItems() {
        if (items == null)
            items = new LinkedList<>();
        return items;
    }

    public void setItems(List<EMIASSyncPOJO> items) {
        this.items = items;
    }
}