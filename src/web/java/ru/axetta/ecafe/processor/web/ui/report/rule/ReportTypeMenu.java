/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import javax.faces.model.SelectItem;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class ReportTypeMenu {

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[ReportRuleConstants.REPORT_HINTS.length];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(ReportRuleConstants.REPORT_HINTS[i].getTypeName());
        }
        return items;
    }

    public SelectItem[] getItems() {
        return items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }
}