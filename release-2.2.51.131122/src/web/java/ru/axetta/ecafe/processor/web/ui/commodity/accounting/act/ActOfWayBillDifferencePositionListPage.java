/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.ActOfWayBillDifferencePosition;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.12
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ActOfWayBillDifferencePositionListPage extends AbstractListPage<ActOfWayBillDifferencePosition, ActOfWayBillDifferencePositionItem> implements OrgSelectPage.CompleteHandler {

    private ActOfWayBillDifferencePositionFilter filter = new ActOfWayBillDifferencePositionFilter();
    private String shortName;

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            shortName = org.getShortName();
            this.filter.setIdOfOrg(idOfOrg);
        }
    }

    @Override
    protected String getPageFileName() {
        return "commodity_accounting/acts/waybilldifferenceposition_acts";
    }

    @Override
    protected Class<ActOfWayBillDifferencePosition> getEntityClass() {
        return ActOfWayBillDifferencePosition.class;
    }

    @Override
    protected ActOfWayBillDifferencePositionItem createItem() {
        return new ActOfWayBillDifferencePositionItem();
    }

    @Override
    protected String getSortField() {
        return "createdDate";
    }

    @Override
    public ActOfWayBillDifferencePositionFilter getFilter() {
        return filter;
    }

    public String getShortName() {
        return shortName;
    }

}
