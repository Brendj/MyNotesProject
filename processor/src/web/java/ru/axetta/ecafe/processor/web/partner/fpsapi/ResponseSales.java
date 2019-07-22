/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ResponseSales{
    private Result result;
    private String serverTimestamp;
    private List<SalesItem> sales;


    public ResponseSales(){
        this.result = new Result();
        this.sales = new LinkedList<SalesItem>();
    }
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getserverTimestamp() {
        return serverTimestamp;
    }

    public void setserverTimestamp(String dateserverTimestamp) {
        this.serverTimestamp = dateserverTimestamp;
    }

    public List<SalesItem> getSales() {
        return sales;
    }

    public void setSales(List<SalesItem> sales) {
        this.sales = sales;
    }
}
