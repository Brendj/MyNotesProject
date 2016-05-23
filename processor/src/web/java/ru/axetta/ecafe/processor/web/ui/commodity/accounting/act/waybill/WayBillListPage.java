/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.WayBill;
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
public class WayBillListPage extends AbstractListPage<WayBill, WayBillItem> implements OrgSelectPage.CompleteHandler {

    private WayBillFilter filter = new WayBillFilter();


    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.filter.setShortName(org.getShortName());
            this.filter.setIdOfOrg(idOfOrg);
        }
    }

    @Override
    protected String getPageFileName() {
        return "commodity_accounting/waybill/waybill";
    }

    @Override
    protected Class<WayBill> getEntityClass() {
        return WayBill.class;
    }

    @Override
    protected WayBillItem createItem() {
        return new WayBillItem();
    }

    @Override
    protected String getSortField() {
        return "createdDate";
    }

    @Override
    public WayBillFilter getFilter() {
        return filter;
    }
}
