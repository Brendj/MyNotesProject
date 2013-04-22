/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

        this.items = items;
    }

}