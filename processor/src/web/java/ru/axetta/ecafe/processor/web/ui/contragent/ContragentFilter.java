/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 30.06.14
 * Time: 9:49
 * To change this template use File | Settings | File Templates.
 */
public class ContragentFilter {

    /* Полное имя контрагента */
    private String officialName;

    public boolean isEmpty() {
        return officialName == null;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public List retrieveContragents(Session session) throws Exception {
        Criteria criteria = session.createCriteria(Contragent.class);
        addRestrictions(criteria);
        return criteria.list();
    }

    public void addRestrictions(Criteria criteria) throws Exception {
        try {
            Long idOfUser = MainPage.getSessionInstance().getCurrentUser().getIdOfUser();
            ContextDAOServices.getInstance().buildOrgRestriction(idOfUser, "org.idOfOrg", criteria);
        } catch (Exception e) {
        }
        if (StringUtils.isNotEmpty(officialName)) {
            criteria.add(Restrictions.like("contragentName", officialName, MatchMode.ANYWHERE).ignoreCase());
        }
        criteria.addOrder(Order.asc("idOfContragent"));
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public void clear() {
        this.officialName = null;
    }

}
