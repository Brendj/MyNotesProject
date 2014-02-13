/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.OrganizationType;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 26.06.2009
 * Time: 11:12:14
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationTypeSwitchMenu {

    public enum OrganizationTypeSwitch {

        /*0*/ SCHOOL("Школа"),
        /*1*/ KINDERGARTEN("Садик"),
        /*2*/ SCHOOL_KINDERGARTEN("Школы и садики"),
        /*3*/ SUPPLIER("Поставщик питания"),
        /*4*/ ALL_ORGS("Все");

        private final String description;

        private OrganizationTypeSwitch(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private List<SelectItem> items = readAllItems();

    private static List<SelectItem> readAllItems() {
        OrganizationTypeSwitch[] organizationTypes = OrganizationTypeSwitch.values();
        List<SelectItem> items = new ArrayList<SelectItem>(organizationTypes.length);
        for (OrganizationTypeSwitch organizationType: organizationTypes){
            items.add(new SelectItem(organizationType, organizationType.toString()));
        }
        return items;
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }
}
