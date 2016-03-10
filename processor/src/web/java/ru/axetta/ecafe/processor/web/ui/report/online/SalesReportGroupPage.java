/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Akhmetov
 * Date: 03.03.16
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SalesReportGroupPage extends BasicWorkspacePage {

    public boolean getEligibleToWorkCommodityAccounting() throws Exception {
        return true;
    }
}