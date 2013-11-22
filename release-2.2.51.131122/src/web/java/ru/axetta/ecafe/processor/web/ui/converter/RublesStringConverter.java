/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.apache.commons.lang.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 16.10.13
 * Time: 17:25
 */

public class RublesStringConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        String[] sums = StringUtils.split(s, ";");
        StringBuilder sb = new StringBuilder();
        try {
            for (String sum : sums) {
                String trimmedSum = StringUtils.trim(sum);
                sb.append(CurrencyStringUtils.rublesToCopecks(trimmedSum)).append(";");
            }
        } catch (Exception e) {
            throw new ConverterException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Неправильный формат денежных данных.", null));
        }
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        String[] sums = StringUtils.split((String) o, ";");
        StringBuilder sb = new StringBuilder();
        for (String sum : sums) {
            sb.append(CurrencyStringUtils.copecksToRubles(Long.parseLong(sum))).append(";");
        }
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }
}
