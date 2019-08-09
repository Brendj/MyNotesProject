/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Voinov on 09.08.19.
 */
public class MonthYearTypeMenu {
    private static int RANGE_YEARS = 10;
    private static List<Integer> years;
    private Integer selectedYear;
    private MonthTypeEnum mounthType;
    private List<SelectItem> itemsMonth = readAllItems();
    private List<SelectItem> itemsYears = readAllItemsYear();

    private static List<SelectItem> readAllItems() {
        MonthTypeEnum[] periodTypeEnums = MonthTypeEnum.values();
        List<SelectItem> items = new ArrayList<SelectItem>(periodTypeEnums.length);
        for (MonthTypeEnum periodTypeEnum : periodTypeEnums) {
            items.add(new SelectItem(periodTypeEnum, periodTypeEnum.toString()));
        }
        return items;
    }

    private static List<SelectItem> readAllItemsYear() {
        List<SelectItem> items = new ArrayList<SelectItem>(getYears().size());
        for (Integer year : getYears()) {
            items.add(new SelectItem(year, year.toString()));
        }
        return items;
    }

    public static void generatePeriodYears (Integer size)
    {
        for (int i = CalendarUtils.getCurrentYear(); i>(CalendarUtils.getCurrentYear()-size);i--)
        {
            getYears().add(i);
        }
    }

    public enum MonthTypeEnum {
        JANUARY("Январь"),
        FEBRUARY("Февраль"),
        MARCH("Март"),
        APRIL("Апрель"),
        MAY("Май"),
        JUNE("Июнь"),
        JULY("Июль"),
        AUGUST("Август"),
        SEPTEMBER("Сентябрь"),
        OCTOBER("Октябрь"),
        NOVEMBER("Ноябрь"),
        DECEMBER("Декабрь");

        private final String description;

        private MonthTypeEnum(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public static List<Integer> getYears() {
        if (years == null) {
            years = new ArrayList<>();
            generatePeriodYears (RANGE_YEARS);
        }
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public MonthTypeEnum getMounthType() {
        return mounthType;
    }

    public void setMounthType(MonthTypeEnum mounthType) {
        this.mounthType = mounthType;
    }

    public List<SelectItem> getItemsMonth() {
        return itemsMonth;
    }

    public void setItemsMonth(List<SelectItem> itemsMonth) {
        this.itemsMonth = itemsMonth;
    }

    public List<SelectItem> getItemsYears() {
        return itemsYears;
    }

    public void setItemsYears(List<SelectItem> itemsYears) {
        this.itemsYears = itemsYears;
    }

    public Integer getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(Integer selectedYear) {
        this.selectedYear = selectedYear;
    }
}
