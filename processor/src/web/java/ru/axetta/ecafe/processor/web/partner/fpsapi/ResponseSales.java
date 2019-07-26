/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow.Result;

import java.util.LinkedList;
import java.util.List;

public class ResponseSales extends Result {
    private List<SalesItem> sales;


    public ResponseSales(){
        this.sales = new LinkedList<SalesItem>();
    }

    public List<SalesItem> getSales() {
        return sales;
    }

    public void setSales(List<SalesItem> sales) {
        this.sales = sales;
    }
}
