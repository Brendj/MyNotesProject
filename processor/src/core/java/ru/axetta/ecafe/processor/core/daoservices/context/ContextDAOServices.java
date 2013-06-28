/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.context;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 26.06.13
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */

@Service
@Transactional
@Component
@Scope("singleton")
public class ContextDAOServices {

    @PersistenceContext
    private EntityManager em;


    public String getContragentsListForTooltip (long idOfUser) {
        try {
            Set<Contragent> contragents = getRestictedContragents(idOfUser);
            StringBuilder str = new StringBuilder("");
            for (Contragent c : contragents) {
                if (str.length() > 0) {
                    str.append("<br />");
                }
                str.append(c.getContragentName());
            }
            return str.toString();
        } catch (Exception e) {
            return "";
        }
    }


    public static ContextDAOServices getInstance() {
        return RuntimeContext.getAppContext().getBean(ContextDAOServices.class);
    }


    /*
                            ОГРАНИЧЕНИЯ    ДЛЯ   ОРГАНИЗАЦИЙ
     */
    public void buildOrgRestriction (long idOfUser, Criteria criteria) {
        buildOrgRestriction(idOfUser, "idOfOrg", criteria);
    }

    public void buildOrgRestriction (long idOfUser, String field, Criteria criteria) {
        List <Org> orgs = getRestictedOrgs(getRestictedContragents(idOfUser));
        if (orgs == null || orgs.size() < 1) {
            return;
        }
        List <Long> orgIds = new ArrayList<Long>();
        for (Org o : orgs) {
            orgIds.add(o.getIdOfOrg());
        }
        criteria.add(Restrictions.in(field, orgIds));
    }

    public String buildOrgRestriction (long idOfUser) {
        return buildOrgRestriction (idOfUser, "cf_orgs.idOfOrg");
    }

    public String buildOrgRestriction (long idOfUser, String field) {
        List <Org> orgs = getRestictedOrgs(getRestictedContragents(idOfUser));
        if (orgs == null || orgs.size() < 1) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (Org org : orgs) {
            if (str.length() > 0) {
                str.append(" or ");
            }
            str.append(field).append("=").append(org.getIdOfOrg());
        }
        return "(" + str.toString() + ")";
    }







    /*
                           ОГРАНИЧЕНИЯ    ДЛЯ   КОНТРАГЕНТОВ
    */
    public void buildContragentRestriction(long idOfUser, Criteria criteria) {
        buildContragentRestriction(idOfUser, "idOfContragent", criteria);
    }

    public void buildContragentRestriction(long idOfUser, String field, Criteria criteria) {
        Set <Contragent> contragents = getRestictedContragents(idOfUser);
        if (contragents == null || contragents.size() < 1) {
            return;
        }
        List <Long> orgIds = new ArrayList<Long>();
        for (Contragent c : contragents) {
            orgIds.add(c.getIdOfContragent());
        }
        criteria.add(Restrictions.in(field, orgIds));
    }










    /*
                           ПОИСКИ    ОБЪЕКТОВ
    */
    @Transactional
    private Set<Contragent> getRestictedContragents (long idOfUser) {
        Session persistenceSession = (Session) em.getDelegate();
        Set<Contragent> contragents = null;
        try {
            User user = (User) persistenceSession.load(User.class, idOfUser);
            contragents = user.getContragents();
        } catch (Exception e) {
            return Collections.EMPTY_SET;
        }
        if (contragents.size() < 1) {
            return Collections.EMPTY_SET;
        }
        return contragents;
    }

    @Transactional
    private List<Org> getRestictedOrgs (Set<Contragent> contragents) {
        if (contragents == null || contragents.size() < 1) {
            return Collections.EMPTY_LIST;
        }
        Session persistenceSession = (Session) em.getDelegate();
        String suppliers = "";
        for (Contragent c : contragents) {
            if (suppliers.length() > 0) {
                suppliers = suppliers + " or ";
            }
            suppliers = suppliers + "defaultSupplier=" + c.getIdOfContragent();
        }
        suppliers = suppliers.length() > 0 ? "where " + suppliers : "";

        List<Org> orgs = new ArrayList<Org>();
        org.hibernate.Query query = persistenceSession.createSQLQuery("select idoforg from cf_orgs " + suppliers);
        List list = query.list();
        for (Object row : list) {
            BigInteger idOfOrg = (BigInteger) row;
            orgs.add((Org) persistenceSession.load(Org.class, idOfOrg.longValue()));
        }
        return orgs;
    }
}
