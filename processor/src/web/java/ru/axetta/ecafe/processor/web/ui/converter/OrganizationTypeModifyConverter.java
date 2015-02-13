/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.core.persistence.OrganizationTypeModify;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 12.02.15
 * Time: 17:45
 */

public class OrganizationTypeModifyConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return OrganizationTypeModify.valueOf(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return ((OrganizationTypeModify) o).name();
    }
}
