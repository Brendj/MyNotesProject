/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 10.06.13
 * Time: 18:01
 * To change this template use File | Settings | File Templates.
 */
public class ManualReportConverter implements Converter {

    private static final Logger logger = LoggerFactory.getLogger(PhoneConverter.class);

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        try {
            return string.toString();
        } catch (Exception e) {
            logger.error("Failed to convert phone number", e);
            throw new ConverterException(e);
        }
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (null == object) {
            return null;
        }
        return (String) object;
    }
}
