/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.card.CardNoFormat;

import org.apache.commons.lang.StringUtils;
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
public class CardNoConverter implements Converter {

    private static final Logger logger = LoggerFactory.getLogger(CardNoConverter.class);

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string)
            throws ConverterException {
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        try {
            return CardNoFormat.parse(string);
        } catch (Exception e) {
            logger.error("Failed to convert card printed number", e);
            throw new ConverterException(e);
        }
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (null == object) {
            return "";
        }
        long value = (Long) object;
        return CardNoFormat.format(value);
    }
}