/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.settlement;

import ru.axetta.ecafe.processor.core.persistence.Settlement;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 25.11.11
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
public class SelectedSettlementGroupPage extends BasicWorkspacePage {
    private String name;

    public String getName() {
        return name;
    }

    public void fill(Session session, Long idOfSettlement) throws Exception {
        Settlement settlement = (Settlement) session.load(Settlement.class, idOfSettlement);
        this.name = settlement.getIdOfContragentPayer().getContragentName() + " - "
                    + settlement.getIdOfContragentReceiver().getContragentName();
    }

}
