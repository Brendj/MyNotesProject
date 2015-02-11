/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.totalbeneffeedreport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 06.02.15
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
public class TotalBenefFeedData {

    List<TotalBenefFeedItem> itemList = new ArrayList<TotalBenefFeedItem>();

    public TotalBenefFeedData() {
    }


    public List<TotalBenefFeedItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TotalBenefFeedItem> itemList) {
        this.itemList = itemList;
    }
}
