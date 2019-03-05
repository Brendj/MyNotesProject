/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

public class JsonBalance implements IJsonBase {
    private Result result = new Result();
    private JsonBalanceInfo balanceInfo;


    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public JsonBalanceInfo getBalanceInfo() {
        return balanceInfo;
    }

    public void setBalanceInfo(JsonBalanceInfo balanceInfo) {
        this.balanceInfo = balanceInfo;
    }
}
