/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.06.16
 * Time: 11:09
 */

public class MigrantsUtils {

    private MigrantsUtils() {
    }

    public static Migrant findMigrant(Session persistenceSession, CompositeIdOfMigrant compositeIdOfMigrant) throws Exception {
        return (Migrant) persistenceSession.get(Migrant.class, compositeIdOfMigrant);
    }

    public static VisitReqResolutionHist findVisitReqResolutionHist(Session persistenceSession, CompositeIdOfVisitReqResolutionHist compositeId) throws Exception {
        return (VisitReqResolutionHist) persistenceSession.get(VisitReqResolutionHist.class, compositeId);
    }

    public static List<Migrant> getActiveMigrantsByIdOfClient(Session session, Long idOfClient) throws Exception {
        List<Migrant> result = new ArrayList<Migrant>();
        Date date = new Date();
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.add(Restrictions.eq("clientMigrate.idOfClient", idOfClient));
        criteria.add(Restrictions.le("visitStartDate", date));
        criteria.add(Restrictions.ge("visitEndDate", date));
        List<Migrant> migrants = criteria.list();
        for(Migrant migrant : migrants){
            Query query = session.createQuery("from VisitReqResolutionHist where migrant=:migrant order by resolutionDateTime desc");
            query.setParameter("migrant", migrant);
            query.setMaxResults(1);
            VisitReqResolutionHist res = (VisitReqResolutionHist) query.uniqueResult();
            if(res.getResolution().equals(1)){
                result.add(migrant);
            }
        }
        return result;
    }


    public static List<Migrant> getMigrantsForOrg(Session session, Long idOfOrg) throws Exception {
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.createCriteria("orgVisit", "org", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("syncState", Migrant.NOT_SYNCHRONIZED));
        return criteria.list();
    }

    public static List<VisitReqResolutionHist> getResolutionsForMigrant(Session session, Migrant migrant) {
        Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
        criteria.add(Restrictions.eq("migrant", migrant));
        criteria.addOrder(Order.asc("resolutionDateTime"));
        return criteria.list();
    }

