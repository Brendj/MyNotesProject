/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

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
public class CopeckSumConverter implements Converter {

    private static final Logger logger = LoggerFactory.getLogger(CopeckSumConverter.class);

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string)
            throws ConverterException {
        if (null == string) {
            return null;
        }
        if (StringUtils.isEmpty(string)) {
            return 0L;
        }
        try {
            return CurrencyStringUtils.rublesToCopecks(string);
        } catch (Exception e) {
            logger.error("Failed to convert sum", e);
            throw new ConverterException(e);
        }
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (null == object) {
            return "";
        }
        return CurrencyStringUtils.copecksToRubles((Long) object);
    }
}