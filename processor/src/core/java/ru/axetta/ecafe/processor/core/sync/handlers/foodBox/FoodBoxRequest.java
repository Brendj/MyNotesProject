/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;

import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.util.LinkedList;
import java.util.List;

public class FoodBoxRequest implements SectionRequest {

    public static final String SECTION_NAME = "FoodBoxDishRemain";
    private Long maxVersion;
    private List<FoodBoxAvailableItem> items = new LinkedList<>();

    public FoodBoxRequest(Node sectionElement) throws Exception {
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

    public List<FoodBoxAvailableItem> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<FoodBoxAvailableItem> items) {
        this.items = items;
    }
}
