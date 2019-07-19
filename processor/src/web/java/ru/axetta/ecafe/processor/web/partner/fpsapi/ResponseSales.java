/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.smartwatch.IJsonBase;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ResponseSales implements IJsonBase {
    private Result result;
    private Date dateserverTimestamp;
    private List<SalesItem> sales;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Date getdateserverTimestamp() {
        return dateserverTimestamp;
    }

    public void setdateserverTimestamp(Date dateserverTimestamp) {
        this.dateserverTimestamp = dateserverTimestamp;
    }

    public List<SalesItem> getSales() {
        if(sales == null){
            sales = new LinkedList<SalesItem>();
        }
        return sales;
    }

    public void setSales(List<SalesItem> sales) {
        this.sales = sales;
    }
}
