/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.06.16
 * Time: 11:09
 */

public class MigrantsUtils {
    private static final Logger logger = LoggerFactory.getLogger(MigrantsUtils.class);
    public static final String[] resolutionNames = {"Создана",                                         //0
                                                     "Подтверждена",                                   //1
                                                     "Отклонена и сдана в архив",                      //2
                                                     "Аннулирована и сдана в архив",                   //3
                                                     "Сдана в архив по истечению срока действия",      //4
                                                     "Сдана в архив по истечению срока действия"};     //5

    public static final Integer MAX_MIGRANTS_FOR_QUERY = 1000;

    private MigrantsUtils() {
    }

    public static Migrant findMigrant(Session persistenceSession, CompositeIdOfMigrant compositeIdOfMigrant) throws Exception {
        return (Migrant) persistenceSession.get(Migrant.class, compositeIdOfMigrant);
    }

    public static VisitReqResolutionHist findVisitReqResolutionHist(Session persistenceSession, CompositeIdOfVisitReqResolutionHist compositeId) throws Exception {
        return (VisitReqResolutionHist) persistenceSession.get(VisitReqResolutionHist.class, compositeId);
    }

    public static List<Migrant> getAllMigrantsByIdOfClient(Session session, Long idOfClient) throws Exception {
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.add(Restrictions.eq("clientMigrate.idOfClient", idOfClient));
        return criteria.list();
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
            return new ArrayList<>();
        }
        List<VisitReqResolutionHist> result = new ArrayList<>();
        List<Migrant> mig_list = new ArrayList<>();
        for (Migrant migrant : migrants) {
            mig_list.add(migrant);
            if (mig_list.size() > MAX_MIGRANTS_FOR_QUERY) {
                Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
                criteria.add(Restrictions.in("migrant", mig_list));
                result.addAll(criteria.list());
                mig_list.clear();
            }
        }
        if (mig_list.size() > 0) {
            Criteria criteria = session.createCriteria(VisitReqResolutionHist.class);
            criteria.add(Restrictions.in("migrant", mig_list));
            result.addAll(criteria.list());
        }
        return result;
    }

    public static List<Client> getActiveMigrantsForOrg(Session session, Long idOfOrg) throws Exception {
        //Set<Client> clients = new HashSet<Client>();
        //List<Migrant> migrants = getCurrentMigrantsForOrg(session, idOfOrg);
        /*Query query = session.createQuery("select distinct v.migrant.clientMigrate from VisitReqResolutionHist v join fetch v.migrant m join fetch m.clientMigrate "
                + "where m.orgVisit.idOfOrg = :idOfOrg and "
                + "m.visitStartDate <= :date and m.visitEndDate >= :date and v.resolution = 1 order by v.resolutionDateTime desc");*/
        Query query = session.createQuery("select distinct m.clientMigrate from Migrant m join m.visitReqResolutionHists v "
                + "where m.orgVisit.idOfOrg = :idOfOrg and "
                + "m.visitStartDate <= :date and m.visitEndDate >= :date and v.resolution = 1 and "
                + "v.resolutionDateTime = (select max(v2.resolutionDateTime) from VisitReqResolutionHist v2 where v2.migrant = m)");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("date", new Date());
        List<Client> list = query.list();
        return list == null ? new ArrayList<Client>() : list;
        /*for(Migrant migrant : migrants){
            Query query = session.createQuery("from VisitReqResolutionHist where migrant=:migrant order by resolutionDateTime desc");
            query.setParameter("migrant", migrant);
            query.setMaxResults(1);
            VisitReqResolutionHist res = (VisitReqResolutionHist) query.uniqueResult();
            if(res != null && res.getResolution().equals(1)){
                clients.add(migrant.getClientMigrate());
            }
        }
        return new ArrayList<Client>(clients);*/
    }

    public static List<Long> getActiveMigrantsIdsForOrg(Session session, Long idOfOrg) throws Exception {
        Query query = session.createQuery("select distinct m.clientMigrate.idOfClient from Migrant m join m.visitReqResolutionHists v "
                + "where m.orgVisit.idOfOrg = :idOfOrg and "
                + "m.visitStartDate <= :date and m.visitEndDate >= :date and v.resolution = 1");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("date", new Date());
        List<Long> list = query.list();
        return list == null ? new ArrayList<Long>() : list;
        /*Set<Long> clientsIds = new HashSet<Long>();
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
        return new ArrayList<Long>(clientsIds);*/
    }

    public static VisitReqResolutionHist getLastResolutionForMigrant(Session session, Migrant migrant){
        Query query = session.createQuery("select res from VisitReqResolutionHist res where res.migrant=:migrant order by res.resolutionDateTime desc");
        query.setParameter("migrant", migrant);
        query.setMaxResults(1);
        return (VisitReqResolutionHist) query.uniqueResult();
    }

    public static VisitReqResolutionHist getFirstResolutionForMigrant(Session session, Migrant migrant){
        Query query = session.createQuery("select res from VisitReqResolutionHist res where res.migrant=:migrant order by res.resolutionDateTime asc");
        query.setParameter("migrant", migrant);
        query.setMaxResults(1);
        return (VisitReqResolutionHist) query.uniqueResult();
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
                "v.idoforgresol=:idoforgresol order by v.idofrecord asc limit 1");
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
                "m.idoforgregistry=:idoforgregistry order by m.idofrequest asc limit 1");
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

    public static List<Migrant> getOutcomeMigrantsForOrgsWithoutDate(Session session, List<Long> idOfOrgs){
        Criteria criteria = session.createCriteria(Migrant.class)
                .createCriteria("orgRegistry", "org")
                .add(Restrictions.in("org.idOfOrg", idOfOrgs));

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

    public static List<Migrant> getIncomeMigrantsForOrgsWithoutDate(Session session, List<Long> idOfOrgs){
        Criteria criteria = session.createCriteria(Migrant.class)
                .createCriteria("orgVisit", "org")
                .add(Restrictions.in("org.idOfOrg", idOfOrgs));

        return criteria.list();
    }

    public static List<Migrant> getAllMigrantsForOrgsByDate(Session session, List<Long> idOfOrgs,
            String guid, Date startDate, Date endDate, Boolean showAll, List<Long> clientIDList){
        String condition = "";

        if (idOfOrgs.size() > 0) {
            String orgs = StringUtils.join(idOfOrgs, ",");
            condition += String.format(" and (m.orgVisit.idOfOrg in (%s) or m.orgRegistry.idOfOrg in (%s))", orgs, orgs);
        }
        if (!StringUtils.isEmpty(guid)) {
            condition += String.format(" and m.clientMigrate.clientGUID = '%s'", guid);
        }
        if (!clientIDList.isEmpty()) {
            String clients = StringUtils.join(clientIDList, ",");
            condition += String.format(" and (m.clientMigrate.idOfClient in (%s))", clients);
        }
        String str;
        if (showAll) {
            str = "select m from VisitReqResolutionHist h "
                    + "join h.migrant m "
                    + "where h.resolutionDateTime < :endDate and h.resolutionDateTime > :startDate and h.resolution = 0 "
                    + condition;
        } else {
            str = "select m from VisitReqResolutionHist h "
                    + "join h.migrant m "
                    + " where not exists (select h from VisitReqResolutionHist h where h.migrant.compositeIdOfMigrant.idOfRequest = m.compositeIdOfMigrant.idOfRequest "
                    + " and h.migrant.compositeIdOfMigrant.idOfOrgRegistry = m.compositeIdOfMigrant.idOfOrgRegistry and h.resolution > :resolution) "
                    + " and h.resolutionDateTime < :endDate and h.resolutionDateTime > :startDate and h.resolution = 0 "
                    + condition;
        }
        Query query = session.createQuery(str);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        if (!showAll) query.setParameter("resolution", VisitReqResolutionHist.RES_CONFIRMED);
        return query.list();
    }

    public static void disableMigrant(CompositeIdOfMigrant compositeIdOfMigrant) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Migrant migrant = (Migrant)session.load(Migrant.class, compositeIdOfMigrant);
            Long nextId = MigrantsUtils.nextIdOfProcessorMigrantResolutions(session, compositeIdOfMigrant.getIdOfOrgRegistry());
            migrant.setSyncState(Migrant.CLOSED);
            CompositeIdOfVisitReqResolutionHist compositeId1 = new CompositeIdOfVisitReqResolutionHist(nextId,
                    migrant.getCompositeIdOfMigrant().getIdOfRequest(), migrant.getOrgRegistry().getIdOfOrg());
            VisitReqResolutionHist hist1 = new VisitReqResolutionHist(compositeId1, migrant.getOrgRegistry(),
                    VisitReqResolutionHist.RES_CANCELED, new Date(),
                    resolutionNames[VisitReqResolutionHist.RES_CANCELED], null, null,
                    VisitReqResolutionHist.NOT_SYNCHRONIZED, VisitReqResolutionHistInitiatorEnum.INITIATOR_ISPP);
            nextId--;
            CompositeIdOfVisitReqResolutionHist compositeId2 = new CompositeIdOfVisitReqResolutionHist(nextId,
                    migrant.getCompositeIdOfMigrant().getIdOfRequest(), migrant.getOrgVisit().getIdOfOrg());
            VisitReqResolutionHist hist2 = new VisitReqResolutionHist(compositeId2, migrant.getOrgRegistry(),
                    VisitReqResolutionHist.RES_CANCELED, new Date(),
                    resolutionNames[VisitReqResolutionHist.RES_CANCELED], null, null,
                    VisitReqResolutionHist.NOT_SYNCHRONIZED, VisitReqResolutionHistInitiatorEnum.INITIATOR_ISPP);
            session.update(migrant);
            session.save(hist1);
            session.save(hist2);
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Cannot disable migrant: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public static String getResolutionString(Integer resolution) {
        return resolutionNames[resolution];
    }

    public static List<ESZMigrantsRequest> getAllESZMigrantsRequests(Session session) {
        Criteria criteria = session.createCriteria(ESZMigrantsRequest.class);
        return criteria.list();
    }

    public static Migrant getMigrantRequestByGuidAndGroupId(Session session, String guid, Long groupId) {
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.createAlias("clientMigrate", "client", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("client.clientGUID", guid));
        criteria.add(Restrictions.eq("resolutionCodeGroup", groupId));
        return (Migrant)criteria.uniqueResult();
    }

    public static List<Migrant> getMigrantRequestsByExternalIdAndGroupId(Session session, Long externalId, Long groupId) {
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.createAlias("clientMigrate", "client", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("client.externalId", externalId));
        criteria.add(Restrictions.eq("resolutionCodeGroup", groupId));
        return criteria.list();
    }

    public static List<ESZMigrantsRequest> getRequestsByExternalIdAndGroupId(Session session, Long externalId, Long groupId) {
        Criteria criteria = session.createCriteria(ESZMigrantsRequest.class);
        criteria.add(Restrictions.eq("idOfESZ", externalId));
        criteria.add(Restrictions.eq("idOfServiceClass", groupId));
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

    public static Set<Migrant> getAllVisitReqResolutionHist(Session session) {

        List<Long> predefined = new ArrayList<Long>();
        predefined.add(ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        predefined.add(ClientGroup.Predefined.CLIENT_DELETED.getValue());

        List<Integer> visitorResStatusNotIn = new ArrayList<Integer>();
        visitorResStatusNotIn.add(VisitReqResolutionHist.RES_REJECTED);
        visitorResStatusNotIn.add(VisitReqResolutionHist.RES_CANCELED);
        visitorResStatusNotIn.add(VisitReqResolutionHist.RES_OVERDUE);
        visitorResStatusNotIn.add(VisitReqResolutionHist.RES_OVERDUE_SERVER);

        Set<Migrant> migrantList = new HashSet<Migrant>();

        List<VisitReqResolutionHist> visitReqResolutionHists;

        Criteria visitorCriteria = session.createCriteria(VisitReqResolutionHist.class, "visitor");
        visitorCriteria.createAlias("visitor.migrant", "migrant", JoinType.LEFT_OUTER_JOIN);
        visitorCriteria.createAlias("migrant.clientMigrate", "client", JoinType.LEFT_OUTER_JOIN);
        visitorCriteria.add(Restrictions.not(Restrictions.in("visitor.resolution", visitorResStatusNotIn)));
        visitorCriteria.add(Restrictions.in("client.idOfClientGroup", predefined));

        visitReqResolutionHists = visitorCriteria.list();

        for (VisitReqResolutionHist value : visitReqResolutionHists) {
            migrantList.add(value.getMigrant());
        }

        return migrantList;
    }

    public static Set<Migrant> getAllVisitReqResolutionHistResFive(Session session) {
        List<Long> predefined = new ArrayList<Long>();
        predefined.add(ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        predefined.add(ClientGroup.Predefined.CLIENT_DELETED.getValue());

        Set<Migrant> migrantList = new HashSet<Migrant>();

        List<VisitReqResolutionHist> visitReqResolutionHists;

        Criteria visitorCriteria = session.createCriteria(VisitReqResolutionHist.class, "visitor");
        visitorCriteria.createAlias("visitor.migrant", "migrant", JoinType.LEFT_OUTER_JOIN);
        visitorCriteria.createAlias("migrant.clientMigrate", "client", JoinType.LEFT_OUTER_JOIN);
        visitorCriteria.add(Restrictions.eq("visitor.resolution", VisitReqResolutionHist.RES_OVERDUE_SERVER));
        visitorCriteria.add(Restrictions.in("client.idOfClientGroup", predefined));
        visitorCriteria.add(Restrictions.eq("visitor.initiator", VisitReqResolutionHistInitiatorEnum.INITIATOR_ISPP));

        visitReqResolutionHists = visitorCriteria.list();

        for (VisitReqResolutionHist value : visitReqResolutionHists) {
            migrantList.add(value.getMigrant());
        }

        return migrantList;
    }

    public static List<Migrant> getMigrationForListOfOrgVisit(Session session, List<Long> idOfOrgs, Date startTime, Date endTime)throws Exception {
        Date date = new Date();
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.createCriteria("orgVisit", "org", JoinType.INNER_JOIN);
        criteria.add(Restrictions.in("org.idOfOrg", idOfOrgs));
        criteria.add(Restrictions.le("visitStartDate", endTime));
        criteria.add(Restrictions.ge("visitEndDate", startTime));
        return criteria.list();
    }

    public static Migrant findMigrant(Session session, Long orgVisitId, Long clientMigrateId) {
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.add(Restrictions.eq("orgVisit.idOfOrg", orgVisitId));
        criteria.add(Restrictions.eq("clientMigrate.idOfClient", clientMigrateId));
        criteria.add(Restrictions.ne("syncState", Migrant.CLOSED));
        List<Migrant> migrantList = criteria.list();
        if (migrantList.isEmpty()) {
            return null;
        }
        return (Migrant) migrantList.get(0);
    }

    public static Migrant findActiveMigrant(Session session, Long orgVisitId, Long clientMigrateId) {
        Criteria criteria = session.createCriteria(Migrant.class);
        criteria.add(Restrictions.eq("orgVisit.idOfOrg", orgVisitId));
        criteria.add(Restrictions.eq("clientMigrate.idOfClient", clientMigrateId));
        criteria.add(Restrictions.gt("visitEndDate", new Date()));
        criteria.add(Restrictions.ne("syncState", Migrant.CLOSED));
        List<Migrant> migrantList = criteria.list();
        if (migrantList.isEmpty()) {
            return null;
        }
        return (Migrant) migrantList.get(0);
    }

    public static void disableMigrantRequestIfExists(Session session, Long orgVisitId, Long clientMigrateId) {
        Migrant migrant = findMigrant(session, orgVisitId, clientMigrateId);
        if (null != migrant) {
            disableMigrant(migrant.getCompositeIdOfMigrant());
        }
    }

    public static void createMigrantRequestForGuardianIfNoPass(
            Session session,
            Client client,
            Org orgVisit,
            MigrantInitiatorEnum initiator,
            VisitReqResolutionHistInitiatorEnum resInitiator,
            int years) {
        if (!client.getOrg().getIdOfOrg().equals(orgVisit.getIdOfOrg())) {
            if (!DAOUtils.isFriendlyOrganizations(session, client.getOrg(), orgVisit)) {
                if (MigrantsUtils.findActiveMigrant(
                        session, orgVisit.getIdOfOrg(), client.getIdOfClient()) == null) {
                    ClientManager.createMigrationForGuardianWithConfirm(
                            session,
                            client,
                            new Date(),
                            orgVisit,
                            initiator,
                            resInitiator,
                            years);
                }
            }
        }
    }
}
