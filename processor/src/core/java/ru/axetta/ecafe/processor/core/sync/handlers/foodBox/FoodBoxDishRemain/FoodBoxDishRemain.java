/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxDishRemain;

import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.util.HashSet;
import java.util.Set;

public class FoodBoxDishRemain implements SectionRequest {

    public static final String SECTION_NAME = "FoodBoxDishRemain";
    private Long maxVersion;
    private Set<FoodBoxAvailableItem> items = new HashSet<>();

    public FoodBoxDishRemain(Node sectionElement) {
        this.maxVersion = XMLUtils.getLongAttributeValue(sectionElement, "V");
        Node nodeElement = sectionElement.getFirstChild();
        while (nodeElement != null) {
            if (nodeElement.getNodeName().equals("FBR")) {
                FoodBoxAvailableItem foodBoxAvailable = new FoodBoxAvailableItem();
                foodBoxAvailable.setIdOfDish(XMLUtils.getLongAttributeValue(nodeElement, "IdOfDish"));
                foodBoxAvailable.setAvailableQty(XMLUtils.getIntegerAttributeValue(nodeElement, "AvailableQty"));
                items.add(foodBoxAvailable);
            }
            nodeElement = nodeElement.getNextSibling();
        }
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }

    public Set<FoodBoxAvailableItem> getItems() {
        if (items == null) {
            items = new HashSet<>();
        }
        return items;
    }

    public void setItems(Set<FoodBoxAvailableItem> items) {
        this.items = items;
    }
}
