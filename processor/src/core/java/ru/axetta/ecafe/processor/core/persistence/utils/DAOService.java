/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.IConfigProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

@Component
@Scope("singleton")
public class DAOService {

    private final static Logger logger = LoggerFactory.getLogger(DAOService.class);

    @PersistenceContext
    EntityManager em;

    @Transactional
    public List<TransactionJournal> fetchTransactionJournal(int nRecs) {
        return DAOUtils.fetchTransactionJournalRecs(em, nRecs);
    }

    public static DAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOService.class);
    }

    @Transactional
    public void setConfigurationProviderInOrg(Long idOfOrg, ConfigurationProvider configurationProvider) {
        Org org = em.find(Org.class, idOfOrg);
        if (org != null) {
            org.setConfigurationProvider(configurationProvider);
            em.persist(org);
        }
    }

    @Transactional
    public Contragent getContragentByName(String name) {
        TypedQuery<Contragent> query = em.createQuery("from Contragent where contragentName=:name", Contragent.class);
        query.setParameter("name", name);
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);


    }

    @Transactional
    public void setDeletedState(DistributedObject distributedObject) {
        distributedObject = em.find(distributedObject.getClass(), distributedObject.getGlobalId());
        distributedObject.setDeletedState(true);
        em.persist(distributedObject);
    }

    public <T> T findDistributedObjectByRefGUID(Class<T> clazz, String guid) {
        TypedQuery<T> query = em.createQuery("from " + clazz.getSimpleName() + " where guid='" + guid + "'", clazz);
        List<T> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public ConfigurationProvider getConfigurationProvider(Long idOfConfigurationProvider) throws Exception {
        return em.find(ConfigurationProvider.class, idOfConfigurationProvider);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long updateVersionByDistributedObjects(String name) {
        TypedQuery<DOVersion> query = em
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                        DOVersion.class);
        query.setParameter("distributedObjectClassName", name.toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = em.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName(name);
        em.persist(doVersion);
        em.flush();
        return version;
    }

    @Transactional
    public DistributedObject mergeDistributedObject(DistributedObject distributedObject, Long globalVersion) {
        TypedQuery<DistributedObject> query = em.createQuery(
                "from " + distributedObject.getClass().getSimpleName() + " where guid='" + distributedObject.getGuid()
                        + "'", DistributedObject.class);
        List<DistributedObject> distributedObjectList = query.getResultList();
        if (distributedObjectList.isEmpty()) {
            return null;
        }
        DistributedObject d = em.find(distributedObject.getClass(), distributedObjectList.get(0).getGlobalId());
        d.fill(distributedObject);
        d.setGlobalVersion(globalVersion);
        d.setDeletedState(distributedObject.getDeletedState());
        d.setLastUpdate(new Date());
        em.persist(d);
        return em.find(distributedObject.getClass(), distributedObjectList.get(0).getGlobalId());
    }

    @Transactional
    public void persistEntity(Object entity) throws Exception {
        em.persist(entity);
    }

    @Transactional
    public Long getClientContractIdByCardId(String idOfCard) throws Exception {
        Client cl = DAOUtils.findClientByCardNo(em, Long.decode(idOfCard));
        if (cl == null) {
            return null;
        }
        return cl.getContractId();
    }

    @Transactional
    public void deleteEntity(Object entity) {
        entity = em.merge(entity);
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Transactional
    public Long getContractIdByCardNo(long lCardId) throws Exception {
        Client client = DAOUtils.findClientByCardNo(em, lCardId);
        if (client != null) {
            return client.getContractId();
        }
        return null;
    }

    @Transactional
    public boolean enableClientNotificationBySMS(Long contractId, boolean state) {
        Query q = em.createQuery("update Client set notifyViaSMS=:notifyViaSMS where contractId=:contractId");
        q.setParameter("notifyViaSMS", state);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean enableClientNotificationByEmail(Long contractId, boolean state) {
        Query q = em.createQuery("update Client set notifyViaEmail=:notifyViaEmail where contractId=:contractId");
        q.setParameter("notifyViaEmail", state);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean setClientMobilePhone(Long contractId, String mobile) {
        Query q = em.createQuery("update Client set mobile=:mobile where contractId=:contractId");
        q.setParameter("mobile", mobile);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean setClientPhone(Long contractId, String phone) {
        Query q = em.createQuery("update Client set phone=:phone where contractId=:contractId");
        q.setParameter("phone", phone);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean setClientAddress(Long contractId, String address) {
        Query q = em.createQuery("update Client set address=:address where contractId=:contractId");
        q.setParameter("address", address);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean setClientEmail(Long contractId, String email) {
        Query q = em.createQuery("update Client set email=:email where contractId=:contractId");
        q.setParameter("email", email);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean setClientPassword(Long contractId, String base64passwordHash) {
        Query q = em.createQuery("update Client set cypheredPassword=:base64passwordHash where contractId=:contractId");
        q.setParameter("base64passwordHash", base64passwordHash);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public boolean setClientExpenditureLimit(Long contractId, long limit) {
        Query q = em.createQuery("update Client set expenditureLimit=:expenditureLimit where contractId=:contractId");
        q.setParameter("expenditureLimit", limit);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    @Transactional
    public Org getOrg(Long idOfOrg) {
        Query q = em.createQuery("from Org where idOfOrg = :idOfOrg");
        q.setParameter("idOfOrg", idOfOrg);
        List l = q.getResultList();
        if (l.size() == 0) {
            return null;
        }
        return (Org) l.get(0);
    }

    @Transactional
    public User setUserInfo(User user) {
        return em.merge(user);
    }

    @Transactional
    public Client getClientByContractId(long contractId) throws Exception {
        Client cl = DAOUtils.findClientByContractId(em, contractId);
        if (cl == null) {
            return null;
        }
        return cl;
    }

    @Transactional
    public void addIntegraPartnerAccessPermissionToClient(Long idOfClient, String idOfIntegraPartner) throws Exception {
        Client cl = em.find(Client.class, idOfClient);
        if (cl == null) {
            throw new Exception("Client not found: " + idOfClient);
        }
        cl.addIntegraPartnerAccessPermission(idOfIntegraPartner);
        em.persist(cl);
    }

    @Transactional
    public List<TechnologicalMapProduct> getTechnologicalMapProducts(TechnologicalMap technologicalMap) {
        TypedQuery<TechnologicalMapProduct> query = em
                .createQuery("from TechnologicalMapProduct where technologicalMap=:technologicalMap",
                        TechnologicalMapProduct.class);
        query.setParameter("technologicalMap", technologicalMap);
        return query.getResultList();
    }

    @Transactional
    public Org findOrById(long idOfOrg) {
        return em.find(Org.class, idOfOrg);
    }

    @Transactional
    public Client findClientById(long idOfClient) {
        return em.find(Client.class, idOfClient);
    }

    @Transactional
    public <T> T saveEntity(T entity) {
        return em.merge(entity);
    }

    @Transactional
    public Client findAndDeleteLinkingToken(String linkingToken) {
        Query query = em.createQuery("from LinkingToken where token=:token");
        query.setParameter("token", linkingToken);
        try {
            LinkingToken token = (LinkingToken) query.getSingleResult();
            em.remove(token);
            return em.find(Client.class, token.getIdOfClient());
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public LinkingToken generateLinkingToken(Client client) {
        Query query = em.createQuery("delete from LinkingToken where idOfClient=:idOfClient");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.executeUpdate();
        ////
        SecureRandom secureRandom = new SecureRandom();
        String randomToken;
        int nSize = 9;
        for (int nCycle = 0; ; nCycle++) {
            if (nCycle == 10) {
                nSize++;
                nCycle = 0;
            }
            randomToken = new BigInteger(nSize * 5, secureRandom).toString(32);
            query = em.createQuery("from LinkingToken where token=:token");
            query.setParameter("token", randomToken);
            List l = query.getResultList();
            if (l.size() == 0) {
                break;
            }
        }
        LinkingToken token = new LinkingToken();
        token.setIdOfClient(client.getIdOfClient());
        token.setToken(randomToken);
        em.persist(token);
        return token;
    }

    @Transactional
    public boolean doesClientBelongToFriendlyOrgs(Long orgId, Long idOfClient) throws Exception {
        Org org = em.find(Org.class, orgId);
        if (org == null) {
            throw new Exception("Организация не найдена: " + orgId);
        }
        Client cl = em.find(Client.class, idOfClient);
        if (cl == null) {
            throw new Exception("Клиент не найден: " + idOfClient);
        }
        if (cl.getOrg().getIdOfOrg() == orgId) {
            return true;
        }
        Set<Org> friendlyOrgs = org.getFriendlyOrg();
        for (Org o : friendlyOrgs) {
            if (cl.getOrg().getIdOfOrg() == o.getIdOfOrg()) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public List<Client> findClientsByMobilePhone(String mobilePhone) {
        TypedQuery<Client> query = em.createQuery("from Client where mobile=:mobile", Client.class);
        query.setParameter("mobile", mobilePhone);
        return query.getResultList();
    }

    @Transactional
    public Contragent getClientOrgDefaultSupplier(Client client) {
        client = em.merge(client);
        Contragent ca = client.getOrg().getDefaultSupplier();
        ca.getContragentName(); //lazy load
        return ca;
    }

    @Transactional
    public ReportInfo registerReport(String ruleName, int documentFormat, String reportName, Date createdDate,
            Long generationTime, Date startDate, Date endDate, String reportFile, String orgNum, Long idOfOrg,
            String tag) {
        ReportInfo ri = new ReportInfo(ruleName, documentFormat, reportName, createdDate, generationTime, startDate,
                endDate, reportFile, orgNum, idOfOrg, tag);
        em.persist(ri);
        return ri;
    }

    @Transactional
    public List<String> getReportHandleRuleNames() {
        TypedQuery<String> query = em
                .createQuery("select ruleName from ReportHandleRule order by ruleName", String.class);
        return query.getResultList();
    }

    @Transactional
    public void updateLastSuccessfulBalanceSync(long idOfOrg) {
        Query q = em.createQuery("update Org set lastSuccessfulBalanceSync=:date where idOfOrg=:idOfOrg");
        q.setParameter("date", new Date());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }

    @Transactional
    public void updateLastUnsuccessfulBalanceSync(long idOfOrg) {
        Query q = em.createQuery("update Org set lastUnSuccessfulBalanceSync=:date where idOfOrg=:idOfOrg");
        q.setParameter("date", new Date());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }

    @Transactional
    public List<Org> getOrderedSynchOrgsList() {
        TypedQuery<Org> query = em.createQuery("from Org order by lastSuccessfulBalanceSync", Org.class);
        return query.getResultList();
    }

    @Transactional
    public long getStatClientsCount() {
        Query q = em.createNativeQuery("select count(*) from cf_clients");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientsWithMobile() {
        Query q = em.createNativeQuery("select count(*) from cf_clients where mobile is not null and mobile<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientsWithEmail() {
        Query q = em.createNativeQuery("select count(*) from cf_clients where email is not null and email<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatUniqueClientsWithPaymentTransaction() {
        Query q = em.createNativeQuery("select count(distinct idOfClient) from cf_transactions");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatUniqueClientsWithEnterEvent() {
        Query q = em.createNativeQuery("select count(distinct idOfClient) from cf_enterevents");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientPaymentsCount() {
        Query q = em.createNativeQuery("select count(*) from cf_clientpayments");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatOrdersCount() {
        Query q = em.createNativeQuery("select count(*) from cf_orders");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getEnterEventsCount() {
        Query q = em.createNativeQuery("select count(*) from cf_enterevents");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getClientSmsCount() {
        Query q = em.createNativeQuery("select count(*) from cf_clientsms");
        return Long.parseLong("" + q.getSingleResult());
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getStatPaymentsByContragents(Date fromDate, Date toDate) {
        Query q = em.createNativeQuery(
                "select contragentName, paymentMethod, avg(paysum), sum(paysum), count(*) from cf_clientpayments cp join cf_contragents cc on cp.idOfContragent=cc.idOfContragent where cp.createdDate>=:fromDate and cp.createdDate<:toDate group by contragentName, paymentMethod order by contragentName, paymentMethod");
        q.setParameter("fromDate", fromDate.getTime());
        q.setParameter("toDate", toDate.getTime());
        return (List<Object[]>) q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getMonitoringPayLastTransactionStats() {
        Query q = em.createNativeQuery(
                "select cp.idOfContragent, cc.contragentName, max(cp.createddate) from cf_clientpayments cp, cf_contragents cc where cp.idOfContragent=cc.idOfContragent and cc.classid=1 group by cp.idOfContragent, cc.contragentName");
        return (List<Object[]>) q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getMonitoringPayDayTransactionsStats(Date fromDate, Date toDate) {
        Query q = em.createNativeQuery(
                "select cp.idOfContragent, cc.contragentName, count(*) from cf_clientpayments cp, cf_contragents cc where cp.idOfContragent=cc.idOfContragent and cc.classid=1 and cp.createdDate>=:fromDate and cp.createdDate<:toDate group by cp.idOfContragent, cc.contragentName");
        q.setParameter("fromDate", fromDate.getTime());
        q.setParameter("toDate", toDate.getTime());
        return (List<Object[]>) q.getResultList();
    }


    @Transactional
    public boolean setCardStatus(long idOfCard, int state, String reason) {
        Query q = em.createNativeQuery("UPDATE cf_cards set state=:state, lockreason=:reason where idofCard=:idOfCard");
        q.setParameter("state", state);
        q.setParameter("reason", reason);
        q.setParameter("idOfCard", idOfCard);
        return q.executeUpdate () > 0;
    }


    public final static int GROUP_TYPE_STUDENTS = 0, GROUP_TYPE_NON_STUDENTS = 1;

    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getOrgEntersCountByGroupType(Date at, Date to, int groupType) {
        String sql = "";
        if (groupType == GROUP_TYPE_STUDENTS) {
            sql = "select cf_enterevents.idoforg, count(distinct cf_enterevents.idofclient) " +
                    "from cf_enterevents " +
                    "left join cf_clients on cf_enterevents.idoforg=cf_clients.idoforg and cf_enterevents.idofclient=cf_clients.idofclient "
                    +
                    "where cf_enterevents.evtdatetime BETWEEN :dateAt AND :dateTo and cf_clients.idOfClientGroup<:studentsMaxValue "
                    +
                    "group by cf_enterevents.idoforg";
        } else {
            sql = "select cf_enterevents.idoforg, count(distinct cf_enterevents.idofclient) " +
                    "from cf_enterevents " +
                    "left join cf_clients on cf_enterevents.idoforg=cf_clients.idoforg and cf_enterevents.idofclient=cf_clients.idofclient "
                    +
                    "where cf_enterevents.evtdatetime BETWEEN :dateAt AND :dateTo and cf_clients.idOfClientGroup>=:nonStudentGroups and cf_clients.idOfClientGroup<:leavingClientGroup "
                    +
                    "group by cf_enterevents.idoforg";
        }

        try {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = em.createNativeQuery(sql);
            q.setParameter("dateAt", at.getTime());
            q.setParameter("dateTo", to.getTime());
            if (groupType == GROUP_TYPE_STUDENTS) {
                q.setParameter("studentsMaxValue", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            } else {
                q.setParameter("nonStudentGroups", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                q.setParameter("leavingClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            }
            List resultList = q.getResultList();

            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                res.put(((BigInteger) e[0]).longValue(), ((BigInteger) e[1]).intValue());
            }
            return res;
        } catch (Exception e) {
            logger.error("Failed to load data", e);
        }
        return Collections.EMPTY_MAP;
    }


    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getOrgOrdersCountByGroupType(Date at, Date to, int groupType) {
        String sql = "";
        if (groupType == GROUP_TYPE_STUDENTS) {
            sql = "select cf_orders.idoforg, count(distinct cf_orders.idofclient) " +
                    "from cf_orders " +
                    "left join cf_clients on cf_orders.idofclient=cf_clients.idofclient " +
                    "where cf_orders.createddate BETWEEN :dateAt AND :dateTo and cf_clients.idOfClientGroup<:studentsMaxValue "
                    +
                    "group by cf_orders.idoforg";
        } else {
            sql = "select cf_orders.idoforg, count(distinct cf_orders.idofclient) " +
                    "from cf_orders " +
                    "left join cf_clients on cf_orders.idofclient=cf_clients.idofclient " +
                    "where cf_orders.createddate BETWEEN :dateAt AND :dateTo and cf_clients.idOfClientGroup>=:nonStudentGroups and cf_clients.idOfClientGroup<:leavingClientGroup "
                    +
                    "group by cf_orders.idoforg";
        }

        try {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = em.createNativeQuery(sql);
            q.setParameter("dateAt", at.getTime());
            q.setParameter("dateTo", to.getTime());
            if (groupType == GROUP_TYPE_STUDENTS) {
                q.setParameter("studentsMaxValue", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            } else {
                q.setParameter("nonStudentGroups", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                q.setParameter("leavingClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            }
            List resultList = q.getResultList();

            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                res.put(((BigInteger) e[0]).longValue(), ((BigInteger) e[1]).intValue());
            }
            return res;
        } catch (Exception e) {
            logger.error("Failed to load data", e);
        }
        return Collections.EMPTY_MAP;
    }


    @SuppressWarnings("unchecked")
    public Map <Long, Integer> getProposalOrgDiscounsCountByGroupType (Date at, Date to, int groupType)
    {
        String sql = "";
        if (groupType == DAOService.GROUP_TYPE_STUDENTS) {
            sql = "select idoforg, count(distinct cf_clientscomplexdiscounts.idofclient) " +
                    "from cf_clients " +
                    "left join cf_clientscomplexdiscounts on cf_clients.idofclient=cf_clientscomplexdiscounts.idofclient " +
                    "where createdate between :dateAt and :dateTo and cf_clients.idOfClientGroup<:studentsMaxValue " +
                    "group by idoforg " +
                    "having count(cf_clientscomplexdiscounts.idofclient)<>0";
        } else {
            sql = "select idoforg, count(distinct cf_clientscomplexdiscounts.idofclient) " +
                    "from cf_clients " +
                    "left join cf_clientscomplexdiscounts on cf_clients.idofclient=cf_clientscomplexdiscounts.idofclient " +
                    "where createdate between :dateAt and :dateTo and cf_clients.idOfClientGroup>=:nonStudentGroups and cf_clients.idOfClientGroup<:leavingClientGroup " +
                    "group by idoforg " +
                    "having count(cf_clientscomplexdiscounts.idofclient)<>0";
        }
        try
        {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = em.createNativeQuery (sql);
            q.setParameter("dateAt", at.getTime());
            q.setParameter("dateTo", to.getTime());
            if (groupType == DAOService.GROUP_TYPE_STUDENTS) {
                q.setParameter("studentsMaxValue", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            } else {
                q.setParameter("nonStudentGroups", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                q.setParameter("leavingClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            }
            List resultList = q.getResultList();
            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                res.put(((BigInteger) e[0]).longValue(), ((BigInteger) e[1]).intValue());
            }
            return res;
        } catch (Exception e) {
            logger.error("Failed to load data", e);
        }
        return Collections.EMPTY_MAP;
    }


    @SuppressWarnings("unchecked")
    public Map <Long, Integer> getOrgUniqueOrdersCountByGroupType (Date at, Date to, int groupType)
        {
        String sql = "";
        if (groupType == DAOService.GROUP_TYPE_STUDENTS) {
            sql = "select cf_orders.idoforg, count(distinct cf_orders.idofclient) "+
                  "from cf_orders "+
                  "left join cf_clients on cf_clients.idofclient=cf_orders.idofclient "+
                  "where createddate between :dateAt and :dateTo and "+
                        "cf_clients.idOfClientGroup<:studentsMaxValue and cf_orders.socdiscount<>0 "+
                  "group by cf_orders.idoforg";
            }
        else
            {
            sql = "select cf_orders.idoforg, count(distinct cf_orders.idofclient) "+
                    "from cf_orders "+
                    "left join cf_clients on cf_clients.idofclient=cf_orders.idofclient "+
                    "where createddate between :dateAt and :dateTo and cf_orders.socdiscount<>0 and "+
                          "cf_clients.idOfClientGroup>=:nonStudentGroups and cf_clients.idOfClientGroup<:leavingClientGroup "+
                    "group by cf_orders.idoforg";
            }

        try
        {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = em.createNativeQuery (sql);
            q.setParameter("dateAt", at.getTime());
            q.setParameter("dateTo", to.getTime());
            if (groupType == DAOService.GROUP_TYPE_STUDENTS) {
                q.setParameter("studentsMaxValue", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            } else {
                q.setParameter("nonStudentGroups", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                q.setParameter("leavingClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            }
            List resultList = q.getResultList();
            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                res.put(((BigInteger) e[0]).longValue(), ((BigInteger) e[1]).intValue());
            }
            return res;
        } catch (Exception e) {
            logger.error("Failed to load data", e);
        }
        return Collections.EMPTY_MAP;
        }


    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getOrgOrdersCount(Date at, Date to) {
        try {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = em.createNativeQuery("select cf_orders.idoforg, count(distinct cf_orders.idoforder) " +
                    "from cf_orders " +
                    "where cf_orders.createddate BETWEEN :dateAt AND :dateTo " +
                    "group by cf_orders.idoforg");
            q.setParameter("dateAt", at.getTime());
            q.setParameter("dateTo", to.getTime());
            List resultList = q.getResultList();

            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                res.put(((BigInteger) e[0]).longValue(), ((BigInteger) e[1]).intValue());
            }
            return res;
        } catch (Exception e) {
            logger.error("Failed to load data", e);
        }
        return Collections.EMPTY_MAP;
    }
}
