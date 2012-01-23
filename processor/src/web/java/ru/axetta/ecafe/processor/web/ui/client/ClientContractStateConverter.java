/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Client;

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
public class ClientContractStateConverter implements Converter {

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        return string;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        int value = (Integer) object;
        if (value >= 0 && value < Client.CONTRACT_STATE_NAMES.length) {
            return Client.CONTRACT_STATE_NAMES[value];
        }
        return Client.UNKNOWN_CONTRACT_STATE_NAME;
    }
}