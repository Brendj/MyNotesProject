/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxChanged;

import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxStateTypeEnum;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.util.HashSet;
import java.util.Set;

public class FoodBoxPreorderChanged implements SectionRequest {

    public static final String SECTION_NAME = "FoodBoxPreorder";
    private Set<FoodBoxPreorderChangedItem> items = new HashSet<>();

    public FoodBoxPreorderChanged() {
    }

    public FoodBoxPreorderChanged(Node sectionElement) throws Exception {
        Node nodeElement = sectionElement.getFirstChild();
        while (nodeElement != null) {
            if (nodeElement.getNodeName().equals("FBP")) {
                FoodBoxPreorderChangedItem foodBoxAvailableItem = new FoodBoxPreorderChangedItem();
                foodBoxAvailableItem.setId(XMLUtils.getLongAttributeValue(nodeElement, "Id"));
                foodBoxAvailableItem.setError(XMLUtils.getStringValueNullSafe(nodeElement, "Error"));
                foodBoxAvailableItem.setIdOfFoodBox(XMLUtils.getLongAttributeValue(nodeElement, "IdOfFoodBox"));
                foodBoxAvailableItem.setCellNumber(XMLUtils.getIntegerAttributeValue(nodeElement, "CellNumber"));
                foodBoxAvailableItem.setState(
                        FoodBoxStateTypeEnum.fromValue(
                                XMLUtils.getIntegerAttributeValue(nodeElement, "State")));
                foodBoxAvailableItem.setIdOfOrder(XMLUtils.getLongAttributeValue(nodeElement, "IdOfOrder"));
                foodBoxAvailableItem.setCancelReason(XMLUtils.getIntegerAttributeValue(nodeElement, "CancelReason"));
                items.add(foodBoxAvailableItem);
            }
            nodeElement = nodeElement.getNextSibling();
        }
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Set<FoodBoxPreorderChangedItem> getItems() {
        if (items == null)
            items = new HashSet<>();
        return items;
    }

    public void setItems(Set<FoodBoxPreorderChangedItem> items) {
        this.items = items;
    }
}
