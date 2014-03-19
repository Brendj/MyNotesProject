/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.core.report.statistics.good.request.DocumentStateFilter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

public class OrgRequestFilterConverter implements Converter {

    public static enum OrgRequestFilterEnum {
        ALL_ORGS("Все"),
        ORG_WITH_DATA("Только с данными");
        //ORG_WITH_OUT_DATA("Только пустые");

        private final String description;

        private OrgRequestFilterEnum(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private OrgRequestFilterEnum orgRequestFilterEnum;
    private List<SelectItem> items = readAllItems();

    public OrgRequestFilterConverter() {
        this(OrgRequestFilterEnum.ORG_WITH_DATA);
    }

    public OrgRequestFilterConverter(OrgRequestFilterEnum orgRequestFilterEnum) {
        this.orgRequestFilterEnum = orgRequestFilterEnum;
    }

    private static List<SelectItem> readAllItems() {
        OrgRequestFilterEnum[] orgRequestFilterEnums = OrgRequestFilterEnum.values();
        List<SelectItem> items = new ArrayList<SelectItem>(orgRequestFilterEnums.length);
        for (OrgRequestFilterEnum orgRequestFilter : orgRequestFilterEnums) {
            items.add(new SelectItem(orgRequestFilter, orgRequestFilter.toString()));
        }
        return items;
    }

    public OrgRequestFilterEnum getOrgRequestFilterEnum() {
        return orgRequestFilterEnum;
    }

    public void setOrgRequestFilterEnum(OrgRequestFilterEnum orgRequestFilterEnum) {
        this.orgRequestFilterEnum = orgRequestFilterEnum;
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return OrgRequestFilterEnum.valueOf(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return ((OrgRequestFilterEnum) o).name();
    }
}
