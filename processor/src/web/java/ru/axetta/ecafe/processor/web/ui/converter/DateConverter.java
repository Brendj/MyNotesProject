/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.lang.StringUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class DateConverter implements Converter {

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            TimeZone localTimeZone = runtimeContext
                    .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setTimeZone(localTimeZone);
            try {
                return dateFormat.parse(string);
            } catch (ParseException e) {
                throw new ConverterException(e);
            }
        } catch (RuntimeContext.NotInitializedException e) {
            throw new ConverterException(e);
        }
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (null == object) {
            return "";
        }
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            TimeZone localTimeZone = runtimeContext
                    .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setTimeZone(localTimeZone);
            return dateFormat.format((Date) object);
        } catch (RuntimeContext.NotInitializedException e) {
            throw new ConverterException(e);
        }
    }
}