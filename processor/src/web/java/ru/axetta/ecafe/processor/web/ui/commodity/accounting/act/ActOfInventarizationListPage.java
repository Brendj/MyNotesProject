/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.ActOfInventarization;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractFilter;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.12
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ActOfInventarizationListPage extends AbstractListPage<ActOfInventarization, ActOfInventarizationItem> implements OrgSelectPage.CompleteHandler {

    private ActOfInventarizationFilter filter = new ActOfInventarizationFilter();
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
        return "commodity_accounting/acts/inventarization_acts";
    }

    @Override
    protected Class<ActOfInventarization> getEntityClass() {
        return ActOfInventarization.class;
    }

    @Override
    protected ActOfInventarizationItem createItem() {
        return new ActOfInventarizationItem();
    }

    @Override
    protected String getSortField() {
        return "createdDate";
    }

    @Override
    public ActOfInventarizationFilter getFilter() {
        return filter;
    }

    public String getShortName() {
        return shortName;
    }

}
