/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.totalsales;

import ru.axetta.ecafe.processor.core.report.TotalSalesReportItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 25.01.15
 * Time: 0:03
 * To change this template use File | Settings | File Templates.
 */
public class TotalSalesData {
    private String name;
    private List<TotalSalesItem> itemList;
    private List<TotalSalesReportItem> totalSalesReportItemList;

    public TotalSalesData() {
        itemList = new ArrayList<TotalSalesItem>();
    }

    public TotalSalesData(String name, List<TotalSalesReportItem> totalSalesReportItemList) {
        this.name = name;
        itemList = new ArrayList<TotalSalesItem>();
        this.totalSalesReportItemList = totalSalesReportItemList;
    }

    public List<TotalSalesItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TotalSalesItem> itemList) {
        this.itemList = itemList;
    }

    public List<TotalSalesReportItem> getTotalSalesReportItemList() {
        return totalSalesReportItemList;
    }

    public void setTotalSalesReportItemList(List<TotalSalesReportItem> totalSalesReportItemList) {
        this.totalSalesReportItemList = totalSalesReportItemList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
