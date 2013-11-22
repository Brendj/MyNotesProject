package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.09.13
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ConfigurationProviderService {

    @Transactional(rollbackFor = Exception.class)
    public ConfigurationProvider onSave(ConfigurationProvider configurationProvider, User currentUser, List<Long> idOfOrgList) throws Exception{
        ConfigurationProvider cp;
        if(configurationProvider.getIdOfConfigurationProvider()!=null){
            cp = entityManager.find(ConfigurationProvider.class, configurationProvider.getIdOfConfigurationProvider());
        } else {
            cp = new ConfigurationProvider();
            cp.setCreatedDate(new Date());
            cp.setUserCreate(currentUser);
        }
        cp.setLastUpdate(new Date());
        cp.setUserEdit(currentUser);
        cp.setName(configurationProvider.getName());


        if(idOfOrgList!=null && !idOfOrgList.isEmpty()){
            TypedQuery<Org> query = entityManager.createQuery("from Org org where org.id in :idOfOrg", Org.class);
            query.setParameter("idOfOrg",idOfOrgList);
            List<Org> orgs = query.getResultList();
            if(!orgs.equals(cp.getOrgs())){
                for (Org org: cp.getOrgs()){
                    org.setCommodityAccounting(false);
                    entityManager.persist(org);
                }
                cp.clearOrg();
                cp.addOrg(orgs);
                for (Org org: orgs){
                    if(!org.getSourceMenuOrgs().isEmpty()){
                        Org sourceOrg = org.getSourceMenuOrgs().iterator().next();
                        final boolean isProvider = !sourceOrg.getConfigurationProvider().getIdOfConfigurationProvider()
                                .equals(cp.getIdOfConfigurationProvider());
                        if(sourceOrg.getConfigurationProvider()==null || isProvider){
                            final StringBuilder message = new StringBuilder("Организации - источника ")
                                    .append(" меню школы ")
                                    .append("'").append(org.getShortName()).append("'")
                                    .append(" не входит в текущую конфигурацию провайдера");
                            throw new Exception(message.toString());
                        }
                    }
                    org.setFullSyncParam(true);
                    org.setCommodityAccounting(true);
                    entityManager.persist(org);
                    entityManager.flush();
                }
            }
        } else {
            if(!cp.getOrgs().isEmpty()){
                for (Org org: cp.getOrgs()){
                    org.setConfigurationProvider(null);
                    org.setCommodityAccounting(false);
                    entityManager.persist(org);
                }
            }
            cp.clearOrg();
        }
        entityManager.persist(cp);
        return configurationProvider;
    }

    public ConfigurationProvider reload(ConfigurationProvider currentConfigurationProvider) {
        currentConfigurationProvider = entityManager.merge(currentConfigurationProvider);
        currentConfigurationProvider.getOrgs(); // Lazy load
        return currentConfigurationProvider;
    }

    @Transactional(readOnly = true)
    public List<Org> findOrgsByConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        Session session = entityManager.unwrap(Session.class);
        return findOrgByConfigurationProvider(currentConfigurationProvider, session);
    }

    @Transactional(readOnly = true)
    public List<ConfigurationProvider> findConfigurationProvider() {
        return findConfigurationProvider(entityManager.unwrap(Session.class), null);
    }

    @Transactional(readOnly = true)
    public List<ConfigurationProvider> findConfigurationProviderByName(String name) {
        return findConfigurationProvider(entityManager.unwrap(Session.class), name);
    }

    @Transactional(readOnly = true)
    public List<ConfigurationProvider> findConfigurationProviderByContragentSet(Long idOfUser, String name) {
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
            return findConfigurationProvider(entityManager.unwrap(Session.class), name, contragentSet);
        }
    }

    /* Public Static methods */
    public static ConfigurationProvider loadConfigurationProvider(Session session, Long idOfConfigurationProvider){
        return (ConfigurationProvider) session.load(ConfigurationProvider.class, idOfConfigurationProvider);
    }

    @SuppressWarnings("unchecked")
    public static List<Org> findOrgByConfigurationProvider(ConfigurationProvider currentConfigurationProvider,
            Session session) {
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("configurationProvider", currentConfigurationProvider));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public static List<ConfigurationProvider> findConfigurationProvider(Session session, String name){
        Criteria criteria = session.createCriteria(ConfigurationProvider.class);
        if(StringUtils.isNotEmpty(name)) criteria.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
        //criteria.addOrder(Order.asc("idOfConfigurationProvider"));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public static List<ConfigurationProvider> findConfigurationProvider(Session session, String name, Set<Contragent> contragentSet){
        Criteria criteria = session.createCriteria(Org.class);
        if(StringUtils.isNotEmpty(name)) {
            criteria.createAlias("configurationProvider","configurationProvider", JoinType.INNER_JOIN);
            criteria.add(Restrictions.ilike("configurationProvider.name", name, MatchMode.ANYWHERE));
        }
        if(!contragentSet.isEmpty()){
            criteria.add(Restrictions.in("defaultSupplier", contragentSet));
        }
        criteria.add(Restrictions.isNotNull("configurationProvider"));
        criteria.setProjection(Projections.distinct(Projections.property("configurationProvider")));
        return criteria.list();
    }

    public static Long extractIdOfConfigurationProviderByIdOfOrg(Session session, Long idOfOrg) throws Exception {
        org.hibernate.Query query = session.createQuery("select org.configurationProvider.idOfConfigurationProvider from Org org where org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        List list = query.list();
        if(list==null || list.isEmpty() || list.size()>1){
            throw new Exception("CONFIGURATION_PROVIDER_NOT_FOUND");
        }
        return (Long) list.get(0);
    }

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
}
