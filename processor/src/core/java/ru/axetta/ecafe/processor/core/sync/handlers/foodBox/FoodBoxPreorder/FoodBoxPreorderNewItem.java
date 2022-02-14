/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxPreorder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxStateTypeEnum;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FoodBoxPreorderNewItem {
    private Long id;
    private FoodBoxStateTypeEnum state;
    private Long idOfClient;
    private Date InitialDateTime;
    private Long version;
    private List<FoodBoxPreorderNewItemItem> items;


    public FoodBoxPreorderNewItem() {

    }

    public FoodBoxPreorderNewItem(Long id, FoodBoxStateTypeEnum state, Long idOfClient, Date InitialDateTime, Long version) {
        this.id = id;
        this.state = state;
        this.idOfClient = idOfClient;
        this.InitialDateTime = InitialDateTime;
        this.version = version;
    }
    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Id", id);
        XMLUtils.setAttributeIfNotNull(element, "State", state.getValue());
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", idOfClient);
        XMLUtils.setAttributeIfNotNull(element, "InitialDateTime", InitialDateTime);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        for (FoodBoxPreorderNewItemItem item : this.getItems()) {
            element.appendChild(item.toElement(document,"FBPD"));
        }
        return element;
    }


    public List<FoodBoxPreorderNewItemItem> getItems() {
        if (items == null)
            items = new ArrayList<>();
        return items;
    }

    public void setItems(List<FoodBoxPreorderNewItemItem> items) {
        this.items = items;
    }
}
