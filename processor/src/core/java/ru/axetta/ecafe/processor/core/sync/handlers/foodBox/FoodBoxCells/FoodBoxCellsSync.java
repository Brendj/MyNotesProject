/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxCells;

import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.util.HashSet;
import java.util.Set;

public class FoodBoxCellsSync implements SectionRequest {

    public static final String SECTION_NAME = "FoodBoxesList";
    private Set<FoodBoxCellsItem> items = new HashSet<>();
    public FoodBoxCellsSync() {
    }

    public FoodBoxCellsSync(Node sectionElement) throws Exception {
        Node nodeElement = sectionElement.getFirstChild();
        while (nodeElement != null) {
            if (nodeElement.getNodeName().equals("FB")) {
                FoodBoxCellsItem foodBoxCellsItem = new FoodBoxCellsItem();
                foodBoxCellsItem.setIdFoodBox(XMLUtils.getLongAttributeValue(nodeElement, "Id"));
                foodBoxCellsItem.setTotalCellsCount(XMLUtils.getLongAttributeValue(nodeElement, "TotalCellsCount"));
                items.add(foodBoxCellsItem);
            }
            nodeElement = nodeElement.getNextSibling();
        }
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Set<FoodBoxCellsItem> getItems() {
        if (items == null)
            items = new HashSet<>();
        return items;
    }

    public void setItems(Set<FoodBoxCellsItem> items) {
        this.items = items;
    }

}
