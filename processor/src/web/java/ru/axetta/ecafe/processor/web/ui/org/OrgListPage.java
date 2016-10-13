/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgListPage extends BasicWorkspacePage {

    private final OrgFilter orgFilter = new OrgFilter();

    private List<OrgItem> items = Collections.emptyList();

    public String getPageFilename() {
        return "org/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<OrgItem> getItems() {
        return items;
    }

    public OrgFilter getOrgFilter() {
        return orgFilter;
    }

    public void fill(Session session) throws Exception {
        List<OrgItem> items = new LinkedList<OrgItem>();

        /* Добавленна проверка на фильтр
         * производится выборка только при введении одного из параметров фильтра */
        if(!orgFilter.isEmpty()){
            items = orgFilter.retrieveOrgs(session);
        }

        Map<Long, OrgItem> orgs = new HashMap<Long, OrgItem>();
        for(OrgItem orgItem : items) {
            orgs.put(orgItem.getIdOfOrg(), orgItem);
        }
        if(orgs.keySet().size() > 0) {
            Query query = session.createSQLQuery("SELECT f.currentorg, o.idoforg, o.shortname FROM cf_orgs o " + "INNER JOIN cf_friendly_organization f ON o.idoforg = f.friendlyorg "
                    + "WHERE o.idoforg IN " + "(SELECT f.friendlyorg FROM cf_friendly_organization f " + "INNER JOIN cf_orgs o ON o.idoforg = f.friendlyorg "
                    + "WHERE f.currentorg IN :orgs AND o.mainbuilding = 1)");
            query.setParameterList("orgs", orgs.keySet());
            List list = query.list();
            for (Object entry : list) {
                Object e[] = (Object[]) entry;
                long idoforg = ((BigInteger) e[0]).longValue();
                long idoforgMain = ((BigInteger) e[1]).longValue();
                String shortNameMain = (String) e[2];
                OrgItem orgItem = orgs.get(idoforg);
                if(orgItem != null) {
                    orgItem.setIdOfOrgMain(idoforgMain);
                    orgItem.setShortNameMain(shortNameMain);
                }
            }
        }

        this.items = items;
    }

}