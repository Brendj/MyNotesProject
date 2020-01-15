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

    private boolean firstAsc = true;
    public MonthTypeEnum getMounthType() {
        if (firstAsc) {
            firstAsc = false;
            return translateNumberToStringMonth();
        }
        else
            return mounthType;
    }

    //Определяем текущий месяц
    private MonthTypeEnum translateNumberToStringMonth()
    {
        Integer num = CalendarUtils.getCurrentMonth();
        num++;
        switch (num) {
            case (1): return MonthTypeEnum.JANUARY;
            case (2): return MonthTypeEnum.FEBRUARY;
            case (3): return MonthTypeEnum.MARCH;
            case (4): return MonthTypeEnum.APRIL;
            case (5): return MonthTypeEnum.MAY;
            case (6): return MonthTypeEnum.JUNE;
            case (7): return MonthTypeEnum.JULY;
            case (8): return MonthTypeEnum.AUGUST;
            case (9): return MonthTypeEnum.SEPTEMBER;
            case (10): return MonthTypeEnum.OCTOBER;
            case (11): return MonthTypeEnum.NOVEMBER;
            case (12): return MonthTypeEnum.DECEMBER;
            default: return MonthTypeEnum.JANUARY;
        }
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
}
