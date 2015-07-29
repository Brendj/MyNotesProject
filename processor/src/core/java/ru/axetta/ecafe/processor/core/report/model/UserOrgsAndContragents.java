/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 28.07.15
 * Time: 18:40
 * To change this template use File | Settings | File Templates.
 */
public class UserOrgsAndContragents {
    private final User user;
    private final List<Long> orgs;
    private final List<Long> contragents;

    public UserOrgsAndContragents(Session session, long idOfUser) throws Exception {
        user = DAOUtils.findUser(session, idOfUser);
        Set<Contragent> userContragents = user.getContragents();
        contragents = new ArrayList<Long>();
        if (userContragents.size() > 0) {
            Criteria orgCriteria = session.createCriteria(Org.class);
            for (Contragent contragent : userContragents) {
                contragents.add(contragent.getIdOfContragent());
            }
            orgCriteria.add(Restrictions.in("defaultSupplier", userContragents));
            orgCriteria.setProjection(Property.forName("idOfOrg"));
            orgs = orgCriteria.list();
        }
        else {
            orgs = null;
        }
    }

    public List<Long> getOrgs() {
        return orgs;
    }

    public List<Long> getContragents() {
        return contragents;
    }

    public User getUser() {
        return user;
    }
}
