/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKMoneyConfig;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 27.07.12
 * Time: 14:24
 * To change this template use File | Settings | File Templates.
 */
public class RBKMoneyConfigResult {
    public RBKMoneyConfigExt rbkConfig;
    public Long resultCode;
    public String description;
    public RBKMoneyConfigResult(){}
    public RBKMoneyConfigResult(RBKMoneyConfigExt rbkConfig,Long resultCode,String description){
        this.rbkConfig=rbkConfig;
        this.resultCode=resultCode;
        this.description=description;
    }
}
