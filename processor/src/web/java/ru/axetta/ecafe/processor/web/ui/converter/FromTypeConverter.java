/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.converter;

import ru.axetta.ecafe.processor.web.ui.client.ClientSubAccountTransferPage;
import ru.axetta.ecafe.processor.web.ui.report.online.PeriodTypeMenu;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.07.13
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
public class FromTypeConverter implements Converter {

    public static enum FromTypeEnum {
        FROM_TO_SUB_BALANCE("в субсчет АП"),
        FROM_TO_BALANCE("в основной счет");

        private final String description;

        private FromTypeEnum(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private List<SelectItem> items = readAllItems();
    private FromTypeEnum fromType;

    public FromTypeConverter() {
        this(FromTypeEnum.FROM_TO_BALANCE);
    }

    public FromTypeConverter(FromTypeEnum periodType) {
        this.fromType = periodType;
    }

    private static List<SelectItem> readAllItems() {
        FromTypeEnum[] periodTypeEnums = FromTypeEnum.values();
        List<SelectItem> items = new ArrayList<SelectItem>(periodTypeEnums.length);
        for (FromTypeEnum periodTypeEnum : periodTypeEnums) {
            items.add(new SelectItem(periodTypeEnum, periodTypeEnum.toString()));
        }
        return items;
    }

    public FromTypeEnum getFromType() {
        return fromType;
    }

    public void setFromType(FromTypeEnum fromType) {
        this.fromType = fromType;
    }

    public List<SelectItem> getItems() {
        return items;
    }


    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return FromTypeEnum.valueOf(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return ((FromTypeEnum) o).name();
    }
}
