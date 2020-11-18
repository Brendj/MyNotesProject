/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.ClientSms;

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
public class SmsContentsTypeConverter implements Converter {

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        return string;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        int value = (Integer) object;
        if (value >= 0 && value < (ClientSms.CONTENTS_TYPE_DESCRIPTION.length + 4)) { //4 - это все ЕМИАС, которые уже включены в 18
            if (value > 24 && value < 29) //Если это одно из событий ЕМИАС, то это тип SPECIAL
                value = 18;
           return ClientSms.CONTENTS_TYPE_DESCRIPTION[value];
        }
        return ClientSms.UNKNOWN_CONTENTS_TYPE_DESCRIPTION;
    }
}