    public static List<VisitReqResolutionHist> getIncomeResolutionsForOrg(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
        criteria.createAlias("migrant","migrant", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("migrant.orgVisit", org));
        criteria.add(Restrictions.ne("orgResol.idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("syncState", VisitReqResolutionHist.NOT_SYNCHRONIZED));
        return criteria.list();
    }

    public static List<VisitReqResolutionHist> getOutcomeResolutionsForOrg(Session session, Long idOfOrg) throws Exception {
        Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
        criteria.add(Restrictions.eq("orgRegistry.idOfOrg", idOfOrg));
        criteria.add(Restrictions.ne("orgResol.idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("syncState", VisitReqResolutionHist.NOT_SYNCHRONIZED));
        return criteria.list();
    }

    public static List<VisitReqResolutionHist> getNotSyncResolutionsForMigrant(Session session, Migrant migrant) throws Exception {
        Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
        criteria.add(Restrictions.eq("migrant", migrant));
        criteria.add(Restrictions.eq("syncState", VisitReqResolutionHist.NOT_SYNCHRONIZED));
        return criteria.list();
    }

    public static List<VisitReqResolutionHist> getSyncedResolutionsForMigrants(Session session, List<Migrant> migrants) throws Exception {
        if(migrants.size() < 1){
            return new ArrayList<VisitReqResolutionHist>();
        }
        Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
        criteria.add(Restrictions.in("migrant", migrants));
        criteria.add(Restrictions.eq("syncState", VisitReqResolutionHist.SYNCHRONIZED));
        return criteria.list();
    }

    public static List<VisitReqResolutionHist> getResolutionsForMigrants(Session session, List<Migrant> migrants) throws Exception {
        if(migrants.size() < 1){
            return new ArrayList<VisitReqResolutionHist>();
        }
        Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
        criteria.add(Restrictions.in("migrant", migrants));
        return criteria.list();
    }

    public static List<Client> getActiveMigrantsForOrg(Session session, Long idOfOrg) throws Exception {
        Set<Client> clients = new HashSet<Client>();
        List<Migrant> migrants = getCurrentMigrantsForOrg(session, idOfOrg);
        for(Migrant migrant : migrants){
            Query query = session.createQuery("from VisitReqResolutionHist where migrant=:migrant order by resolutionDateTime desc");
            query.setParameter("migrant", migrant);
            query.setMaxResults(1);
            VisitReqResolutionHist res = (VisitReqResolutionHist) query.uniqueResult();
            if(res != null && res.getResolution().equals(1)){
                clients.add(migrant.getClientMigrate());
            }
        }
        return new ArrayList<Client>(clients);
    }

    public static List<Long> getActiveMigrantsIdsForOrg(Session session, Long idOfOrg) throws Exception {
        Set<Long> clientsIds = new HashSet<Long>();
        List<Migrant> migrants = getCurrentMigrantsForOrg(session, idOfOrg);
        for(Migrant migrant : migrants){
            Query query = session.createQuery("from VisitReqResolutionHist where migrant=:migrant order by resolutionDateTime desc");
            query.setParameter("migrant", migrant);
            query.setMaxResults(1);
            VisitReqResolutionHist res = (VisitReqResolutionHist) query.uniqueResult();
            if(res.getResolution().equals(1)){
                clientsIds.add(migrant.getClientMigrate().getIdOfClient());
            }
        }
        return new ArrayList<Long>(clientsIds);
    }

    public static Integer getLastResolutionForMigrant(Session session, Migrant migrant){
        Query query = session.createQuery("select resolution from VisitReqResolutionHist where migrant=:migrant and resolution <> 5 order by resolutionDateTime desc");
        query.setParameter("migrant", migrant);
        query.setMaxResults(1);
        Object result = query.uniqueResult();
        return result == null ? 0 : (Integer) result;
    }

    public static List<Migrant> getCurrentMigrantsForOrg(Session session, Long idOfOrg) throws Exception {
        Date date = new Date();
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.add(Restrictions.eq("orgVisit.idOfOrg", idOfOrg));
        criteria.add(Restrictions.le("visitStartDate", date));
        criteria.add(Restrictions.ge("visitEndDate", date));
        return criteria.list();
    }

    public static List<Migrant> getSyncedMigrantsForOrg(Session session, Long idOfOrg) throws Exception {
        Date date = new Date();
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.add(Restrictions.eq("orgVisit.idOfOrg", idOfOrg));
        criteria.add(Restrictions.ge("visitEndDate", date));
        criteria.add(Restrictions.eq("syncState", Migrant.SYNCHRONIZED));
        return criteria.list();
    }

    public static List<Migrant> getMigrantsIdsForOrgReg(Session session, Long idOfOrg) throws Exception {
        Date date = new Date();
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.add(Restrictions.eq("orgRegistry.idOfOrg", idOfOrg));
        criteria.add(Restrictions.le("visitStartDate", date));
        criteria.add(Restrictions.ge("visitEndDate", date));
        criteria.add(Restrictions.ne("syncState", Migrant.CLOSED));
        return criteria.list();
    }

    public static List<Migrant> getOverdueMigrants(Session session) throws Exception {
        Date date = new Date();
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.add(Restrictions.lt("visitEndDate", date));
        criteria.add(Restrictions.ne("syncState", Migrant.CLOSED));
        return criteria.list();
    }

    public static long nextIdOfProcessorMigrantResolutions(Session session, Long idOfOrg){
        long id = -1L;
        Query query = session.createSQLQuery("select v.idofrecord from cf_visitreqresolutionhist as v where v.idofrecord < 0 and " +
                "v.idoforgresol=:idoforgresol order by v.idofrecord asc limit 1 for update");
        query.setParameter("idoforgresol", idOfOrg);
        Object o = query.uniqueResult();
        if(o!=null){
            id = Long.valueOf(o.toString()) - 1;
        }
        return id;
    }

    public static long nextIdOfProcessorMigrantRequest(Session session, Long idOfOrg){
        long id = -1L;
        Query query = session.createSQLQuery("select m.idofrequest from cf_migrants as m where m.idofrequest < 0 and " +
                "m.idoforgregistry=:idoforgregistry order by m.idofrequest asc limit 1 for update");
        query.setParameter("idoforgregistry", idOfOrg);
        Object o = query.uniqueResult();
        if(o!=null){
            id = Long.valueOf(o.toString()) - 1;
        }
        return id;
    }

    public static List<Migrant> getOutcomeMigrantsForOrgsByDate(Session session, List<Long> idOfOrgs,
            Date startDate, Date endDate){
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.createCriteria("orgRegistry", "org", JoinType.INNER_JOIN);
        criteria.add(Restrictions.in("org.idOfOrg", idOfOrgs));
        criteria.add(Restrictions.or(Restrictions.between("visitStartDate", startDate, endDate),
                Restrictions.between("visitEndDate", startDate, endDate)));
        return criteria.list();
    }

    public static List<Migrant> getIncomeMigrantsForOrgsByDate(Session session, List<Long> idOfOrgs,
            Date startDate, Date endDate){
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.createCriteria("orgVisit", "org", JoinType.INNER_JOIN);
        criteria.add(Restrictions.in("org.idOfOrg", idOfOrgs));
        criteria.add(Restrictions.or(Restrictions.between("visitStartDate", startDate, endDate),
                Restrictions.between("visitEndDate", startDate, endDate)));
        return criteria.list();
    }

    public enum MigrantsEnumType {
        /*0*/ ALL("all", "По всем заявкам"),
        /*1*/ OUTCOME("outcome", "По исходящим заявкам"),
        /*2*/ INCOME("income", "По входящим заявкам");

        private final String name;
        private final String description;

        private MigrantsEnumType(String name, String description){
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public static String getNameByDescription(String description){
            for(MigrantsEnumType m : values()){
                if(m.getDescription().equals(description)){
                    return m.getName();
                }
            }
            throw new IllegalArgumentException("Migrant type with description \"" + description + "\" does not exists.");
        }

        public static String buildRuleString(){
            return null;
        }
    }
}
