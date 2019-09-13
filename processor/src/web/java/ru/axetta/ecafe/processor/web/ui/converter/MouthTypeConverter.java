/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.web.ui.report.online.MonthYearTypeMenu;
import ru.axetta.ecafe.processor.web.ui.report.online.PeriodTypeMenu;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class MouthTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return MonthYearTypeMenu.MonthTypeEnum.valueOf(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return ((MonthYearTypeMenu.MonthTypeEnum) o).name();
    }
}
