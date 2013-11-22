/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.12
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class CommodityAccountingGroupPage extends BasicWorkspacePage {

    public boolean getEligibleToWorkCommodityAccounting() throws Exception {
        return ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToCommodityAccounting();
    }


}
