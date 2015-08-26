/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 26.08.15
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class FinancialControlPage extends BasicWorkspacePage {

    public boolean getEligibleToWorkCommodityAccounting() throws Exception {
        return true;
    }
}
