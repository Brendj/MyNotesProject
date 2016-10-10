/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 10.10.16
 * Time: 10:30
 */

@Component
@Scope("session")
public class ElectronicReconciliationReportGroupPage extends BasicWorkspacePage {

    public boolean getEligibleToWorkCommodityAccounting() throws Exception {
        return true;
    }

}
