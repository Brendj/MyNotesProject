/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.02.14
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepanciesOnOrdersAndAttendanceReport {

    private final List<Item> items;

    public DiscrepanciesOnOrdersAndAttendanceReport(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }
}
