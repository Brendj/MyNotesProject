/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.context;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 26.06.13
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("singleton")
@Transactional
public class ContextDAOServices {

    @PersistenceContext(unitName = "processorPU")
    //@PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;


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

    public void buildOrgOrContragentRestriction(long idOfUser, String fieldOrg, String fieldContragent,
            Criteria criteria) {
        List<Long> orgIds = findOrgOwnersByContragentSet(idOfUser);
        Set<Contragent> contragents = getRestictedContragents(idOfUser);
        if ((orgIds == null || orgIds.isEmpty()) &&
            (contragents == null || contragents.isEmpty())) {
            return;
        }
        List<Long> contragentIds = new ArrayList<Long>();
        for (Contragent c : contragents) {
            contragentIds.add(c.getIdOfContragent());
        }
        Criterion inOrgsRestriction = null;
        if (orgIds != null && orgIds.size() > 0) {
            inOrgsRestriction = Restrictions.in(fieldOrg, orgIds);
        }
        Criterion inContragentsRestriction = null;
        if (contragentIds != null && contragentIds.size() > 0) {
            inContragentsRestriction = Restrictions.in(fieldContragent, contragentIds);
        }

        Restrictions mainRestriction = null;
        if (inOrgsRestriction != null && inContragentsRestriction != null) {
            criteria.add(Restrictions.or(inOrgsRestriction, inContragentsRestriction));
        }
        else {
            criteria.add(inOrgsRestriction == null ? inContragentsRestriction : inOrgsRestriction);
        }
    }

    public void buildOrgRestriction(long idOfUser, String field, Criteria criteria) {
        List<Long> orgIds = findOrgOwnersByContragentSet(idOfUser);     //  Ограничение по контрагенту
        if (!orgIds.isEmpty()) {
            criteria.add(Restrictions.in(field, orgIds));
        }
        buildRegionsRestriction(idOfUser, "district", criteria);        //  Ограничение по региону
    }

    public String buildOrgRestriction(long idOfUser, String field) {
        List<Long> orgIds = findOrgOwnersByContragentSet(idOfUser);
        if (orgIds.isEmpty()) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (Long id : orgIds) {
            if (str.length() > 0) {
                str.append(" or ");
            }
            str.append(field).append("=").append(id);
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
        Set<Contragent> contragents = getRestictedContragents(idOfUser);
        if (contragents.isEmpty()) {
            return;
        }
        List<Long> orgIds = new ArrayList<Long>();
        for (Contragent c : contragents) {
            orgIds.add(c.getIdOfContragent());
        }
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.in(field, orgIds));
        disjunction.add(Restrictions.eq("classId", Contragent.PAY_AGENT));
        criteria.add(disjunction);
    }





    /*
                           ОГРАНИЧЕНИЯ    ПО    РЕГИОНАМ
     */
    public void buildRegionsRestriction(long idOfUser, Criteria criteria) {
        buildRegionsRestriction(idOfUser, "region", criteria);
    }

    public void buildRegionsRestriction(long idOfUser, String field, Criteria criteria) {
        try {
            User user = DAOService.getInstance().findUserById(idOfUser);
            criteria.add(Restrictions.eq(field, user.getRegion()));
        } catch (Exception e) {

        }
    }









    /*
                           ПОИСКИ    ОБЪЕКТОВ
    */
    @Transactional
    public Set<Contragent> getRestictedContragents(long idOfUser) {
        Session persistenceSession = (Session) entityManager.getDelegate();
        Set<Contragent> contragents;
        try {
            User user = (User) persistenceSession.load(User.class, idOfUser);
            contragents = user.getContragents();
        } catch (Exception e) {
            return Collections.emptySet();
        }
        return contragents;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @SuppressWarnings("unchecked")
    public List<Long> findOrgOwnersByContragentSet(Long idOfUser) {
        Query query = entityManager.createQuery(
                "select distinct o.idOfOrg from Contragent c join c.orgsInternal o join c.usersInternal u where u.idOfUser = :idOfUser")
                .setParameter("idOfUser", idOfUser);
        return (List<Long>) query.getResultList();
    }

    public List<ConfigurationProvider> findConfigurationProviderByContragentSet(Long idOfUser) {
        Query contragentTypedQuery = entityManager.createQuery("select u.contragents from User u where u.idOfUser=:idOfUser");
        contragentTypedQuery.setParameter("idOfUser", idOfUser);
        List list = contragentTypedQuery.getResultList();
        Set<Contragent> contragentSet= new HashSet<Contragent>();
        for (Object o: list){
            Contragent contragent = (Contragent) o;
            if(!contragentSet.contains(contragent)){
                contragentSet.add(contragent);
            }
        }
        if(contragentSet.isEmpty()){
            return null;
        } else {
            TypedQuery<ConfigurationProvider> query = entityManager.createQuery("select configurationProvider from Org where defaultSupplier in :contragentSet",ConfigurationProvider.class);
            query.setParameter("contragentSet", contragentSet);
            return query.getResultList();
        }
    }

    public List<ConfigurationProvider> findConfigurationProviderByContragentSet(Long idOfUser, String filter) {
        Query contragentTypedQuery = entityManager.createQuery("select u.contragents from User u where u.idOfUser=:idOfUser");
        contragentTypedQuery.setParameter("idOfUser", idOfUser);
        List list = contragentTypedQuery.getResultList();
        Set<Contragent> contragentSet= new HashSet<Contragent>();
        for (Object o: list){
            Contragent contragent = (Contragent) o;
            if(!contragentSet.contains(contragent)){
                contragentSet.add(contragent);
            }
        }
        if(contragentSet.isEmpty()){
            return null;
        } else {
            TypedQuery<ConfigurationProvider> query = entityManager.createQuery("select configurationProvider from Org where UPPER(configurationProvider.name) like '%"+filter.toUpperCase()+"%' defaultSupplier in :contragentSet",ConfigurationProvider.class);
            query.setParameter("contragentSet", contragentSet);
            return query.getResultList();
        }
    }
}
