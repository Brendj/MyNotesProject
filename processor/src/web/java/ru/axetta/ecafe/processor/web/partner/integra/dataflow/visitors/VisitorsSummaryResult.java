/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ResultConst;

/**
 * User: Shamil
 * Date: 15.09.14
 */
public class VisitorsSummaryResult extends Result {
    public VisitorsSummaryList orgsList;

    public VisitorsSummaryResult() {
        resultCode = ResultConst.CODE_OK;
        description = ResultConst.DESCR_OK;
    }
}
