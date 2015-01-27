/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.totalsales;

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

    public TotalSalesData() {
        itemList = new ArrayList<TotalSalesItem>();
    }

    public TotalSalesData(String name) {
        this.name = name;
        itemList = new ArrayList<TotalSalesItem>();
    }

    public List<TotalSalesItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TotalSalesItem> itemList) {
        this.itemList = itemList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
