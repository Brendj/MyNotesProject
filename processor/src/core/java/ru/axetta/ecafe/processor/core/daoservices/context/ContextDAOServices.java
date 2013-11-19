/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.context;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.User;

import org.hibernate.Criteria;
import org.hibernate.Session;
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

    public void buildOrgRestriction(long idOfUser, String field, Criteria criteria) {
        List<Long> orgIds = findOrgOwnersByContragentSet(idOfUser);
        if (orgIds.isEmpty()) {
            return;
        }
        criteria.add(Restrictions.in(field, orgIds));
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
    public Set<Contragent> getRestictedContragents (long idOfUser) {
        Session persistenceSession = (Session) entityManager.getDelegate();
        Set<Contragent> contragents;
        try {
            User user = (User) persistenceSession.load(User.class, idOfUser);
            contragents = user.getContragents();
        } catch (Exception e) {
            return Collections.emptySet();
        }
        if (contragents.size() < 1) {
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
