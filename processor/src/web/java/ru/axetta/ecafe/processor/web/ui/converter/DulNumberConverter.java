package ru.axetta.ecafe.processor.web.ui.converter;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.DulGuide;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class DulNumberConverter implements Converter{

    private static final Logger logger = LoggerFactory.getLogger(DulNumberConverter.class);

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if (o == null)
            return "";
        StringBuilder number = new StringBuilder((String) o);
        if (number.length() > 1) {
            int isEven = number.length()%2==0 ? 0 : 1;
            for (int i = 0; i < number.length() / 2 + isEven; i ++){
                number.setCharAt(i, '*');
            }
        }
        return number.toString();
    }
}
