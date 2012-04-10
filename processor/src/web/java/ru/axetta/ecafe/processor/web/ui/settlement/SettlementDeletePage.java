/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.settlement;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.CurrentPositionsManager;
import ru.axetta.ecafe.processor.core.persistence.Settlement;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class SettlementDeletePage extends BasicPage {
    public void removeSettlement(Session session, Long id) throws Exception {
        Settlement settlement = (Settlement) session.get(Settlement.class, id);
        RuntimeContext.getFinancialOpsManager().deleteSettlement(session, settlement);
    }
}
