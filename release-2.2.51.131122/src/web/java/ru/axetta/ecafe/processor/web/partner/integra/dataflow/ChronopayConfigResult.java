/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayConfig;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKMoneyConfig;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 27.07.12
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class ChronopayConfigResult {
    public ChronopayConfigExt chronopayConfig;
    public Long resultCode;
    public String description;
    public ChronopayConfigResult(){}
    public ChronopayConfigResult(ChronopayConfigExt chronopayConfig,Long resultCode,String description){
        this.chronopayConfig=chronopayConfig;
        this.resultCode=resultCode;
        this.description=description;
    }
}
