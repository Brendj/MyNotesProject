/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.ccaccount;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.ContragentClientAccount;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.07.2009
 * Time: 12:30:24
 * To change this template use File | Settings | File Templates.
 */
public class CCAccountFilter {

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public boolean isEmpty() {
            return null == idOfContragent;
        }

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
        }

        public boolean isEmpty() {
            return null == idOfOrg;
        }

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }
    }

    private Long idOfAccount = null;
    private ContragentItem contragent = new ContragentItem();
    private OrgItem org = new OrgItem();

    public Long getIdOfAccount() {
        return idOfAccount;
    }

    public void setIdOfAccount(Long idOfAccount) {
        if (0L == idOfAccount) {
            this.idOfAccount = null;
        } else {
            this.idOfAccount = idOfAccount;
        }
    }

    public ContragentItem getContragent() {
        return contragent;
    }

    public OrgItem getOrg() {
        return org;
    }

    public boolean isEmpty() {
        return null == idOfAccount && contragent.isEmpty() && org.isEmpty();
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public void completeContragentSelection(Session session, Long idOfContragent) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.contragent = new ContragentItem(contragent);
        }
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
        }
    }

    public void clear() {
        idOfAccount = null;
        contragent = new ContragentItem();
        org = new OrgItem();
    }

    public List retrieveCCAccounts(Session session) throws Exception {
        Criteria criteria = session.createCriteria(ContragentClientAccount.class);
        criteria.setFetchMode("client", FetchMode.JOIN);
        if (!this.isEmpty()) {
            if (null != this.idOfAccount) {
                criteria.add(Restrictions.eq("idOfAccount", this.idOfAccount));
            }
            if (!this.contragent.isEmpty()) {
                Contragent contragent = (Contragent) session
                        .load(Contragent.class, this.contragent.getIdOfContragent());
                criteria.add(Restrictions.eq("contragent", contragent));
            }
            if (!this.org.isEmpty()) {
                Org org = (Org) session.load(Org.class, this.org.getIdOfOrg());
                criteria.createCriteria("client").add(Restrictions.eq("org", org));
            }
        }
        return criteria.list();
    }
}