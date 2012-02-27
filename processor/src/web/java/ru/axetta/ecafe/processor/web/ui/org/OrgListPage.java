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

    public static class Item {

        private final Long idOfOrg;
        private final String shortName;
        private final String contractId;
        private final Integer state;
        private final String phone;

        public Item(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.state = org.getState();
            this.contractId = org.getContractId();
            this.phone = org.getPhone();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getContractId() {
            return contractId;
        }

        public Integer getState() {
            return state;
        }

        public String getPhone() {
            return phone;
        }
    }

    private final OrgFilter orgFilter = new OrgFilter();

    private List<Item> items = Collections.emptyList();

    public String getPageFilename() {
        return "org/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public OrgFilter getOrgFilter() {
        return orgFilter;
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();

        /* Добавленна проверка на фильтр
         * производится выборка только при введении одного из параметров фильтра */
        if(!orgFilter.isEmpty()){
            List orgs = orgFilter.retrieveOrgs(session);
            for (Object object : orgs) {
                Org org = (Org) object;
                items.add(new Item(org));
            }
        }

        this.items = items;
    }

}