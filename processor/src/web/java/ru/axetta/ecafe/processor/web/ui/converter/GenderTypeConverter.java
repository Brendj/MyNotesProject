package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.web.ui.report.online.PeriodTypeMenu;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class GenderTypeConverter implements Converter{
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return Integer.parseInt(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return ((Integer) o) == 1 ? "Мужской" : "Женский";
    }
}
