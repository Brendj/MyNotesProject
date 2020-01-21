/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Created by IntelliJ IDEA.
 * User: Olga Petrova
 * Date: 21.01.2020
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class QuantityConverter implements Converter {

    private static final Logger logger = LoggerFactory.getLogger(QuantityConverter.class);

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string)
            throws ConverterException {
        //if (StringUtils.isEmpty(string) || string == null) {
        //    return "";
        //}
        //try {
        //    return Integer.parseInt(string);
        //} catch (Exception e) {
        //    logger.error("Failed to convert qty", e);
        //    throw new ConverterException(e);
        //}

        Integer result = null;
        if (string != null && string.trim().length() > 0) {
            try {
                result = new Integer(string);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        } else {
            //string = null;
            ((EditableValueHolder) uiComponent).setSubmittedValue(null);
        }
        return result;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (object == null) {
            return "";
        }
        return ((Integer) object).toString();
    }
}
