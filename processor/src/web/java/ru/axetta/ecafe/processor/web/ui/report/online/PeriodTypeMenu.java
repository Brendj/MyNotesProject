/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 26.06.2009
 * Time: 11:12:14
 * To change this template use File | Settings | File Templates.
 */
public class PeriodTypeMenu {

    public enum PeriodTypeEnum {
        ONE_DAY("1 день"),
        ONE_WEEK("1 неделя"),
        TWO_WEEK("2 недели"),
        ONE_MONTH("1 месяц"),
        FIXED_DAY("Точная дата");

        private final String description;

        private PeriodTypeEnum(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private List<SelectItem> items = readAllItems();
    private List<SelectItem> itemsShort = readAllItemsShort();
    private PeriodTypeMenu.PeriodTypeEnum periodType;

    public PeriodTypeMenu() {
        this(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
    }

    public PeriodTypeMenu(PeriodTypeEnum periodType) {
        this.periodType = periodType;
    }

    private static List<SelectItem> readAllItems() {
        PeriodTypeEnum[] periodTypeEnums = PeriodTypeEnum.values();
        List<SelectItem> items = new ArrayList<SelectItem>(periodTypeEnums.length);
        for (PeriodTypeEnum periodTypeEnum : periodTypeEnums) {
            items.add(new SelectItem(periodTypeEnum, periodTypeEnum.toString()));
        }
        return items;
    }

    private List<SelectItem> readAllItemsShort() {
        PeriodTypeEnum[] periodTypeEnums = PeriodTypeEnum.values();
        List<SelectItem> itemsShort = new ArrayList<SelectItem>(periodTypeEnums.length);
        for (PeriodTypeEnum periodTypeEnum : periodTypeEnums) {
            if (!periodTypeEnum.toString().equals("2 недели") && !periodTypeEnum.toString().equals("1 месяц") && !periodTypeEnum.toString().equals("Точная дата")) {
                itemsShort.add(new SelectItem(periodTypeEnum, periodTypeEnum.toString()));
            }
        }
        return itemsShort;
    }

    public PeriodTypeEnum getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodTypeEnum periodType) {
        this.periodType = periodType;
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }

    public List<SelectItem> getItemsShort() {
        return itemsShort;
    }

    public void setItemsShort(List<SelectItem> itemsShort) {
        this.itemsShort = itemsShort;
    }
}
