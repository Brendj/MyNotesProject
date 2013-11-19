/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill_old;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.WayBillPosition;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill.WayBillItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.12
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */

public class WayBillPositionListPage extends AbstractListPage<WayBillPosition, WayBillPositionItem> /*implements OrgSelectPage.CompleteHandler*/ {

    private WayBillPositionFilter filter = new WayBillPositionFilter();
    private String shortName;
    private WayBillItem wayBillItem;

    /*@Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            shortName = org.getShortName();
            this.filter.setIdOfOrg(idOfOrg);
        }
    }*/

    @Override
    protected String getPageFileName() {
        return "commodity_accounting/waybill/waybillposition";
    }

    @Override
    protected Class<WayBillPosition> getEntityClass() {
        return WayBillPosition.class;
    }

    @Override
    protected WayBillPositionItem createItem() {
        return new WayBillPositionItem();
    }

    @Override
    protected String getSortField() {
        return "createdDate";
    }

    @Override
    public WayBillPositionFilter getFilter() {
        return filter;
    }

    public void setFilter(WayBillPositionFilter filter) {
        this.filter = filter;
    }

    public String getShortName() {
        return shortName;
    }

    public WayBillItem getWayBillItem() {
        return wayBillItem;
    }

    public void setWayBillItem(WayBillItem wayBillItem) {
        this.wayBillItem = wayBillItem;
    }
}
