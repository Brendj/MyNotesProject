/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.OrganizationTypeModify;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 12.02.15
 * Time: 17:33
 */

public class OrganizationTypeModifyMenu {

    private List<SelectItem> customItems = readCustomItems();

    private static List<SelectItem> readCustomItems() {
        OrganizationTypeModify[] organizationTypeModifies = OrganizationTypeModify.values();
        List<SelectItem> items = new ArrayList<SelectItem>(organizationTypeModifies.length);
        for (OrganizationTypeModify organizationType : organizationTypeModifies) {
            items.add(new SelectItem(organizationType, organizationType.toString()));
        }
        return items;
    }

    public List<SelectItem> getCustomItems() {
        return customItems;
    }

    public void setCustomItems(List<SelectItem> customItems) {
        this.customItems = customItems;
    }

}
