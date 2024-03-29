/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.core.persistence.CardOperationStation;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.07.13
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
public class CardStationConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return CardOperationStation.valueOf(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return ((CardOperationStation) o).name();
    }
}
