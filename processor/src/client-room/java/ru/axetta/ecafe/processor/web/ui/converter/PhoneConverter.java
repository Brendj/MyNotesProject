/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class PhoneConverter implements Converter {

    private static final Logger logger = LoggerFactory.getLogger(PhoneConverter.class);

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        try {
            return PhoneNumberCanonicalizator.canonicalize(string);
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