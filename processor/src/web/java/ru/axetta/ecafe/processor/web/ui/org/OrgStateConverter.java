/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Org;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class OrgStateConverter implements Converter {

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        return string;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        int value = (Integer) object;
        if (value >= 0 && value < Org.STATE_NAMES.length) {
            return Org.STATE_NAMES[value];
        }
        return Org.UNKNOWN_STATE_NAME;
    }
}