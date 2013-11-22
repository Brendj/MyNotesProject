/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class AddressListConverter implements Converter {

    private static final Logger logger = LoggerFactory.getLogger(AddressListConverter.class);
    private static final int MAX_LEN = 64;
    private static final String TAIL_FILL = "...";

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            boolean first = true;
            for (Object currObject : (List) object) {
                String address = (String) currObject;
                if (StringUtils.isNotEmpty(address)) {
                    if (!first) {
                        stringBuffer.append(", ");
                    }
                    stringBuffer.append(address);
                    first = false;
                    if (stringBuffer.length() > MAX_LEN) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to convert function set", e);
            throw new ConverterException(e);
        }
        int len = stringBuffer.length();
        if (len > MAX_LEN) {
            return stringBuffer.substring(0, MAX_LEN - TAIL_FILL.length()) + TAIL_FILL;
        }
        return stringBuffer.toString();
    }

}