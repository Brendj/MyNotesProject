/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

@Component
@Scope("singleton")
@Transactional
public class DAOService {

    private final static Logger logger = LoggerFactory.getLogger(DAOService.class);

    public final static int GROUP_TYPE_STUDENTS = 0, GROUP_TYPE_NON_STUDENTS = 1;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public static DAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOService.class);
    }

    public List<CategoryDiscount> getCategoryDiscountList() {
        TypedQuery<CategoryDiscount> q = entityManager.createQuery("from CategoryDiscount order by idOfCategoryDiscount", CategoryDiscount.class);
        return q.getResultList();
    }

    public List<Contragent> getContragentsWithClassIds(List<Integer> classIds) {
        TypedQuery<Contragent> q = entityManager.createQuery("from Contragent where classId in (:classIds)", Contragent.class);
        q.setParameter("classIds", classIds);
        return q.getResultList();
    }

    public List<TransactionJournal> fetchTransactionJournal(int nRecs) {
        return DAOUtils.fetchTransactionJournalRecs(entityManager, nRecs);
    }

    public User findUserByUserName(String userName) throws Exception {
        javax.persistence.Query q = entityManager.createQuery("from User where userName=:userName");
        q.setParameter("userName", userName);
        return (User) q.getSingleResult();
    }

    public User setUserInfo(User user) {
        return entityManager.merge(user);
    }

    public Boolean isMenuExchange(Long idOfOrg){
        TypedQuery<Long> query = entityManager.createQuery("select idOfSourceOrg from MenuExchangeRule where idOfSourceOrg = :idOfSourceOrg",Long.class);
        query.setParameter("idOfSourceOrg",idOfOrg);
        List<Long> list = query.getResultList();
        return !list.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public List<ECafeSettings> geteCafeSettingses(final Long idOfOrg,final SettingsIds settingsIds,final Boolean deleted) {
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(ECafeSettings.class);
        if(idOfOrg==null && settingsIds==null){
            return new ArrayList<ECafeSettings>(0);
        }
        if(idOfOrg!=null){
            criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        }
        if(settingsIds!=null){
            criteria.add(Restrictions.eq("settingsId",settingsIds));
        }
        if(!deleted){
            criteria.add(Restrictions.eq("deletedState",false));
        }
        List<ECafeSettings> list =(List<ECafeSettings>) criteria.list();
        return list;
    }

    public ConfigurationProvider onSave(ConfigurationProvider configurationProvider, User currentUser, List<Long> idOfOrgList) throws Exception {
        ConfigurationProvider cp = entityManager.find(ConfigurationProvider.class, configurationProvider.getIdOfConfigurationProvider());
        cp.setName(configurationProvider.getName());
        cp.setLastUpdate(new Date());
        cp.setUserEdit(currentUser);
        if(!cp.getOrgs().isEmpty()){
            for (Org org: cp.getOrgs()){
                org = entityManager.merge(org);
                org.setConfigurationProvider(null);
                org = entityManager.merge(org);
            }
        }
        cp.getOrgs().clear();
        configurationProvider = entityManager.merge(cp);
        if(!idOfOrgList.isEmpty()){
            for (Long idOfOrg: idOfOrgList){
                Org org = entityManager.find(Org.class, idOfOrg);
                if (org != null) {
                    org.setConfigurationProvider(configurationProvider);
                    entityManager.persist(org);
                }
            }
        }
        return configurationProvider;
    }

    public Contragent getContragentByName(String name) {
        TypedQuery<Contragent> query = entityManager.createQuery("from Contragent where contragentName=:name", Contragent.class);
        query.setParameter("name", name);
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);


    }

    public void updateClientSetGUID(List<Long> clients) throws Exception {
        for (Long id : clients) {
            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion((Session) entityManager.getDelegate());
            Query query = entityManager.createQuery("update Client set clientGUID=:uuid, clientRegistryVersion=:version where idOfClient=:idOfClient");
            query.setParameter("idOfClient", id);
            query.setParameter("version", clientRegistryVersion);
            query.setParameter("uuid", UUID.randomUUID().toString());
            int result = query.executeUpdate();
            if(result==0){
                logger.error("Error edit uuid by client: idOfClient="+id);
            }
        }

    }

    public void setDeletedState(DistributedObject distributedObject) {
        distributedObject = entityManager.find(distributedObject.getClass(), distributedObject.getGlobalId());
        distributedObject.setDeletedState(true);
        entityManager.persist(distributedObject);
    }

    public <T> T findDistributedObjectByRefGUID(Class<T> clazz, String guid) {
        TypedQuery<T> query = entityManager.createQuery("from " + clazz.getSimpleName() + " where guid='" + guid + "'", clazz);
        List<T> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }


    public ConfigurationProvider getConfigurationProvider(Long idOfConfigurationProvider) throws Exception {
        return entityManager.find(ConfigurationProvider.class, idOfConfigurationProvider);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long updateVersionByDistributedObjects(String name) {
        TypedQuery<DOVersion> query = entityManager
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
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName(name);
        entityManager.persist(doVersion);
        entityManager.flush();
        return version;
    }


    public void updateGoodsBasicBasket(GoodsBasicBasket goodsBasicBasket){
        Query query = entityManager.createQuery("update GoodsBasicBasket set lastUpdate=:lastUpdate, nameOfGood=:nameOfGood, unitsScale=:unitsScale, netWeight=:netWeight where idOfBasicGood=:idOfBasicGood ");
        query.setParameter("lastUpdate", new Date());
        query.setParameter("nameOfGood", goodsBasicBasket.getNameOfGood());
        query.setParameter("unitsScale", goodsBasicBasket.getUnitsScale());
        query.setParameter("netWeight", goodsBasicBasket.getNetWeight());
        query.setParameter("idOfBasicGood", goodsBasicBasket.getIdOfBasicGood());
        query.executeUpdate();
    }


    public void removeGoodsBasicBasket(Long idOfBasicGood){
        Query query = entityManager.createQuery("delete from GoodsBasicBasket where idOfBasicGood=:idOfBasicGood");
        query.setParameter("idOfBasicGood",idOfBasicGood);
        query.executeUpdate();
    }


    public void removeTechnologicalMap(Long idOfTechnologicalMaps){
        Query query1 = entityManager.createNativeQuery("DELETE FROM cf_technological_map_products where idoftechnologicalmaps="+idOfTechnologicalMaps);
        query1.executeUpdate();
        Query query = entityManager.createNativeQuery("DELETE FROM cf_technological_map where idoftechnologicalmaps="+idOfTechnologicalMaps);
        query.executeUpdate();
    }



    public void removeGoodGroup(GoodGroup goodGroup){
         GoodGroup group = entityManager.merge(goodGroup);
         entityManager.remove(group);
    }


    public void removeSetting(ECafeSettings eCafeSettings){
        ECafeSettings settings = entityManager.merge(eCafeSettings);
        entityManager.remove(settings);
    }


    public void removeGood(Good good){
        Good g = entityManager.merge(good);
        entityManager.remove(g);
    }


    public void removeProduct(Product product){
        Product p = entityManager.merge(product);
        entityManager.remove(p);
    }


    public Boolean isEmptyOrgConfigurationProvider(ConfigurationProvider configurationProvider){
        ConfigurationProvider cp = entityManager.merge(configurationProvider);
        return cp.getOrgEmpty();
    }


    public void removeConfigurationProvider(ConfigurationProvider configurationProvider) throws Exception{
        ConfigurationProvider cp = entityManager.merge(configurationProvider);
        entityManager.remove(cp);
    }


    public DistributedObject mergeDistributedObject(DistributedObject distributedObject, Long globalVersion) {
        TypedQuery<DistributedObject> query = entityManager.createQuery(
                "from " + distributedObject.getClass().getSimpleName() + " where guid='" + distributedObject.getGuid()
                        + "'", DistributedObject.class);
        List<DistributedObject> distributedObjectList = query.getResultList();
        if (distributedObjectList.isEmpty()) {
            return null;
        }
        DistributedObject d = entityManager.find(distributedObject.getClass(), distributedObjectList.get(0).getGlobalId());
        d.fill(distributedObject);
        d.setGlobalVersion(globalVersion);
        d.setDeletedState(distributedObject.getDeletedState());
        d.setLastUpdate(new Date());
        entityManager.persist(d);
        return entityManager.find(distributedObject.getClass(), distributedObjectList.get(0).getGlobalId());
    }

    public void persistEntity(Object entity) throws Exception {
        entityManager.persist(entity);
    }


    public void deleteEntity(Object entity) {
        entity = entityManager.merge(entity);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    public Long getContractIdByCardNo(long lCardId) throws Exception {
        TypedQuery<Long> query = entityManager.createQuery("select card.client.contractId from Card card where card.cardNo=:cardNo", Long.class);
        query.setParameter("cardNo", lCardId);
        List<Long> list = query.getResultList();
        if(list==null || list.isEmpty()){
            return null;
        } else {
            return list.get(0);
        }
    }


    public Long getContractIdByTempCardNoAndCheckValidDate(long lCardId) throws Exception {
        /* так как в поле хранится дата на 00:00 ночи текущего дня вычтем из текущего дня 24 часа в милисекудах */
        Long currentDay = System.currentTimeMillis() - 86400000L;
        TypedQuery<Long> query = entityManager.createQuery("select cl.contractId from CardTemp card left join card.client cl where card.cardNo=:cardNo and card.validDate>:currentDay", Long.class);
        query.setParameter("cardNo", lCardId);
        query.setParameter("currentDay", currentDay);
        List<Long> list = query.getResultList();
        if(list==null || list.isEmpty()){
            return null;
        } else {
            return list.get(0);
        }
    }


    public boolean enableClientNotificationBySMS(Long contractId, boolean state) {
        Query q = entityManager.createQuery("update Client set notifyViaSMS=:notifyViaSMS where contractId=:contractId");
        q.setParameter("notifyViaSMS", state);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }


    public boolean enableClientNotificationByEmail(Long contractId, boolean state) {
        Query q = entityManager.createQuery("update Client set notifyViaEmail=:notifyViaEmail where contractId=:contractId");
        q.setParameter("notifyViaEmail", state);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }


    public boolean setClientMobilePhone(Long contractId, String mobile) {
        Query q = entityManager.createQuery("update Client set mobile=:mobile where contractId=:contractId");
        q.setParameter("mobile", mobile);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }


    public boolean setClientPhone(Long contractId, String phone) {
        Query q = entityManager.createQuery("update Client set phone=:phone where contractId=:contractId");
        q.setParameter("phone", phone);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }


    public boolean setClientAddress(Long contractId, String address) {
        Query q = entityManager.createQuery("update Client set address=:address where contractId=:contractId");
        q.setParameter("address", address);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }


    public boolean setClientEmail(Long contractId, String email) {
        Query q = entityManager.createQuery("update Client set email=:email where contractId=:contractId");
        q.setParameter("email", email);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }


    public boolean setClientPassword(Long contractId, String base64passwordHash) {
        Query q = entityManager.createQuery("update Client set cypheredPassword=:base64passwordHash where contractId=:contractId");
        q.setParameter("base64passwordHash", base64passwordHash);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }


    public boolean setClientExpenditureLimit(Long contractId, long limit) {
        Query q = entityManager.createQuery("update Client set expenditureLimit=:expenditureLimit where contractId=:contractId");
        q.setParameter("expenditureLimit", limit);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }


    public Org getOrg(Long idOfOrg) {
        Query q = entityManager.createQuery("from Org where idOfOrg = :idOfOrg");
        q.setParameter("idOfOrg", idOfOrg);
        List l = q.getResultList();
        if (l.size() == 0) {
            return null;
        }
        return (Org) l.get(0);
    }


    public Client getClientByContractId(long contractId) throws Exception {
        Client cl = DAOUtils.findClientByContractId(entityManager, contractId);
        if (cl == null) {
            return null;
        }
        return cl;
    }


    public void addIntegraPartnerAccessPermissionToClient(Long idOfClient, String idOfIntegraPartner) throws Exception {
        Client cl = entityManager.find(Client.class, idOfClient);
        if (cl == null) {
            throw new Exception("Client not found: " + idOfClient);
        }
        cl.addIntegraPartnerAccessPermission(idOfIntegraPartner);
        entityManager.persist(cl);
    }


    public List<TechnologicalMapProduct> getTechnologicalMapProducts(TechnologicalMap technologicalMap) {
        TypedQuery<TechnologicalMapProduct> query = entityManager
                .createQuery("from TechnologicalMapProduct where technologicalMap=:technologicalMap",
                        TechnologicalMapProduct.class);
        query.setParameter("technologicalMap", technologicalMap);
        return query.getResultList();
    }


    public List<TechnologicalMap> findTechnologicalMapByTechnologicalMapGroup(TechnologicalMapGroup technologicalMapGroup){
        TypedQuery<TechnologicalMap> query = entityManager.createQuery(
                "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup", TechnologicalMap.class);
        query.setParameter("technologicalMapGroup",technologicalMapGroup);
        return query.getResultList();
    }


    public Org findOrById(long idOfOrg) {
        return entityManager.find(Org.class, idOfOrg);
    }
    
    public ReportInfo findReportInfoById(long idOfReportInfo) {
        return entityManager.find(ReportInfo.class, idOfReportInfo);
    }


    public Client findClientById(long idOfClient) {
        return entityManager.find(Client.class, idOfClient);
    }


    public <T> T saveEntity(T entity) {
        return entityManager.merge(entity);
    }


    public Client findAndDeleteLinkingToken(String linkingToken) {
        Query query = entityManager.createQuery("from LinkingToken where token=:token");
        query.setParameter("token", linkingToken);
        try {
            LinkingToken token = (LinkingToken) query.getSingleResult();
            entityManager.remove(token);
            return entityManager.find(Client.class, token.getIdOfClient());
        } catch (NoResultException e) {
            return null;
        }
    }


    public LinkingToken generateLinkingToken(Client client) {
        Query query = entityManager.createQuery("delete from LinkingToken where idOfClient=:idOfClient");
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
            query = entityManager.createQuery("from LinkingToken where token=:token");
            query.setParameter("token", randomToken);
            List l = query.getResultList();
            if (l.size() == 0) {
                break;
            }
        }
        LinkingToken token = new LinkingToken();
        token.setIdOfClient(client.getIdOfClient());
        token.setToken(randomToken);
        entityManager.persist(token);
        return token;
    }


    public boolean doesClientBelongToFriendlyOrgs(Long orgId, Long idOfClient) throws Exception {
        Org org = entityManager.find(Org.class, orgId);
        if (org == null) {
            throw new Exception("Организация не найдена: " + orgId);
        }
        Client cl = entityManager.find(Client.class, idOfClient);
        if (cl == null) {
            throw new Exception("Клиент не найден: " + idOfClient);
        }
        if (cl.getOrg().getIdOfOrg().equals(orgId)) {
            return true;
        }
        Set<Org> friendlyOrgs = org.getFriendlyOrg();
        for (Org o : friendlyOrgs) {
            if (cl.getOrg().getIdOfOrg().equals(o.getIdOfOrg())) {
                return true;
            }
        }
        return false;
    }


    public List<Client> findClientsByMobilePhone(String mobilePhone) {
        TypedQuery<Client> query = entityManager.createQuery("from Client where mobile=:mobile", Client.class);
        query.setParameter("mobile", mobilePhone);
        return query.getResultList();
    }


    public Contragent getClientOrgDefaultSupplier(Client client) {
        client = entityManager.merge(client);
        Contragent ca = client.getOrg().getDefaultSupplier();
        ca.getContragentName(); //lazy load
        return ca;
    }


    public ReportInfo registerReport(String ruleName, int documentFormat, String reportName, Date createdDate,
            Long generationTime, Date startDate, Date endDate, String reportFile, String orgNum, Long idOfOrg,
            String tag, Long idOfContragentReceiver, String contragentReceiver, Long idOfContragent, String contragent) {
        ReportInfo ri = new ReportInfo(ruleName, documentFormat, reportName, createdDate, generationTime, startDate,
                endDate, reportFile, orgNum, idOfOrg, tag,idOfContragent, contragent, idOfContragentReceiver,
                contragentReceiver);
        entityManager.persist(ri);
        return ri;
    }


    public List<String> getReportHandleRuleNames() {
        TypedQuery<String> query = entityManager
                .createQuery("select ruleName from ReportHandleRule order by ruleName", String.class);
        return query.getResultList();
    }


    public void updateLastSuccessfulBalanceSync(long idOfOrg) {
        Query q = entityManager.createQuery("update Org set lastSuccessfulBalanceSync=:date where idOfOrg=:idOfOrg");
        q.setParameter("date", new Date());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }


    public void updateLastUnsuccessfulBalanceSync(long idOfOrg) {
        Query q = entityManager.createQuery("update Org set lastUnSuccessfulBalanceSync=:date where idOfOrg=:idOfOrg");
        q.setParameter("date", new Date());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }


    public List<Org> getOrderedSynchOrgsList() {
        return getOrderedSynchOrgsList(false);
    }


    public List<Org> getOrderedSynchOrgsList(boolean excludeDisabled) {
        String disabledClause = "";
        if (excludeDisabled) {
            disabledClause = " where state<>0 ";
        }
        TypedQuery<Org> query = entityManager.createQuery("from Org " + disabledClause + " order by lastSuccessfulBalanceSync", Org.class);
        return query.getResultList();
    }

    public long getStatClientsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientsWithMobile() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE mobile IS NOT NULL AND mobile<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientsWithEmail() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE email IS NOT NULL AND email<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatUniqueClientsWithPaymentTransaction() {
        Query q = entityManager.createNativeQuery("select count(distinct idofclient) from cf_clientpayments cp, cf_transactions t where cp.idoftransaction=t.idoftransaction");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatUniqueClientsWithEnterEvent() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(DISTINCT idOfClient) FROM cf_enterevents");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientPaymentsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clientpayments");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatOrdersCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_orders");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getEnterEventsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_enterevents");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getClientSmsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clientsms");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsWithPurchaseOfFoodPayPurchaseReducedPriceMeals() {
        Query q = entityManager.createNativeQuery("select count(distinct idofclient) from cf_orders o, cf_orderdetails od where o.idoforder=od.idoforder and o.idoforg=od.idoforg ");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsWithPurchaseOfMealBenefits() {
        Query q = entityManager.createNativeQuery("select count(distinct idofclient) from cf_orders o, cf_orderdetails od where o.idoforder=od.idoforder and o.idoforg=od.idoforg and od.menutype>50");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsPayPowerPurchase() {
        Query q = entityManager.createNativeQuery("select count(distinct idofclient) from cf_orders o, cf_orderdetails od where o.idoforder=od.idoforder and o.idoforg=od.idoforg and od.menutype=0");
        return Long.parseLong("" + q.getSingleResult());
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getStatPaymentsByContragents(Date fromDate, Date toDate) {
        Query q = entityManager.createNativeQuery(
                "SELECT contragentName, paymentMethod, AVG(paysum), SUM(paysum), COUNT(*) FROM cf_clientpayments cp JOIN cf_contragents cc ON cp.idOfContragent=cc.idOfContragent WHERE cp.createdDate>=:fromDate AND cp.createdDate<=:toDate GROUP BY contragentName, paymentMethod ORDER BY contragentName, paymentMethod");
        q.setParameter("fromDate", fromDate.getTime());
        q.setParameter("toDate", toDate.getTime());
        return (List<Object[]>) q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getMonitoringPayLastTransactionStats() {
        Query q = entityManager.createNativeQuery(
                "SELECT cp.idOfContragent, cc.contragentName, MAX(cp.createddate) FROM cf_clientpayments cp, cf_contragents cc WHERE cp.idOfContragent=cc.idOfContragent AND cc.classid=1 GROUP BY cp.idOfContragent, cc.contragentName");
        return (List<Object[]>) q.getResultList();
    }



    @SuppressWarnings("unchecked")
    public List<Object[]> getMonitoringPayDayTransactionsStats(Date fromDate, Date toDate) {
        Query q = entityManager.createNativeQuery(
                "SELECT cp.idOfContragent, cc.contragentName, COUNT(*) FROM cf_clientpayments cp, cf_contragents cc WHERE cp.idOfContragent=cc.idOfContragent AND cc.classid=1 AND cp.createdDate>=:fromDate AND cp.createdDate<:toDate GROUP BY cp.idOfContragent, cc.contragentName");
        q.setParameter("fromDate", fromDate.getTime());
        q.setParameter("toDate", toDate.getTime());
        return (List<Object[]>) q.getResultList();
    }


    public boolean setCardStatus(long idOfCard, int state, String reason) {
        Query q = entityManager.createNativeQuery("UPDATE cf_cards SET state=:state, lockreason=:reason WHERE idofCard=:idOfCard");
        q.setParameter("state", state);
        q.setParameter("reason", reason);
        q.setParameter("idOfCard", idOfCard);
        return q.executeUpdate() > 0;
    }

    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getOrgEntersCountByGroupType(Date at, Date to, int groupType) {
        String sql = "";
        if (groupType == GROUP_TYPE_STUDENTS) {
            sql = "SELECT cf_enterevents.idoforg, COUNT(cf_enterevents.idofclient) " +
                    "FROM cf_enterevents " +
                    "LEFT JOIN cf_clients ON cf_enterevents.idoforg=cf_clients.idoforg AND cf_enterevents.idofclient=cf_clients.idofclient "
                    +
                    "WHERE cf_enterevents.evtdatetime BETWEEN :dateAt AND :dateTo AND cf_clients.idOfClientGroup<:studentsMaxValue "
                    +
                    "GROUP BY cf_enterevents.idoforg";
        } else {
            sql = "SELECT cf_enterevents.idoforg, COUNT(cf_enterevents.idofclient) " +
                    "FROM cf_enterevents " +
                    "LEFT JOIN cf_clients ON cf_enterevents.idoforg=cf_clients.idoforg AND cf_enterevents.idofclient=cf_clients.idofclient "
                    +
                    "WHERE cf_enterevents.evtdatetime BETWEEN :dateAt AND :dateTo AND cf_clients.idOfClientGroup>=:nonStudentGroups AND cf_clients.idOfClientGroup<:leavingClientGroup "
                    +
                    "GROUP BY cf_enterevents.idoforg";
        }

        try {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = entityManager.createNativeQuery(sql);
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
    public Map<Long, Integer> getOrgOrdersCountByGroupType(Date at, Date to, int groupType, boolean notDiscounted) {
        String sql = "";
        if (groupType == GROUP_TYPE_STUDENTS) {
            sql = "SELECT cf_orders.idoforg, COUNT(distinct cf_orders.idofclient) " +
                    "FROM cf_orders " +
                    "LEFT JOIN cf_clients ON cf_orders.idofclient=cf_clients.idofclient " +
                    "WHERE cf_orders.createddate BETWEEN :dateAt AND :dateTo AND cf_clients.idOfClientGroup<:studentsMaxValue "
                    + (notDiscounted ? " AND cf_orders.socdiscount=0" : "") +
                    "GROUP BY cf_orders.idoforg";
        } else {
            sql = "SELECT cf_orders.idoforg, COUNT(distinct cf_orders.idofclient) " +
                    "FROM cf_orders " +
                    "LEFT JOIN cf_clients ON cf_orders.idofclient=cf_clients.idofclient " +
                    "WHERE cf_orders.createddate BETWEEN :dateAt AND :dateTo AND cf_clients.idOfClientGroup>=:nonStudentGroups AND cf_clients.idOfClientGroup<:leavingClientGroup "
                    + (notDiscounted ? " AND cf_orders.socdiscount=0" : "") +
                    "GROUP BY cf_orders.idoforg";
        }

        try {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = entityManager.createNativeQuery(sql);
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
    public Map<Long, Integer> getProposalOrgDiscounsCountByGroupType(Date at, Date to, int groupType) {
        String sql = "";
        if (groupType == DAOService.GROUP_TYPE_STUDENTS) {
            sql = "SELECT idoforg, COUNT(DISTINCT cf_clientscomplexdiscounts.idofclient) " +
                    "FROM cf_clients " +
                    "LEFT JOIN cf_clientscomplexdiscounts ON cf_clients.idofclient=cf_clientscomplexdiscounts.idofclient "
                    +
                    "WHERE cf_clients.idOfClientGroup<:studentsMaxValue " +
                    //"where createdate between :dateAt and :dateTo and cf_clients.idOfClientGroup<:studentsMaxValue " +
                    "GROUP BY idoforg " +
                    "HAVING COUNT(cf_clientscomplexdiscounts.idofclient)<>0";
        } else {
            sql = "SELECT idoforg, COUNT(DISTINCT cf_clientscomplexdiscounts.idofclient) " +
                    "FROM cf_clients " +
                    "LEFT JOIN cf_clientscomplexdiscounts ON cf_clients.idofclient=cf_clientscomplexdiscounts.idofclient "
                    +
                    "WHERE cf_clients.idOfClientGroup>=:nonStudentGroups AND cf_clients.idOfClientGroup<:leavingClientGroup "
                    +
                    //"where createdate between :dateAt and :dateTo and cf_clients.idOfClientGroup>=:nonStudentGroups and cf_clients.idOfClientGroup<:leavingClientGroup " +
                    "GROUP BY idoforg " +
                    "HAVING COUNT(cf_clientscomplexdiscounts.idofclient)<>0";
        }
        try {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = entityManager.createNativeQuery(sql);
            /*q.setParameter("dateAt", at.getTime());
            q.setParameter("dateTo", to.getTime());*/
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
    public Map<Long, Integer> getOrgUniqueOrdersCountByGroupType(Date at, Date to, int groupType) {
        String sql = "";
        if (groupType == DAOService.GROUP_TYPE_STUDENTS) {
            sql = "SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idofclient) " +
                    "FROM cf_orders " +
                    "LEFT JOIN cf_clients ON cf_clients.idofclient=cf_orders.idofclient " +
                    "WHERE createddate BETWEEN :dateAt AND :dateTo AND " +
                    "cf_clients.idOfClientGroup<:studentsMaxValue AND cf_orders.socdiscount<>0 " +
                    "GROUP BY cf_orders.idoforg";
        } else {
            sql = "SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idofclient) " +
                    "FROM cf_orders " +
                    "LEFT JOIN cf_clients ON cf_clients.idofclient=cf_orders.idofclient " +
                    "WHERE createddate BETWEEN :dateAt AND :dateTo AND cf_orders.socdiscount<>0 AND " +
                    "cf_clients.idOfClientGroup>=:nonStudentGroups AND cf_clients.idOfClientGroup<:leavingClientGroup "
                    +
                    "GROUP BY cf_orders.idoforg";
        }

        try {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = entityManager.createNativeQuery(sql);
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
            Query q = entityManager.createNativeQuery("SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idoforder) " +
                    "FROM cf_orders " +
                    "WHERE cf_orders.createddate BETWEEN :dateAt AND :dateTo " +
                    "GROUP BY cf_orders.idoforg");
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


    @SuppressWarnings("unchecked")
    
    public boolean bindClientToGroup(long idofclient, long idofclientgroup) {
        if (idofclient < 0) {
            return false;
        }

        try {
            Query q = entityManager.createNativeQuery(
                    "UPDATE cf_clients SET cf_clients.idofclientgroup=:idofclientgroup WHERE cf_clients.idofclient=:idofclient");
            q.setParameter("idofclient", idofclient);
            q.setParameter("idofclientgroup", idofclientgroup);
            return q.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Failed to rebind client " + idofclient + " to group " + idofclientgroup, e);
        }
        return false;
    }

    
    public List<Contragent> getContragentsList() {
        TypedQuery<Contragent> query = entityManager.createQuery("from Contragent", Contragent.class);
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    public ReportInfo getReportInfo(Long idOfOrg, Date startDate, Date endDate, String reportName) {
        String sql = "from ReportInfo where idOfOrg=:idOfOrg and startDate=:startDate and endDate=:endDate and reportFile like '%"+reportName+"%'";
        TypedQuery<ReportInfo> query = entityManager.createQuery(sql, ReportInfo.class);
        query.setParameter("idOfOrg",idOfOrg);
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        List<ReportInfo> reportInfoList = query.getResultList();
        if (reportInfoList.isEmpty()){
            return null;
        } else {
            return reportInfoList.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    
    public Contragent getContragentByBIC(String bic) {
        TypedQuery<Contragent> query = entityManager.createQuery("from Contragent where bic=:bic and classId=:classId", Contragent.class);
        query.setParameter("bic", bic);
        query.setParameter("classId", Contragent.PAY_AGENT);
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }


    public Contragent getRNIPContragent () {
        TypedQuery<Contragent> query = entityManager.createQuery("from Contragent where remarks=:remarks", Contragent.class);
        query.setParameter("remarks", "RNIP_DEFAULT");
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }


    public Org getOrgByGuid (String guid) {
        javax.persistence.Query q = entityManager.createQuery("from Org where guid=:guid");
        q.setParameter("guid", guid);
        List l = q.getResultList();
        if (l.size()==0) return null;
        return ((Org)l.get(0));
    }


    public Client getClientByGuid (String guid) {
        return DAOUtils.findClientByGuid(entityManager, guid);
    }

    
    public ReportHandleRule getReportHandleRule (long idOfReportHandleRule) {
        try {
            Session session = (Session) entityManager.getDelegate();
            Criteria criteria = session.createCriteria(ReportHandleRule.class);
            criteria.add(Restrictions.eq("idOfReportHandleRule", idOfReportHandleRule));
            ReportHandleRule handleRule = (ReportHandleRule) criteria.uniqueResult();
            return handleRule;
        } catch (Exception e) {
            return null;
        }
    }

    
    public String getReportHandlerType (long idOfReportHandleRule) {
         try {
             Session session = (Session) entityManager.getDelegate();
             Criteria criteria = session.createCriteria(ReportHandleRule.class);
             criteria.add(Restrictions.eq("idOfReportHandleRule", idOfReportHandleRule));
             ReportHandleRule handleRule = (ReportHandleRule) criteria.uniqueResult();
             String reportType = handleRule.findType(session);
             return reportType;
         } catch (Exception e) {
             return "";
         }
    }

    
    public List <ReportHandleRule> getReportHandlerRules (boolean manualAllowed) {
        try {
            Criteria reportRulesCriteria = ReportHandleRule.createAllReportRulesCriteria(manualAllowed ,(Session) entityManager
                    .getDelegate());
            List <ReportHandleRule> rules = reportRulesCriteria.list();
            return rules;
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    
    public List<RuleCondition> getReportHandlerRules (Long ruleId) {
        TypedQuery<RuleCondition> query = entityManager.createQuery("from RuleCondition where idOfRuleCondition=:handler", RuleCondition.class);
        query.setParameter("handler",ruleId);
        List<RuleCondition> result = query.getResultList();
        return result;
    }

    
    public Contragent getContragentById (Long idOfContragent) throws Exception {
        return DAOUtils.findContragent ((Session) entityManager.getDelegate(), idOfContragent);
    }

    
    public Contract getContractById (Long idOfContract) throws Exception {
        return DAOUtils.findContract ((Session) entityManager.getDelegate(), idOfContract);
    }

    
    public String getContractNameById (Long idOfContract) throws Exception {
        Contract contract = DAOUtils.findContract ((Session) entityManager.getDelegate(), idOfContract);
        return contract.getContractNumber();
    }

    @Transactional(readOnly = true)
    public List<ConfigurationProvider> findConfigurationProvidersList() {
        return entityManager.createQuery("from ConfigurationProvider order by id",ConfigurationProvider.class).getResultList();
    }

    @Transactional(readOnly = true)
    public List<ConfigurationProvider> findConfigurationProvidersList(String filter) {
        return entityManager.createQuery("from ConfigurationProvider where UPPER(name) like '%"+filter.toUpperCase()+"%' order by id",ConfigurationProvider.class).getResultList();
    }

    
    public void persistConfigurationProvider(ConfigurationProvider currentConfigurationProvider,
            List<Long> idOfOrgList) throws Exception{
        entityManager.persist(currentConfigurationProvider);
        if(!idOfOrgList.isEmpty()){
            for (Long idOfOrg: idOfOrgList){
                //daoService.setConfigurationProviderInOrg(idOfOrg,currentConfigurationProvider);
                Org org = entityManager.find(Org.class, idOfOrg);
                if (org != null) {
                    org.setConfigurationProvider(currentConfigurationProvider);
                    entityManager.persist(org);
                }
            }
        }
    }

    public List<GoodGroup> findGoodGroupBySuplifier(Boolean deletedStatusSelected) {
        TypedQuery<GoodGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from GoodGroup order by globalId",GoodGroup.class);
        } else {
            query = entityManager.createQuery("from GoodGroup where deletedState=false order by globalId",GoodGroup.class);
        }
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<GoodGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from GoodGroup where orgOwner in :orgOwners order by globalId",GoodGroup.class);
        } else {
            query = entityManager.createQuery("from GoodGroup where orgOwner in :orgOwners and deletedState=false order by globalId",GoodGroup.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(String filter) {
        TypedQuery<GoodGroup> query = entityManager.createQuery("from GoodGroup where UPPER(NameOfGoodsGroup) like '%"+filter.toUpperCase()+"%' and deletedState=false order by globalId", GoodGroup.class);
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(List<Long> orgOwners, String filter) {
        TypedQuery<GoodGroup> query = entityManager.createQuery("from GoodGroup where UPPER(NameOfGoodsGroup) like '%"+filter.toUpperCase()+"%' and orgOwner in :orgOwners and deletedState=false  order by globalId", GoodGroup.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<Good> findGoodsByGoodGroup(GoodGroup goodGroup, Boolean deletedStatusSelected){
        TypedQuery<Good> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from Good where goodGroup=:goodGroup order by globalId",Good.class);
        } else {
            query = entityManager.createQuery("from Good where goodGroup=:goodGroup and deletedState=false order by globalId",Good.class);
        }
        query.setParameter("goodGroup",goodGroup);
        return query.getResultList();
    }


    public List<Good> findGoodsByGoodGroup(GoodGroup goodGroup, List<Long> orgOwners, Boolean deletedStatusSelected){
        TypedQuery<Good> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from Good where goodGroup=:goodGroup and orgOwner in :orgOwner order by globalId",Good.class);
        } else {
            query = entityManager.createQuery("from Good where goodGroup=:goodGroup and orgOwner in :orgOwner and deletedState=false order by globalId",Good.class);
        }
        query.setParameter("goodGroup",goodGroup);
        query.setParameter("orgOwner", orgOwners);
        return query.getResultList();
    }


    @SuppressWarnings("unchecked")
    public List<Good> findGoods(ConfigurationProvider configurationProvider, GoodGroup goodGroup,  List<Long> orgOwners, Boolean deletedStatusSelected){
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(Good.class);
        if(goodGroup!=null) criteria.add(Restrictions.eq("goodGroup",goodGroup));
        if(orgOwners!=null && !orgOwners.isEmpty()) criteria.add(Restrictions.in("orgOwner",orgOwners));
        if(configurationProvider!=null)
            criteria.add(Restrictions.eq("idOfConfigurationProvider",configurationProvider.getIdOfConfigurationProvider()));
        if(deletedStatusSelected!=null && !deletedStatusSelected) criteria.add(Restrictions.eq("deletedState",false));
        //if(StringUtils.isNotEmpty(nameOfGoodsGroup))
        //    criteria.add(Restrictions.ilike("nameOfGoodsGroup",nameOfGoodsGroup, MatchMode.ANYWHERE));
        return criteria.list();
    }


    public List<GoodsBasicBasket> findGoodsBasicBasket() {
        TypedQuery<GoodsBasicBasket> query = entityManager.createQuery("from GoodsBasicBasket order by idOfBasicGood", GoodsBasicBasket.class);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(Long idOfConfigurationProvider,
            Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",ProductGroup.class);
        } else {
            query = entityManager.createQuery("from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",ProductGroup.class);
        }
        query.setParameter("idOfConfigurationProvider",idOfConfigurationProvider);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",ProductGroup.class);
        } else {
            query = entityManager.createQuery("from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",ProductGroup.class);
        }
        query.setParameter("idOfConfigurationProvider",idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from ProductGroup where orgOwner in :orgOwners order by globalId",ProductGroup.class);
        } else {
            query = entityManager.createQuery("from ProductGroup where orgOwner in :orgOwners and deletedState=false order by globalId",ProductGroup.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(String filter) {
        TypedQuery<ProductGroup> query = entityManager.createQuery("from ProductGroup where UPPER(nameOfGroup) like '%"+filter.toUpperCase()+"%' and deletedState=false order by globalId",ProductGroup.class);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from ProductGroup order by globalId",ProductGroup.class);
        } else {
            query = entityManager.createQuery("from ProductGroup where deletedState=false order by globalId",ProductGroup.class);
        }
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(List<Long> orgOwners, String filter) {
        TypedQuery<ProductGroup> query = entityManager.createQuery("from ProductGroup where UPPER(nameOfGroup) like '%"+filter.toUpperCase()+"%' and orgOwner in :orgOwners and deletedState=false order by globalId",ProductGroup.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> findProductByConfigurationProvider(ProductGroup pg, Long confProviderId, Boolean deleteSt, List<Long> orgOwners,
            String productName) {
        Session session = (Session) entityManager.getDelegate();
        Criteria cr = session.createCriteria(Product.class);
        Conjunction conj = Restrictions.conjunction();
        if (pg != null)
            conj.add(Restrictions.eq("productGroup", pg));
        if (confProviderId != null)
            conj.add(Restrictions.eq("idOfConfigurationProvider", confProviderId));
        if (deleteSt != null && !deleteSt)
            conj.add(Restrictions.eq("deletedState", false));
        if (orgOwners != null && !orgOwners.isEmpty())
            conj.add(Restrictions.in("orgOwner", orgOwners));
        if (productName != null && !productName.isEmpty())
            conj.add(Restrictions.ilike("productName", productName, MatchMode.ANYWHERE));
        cr.add(conj).addOrder(Order.asc("globalId"));
        return (List<Product>) cr.list();
    }

    public List<Product> findProductByConfigurationProvider(List<Long> orgOwners, String productName) {
        return findProductByConfigurationProvider(orgOwners, false, productName);
    }

    public List<Product> findProductByConfigurationProvider(List<Long> orgOwners, Boolean deletedStatusSelected, String productName) {
        return findProductByConfigurationProvider(null, null, deletedStatusSelected, orgOwners, productName);
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(Long idOfConfigurationProvider, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",TechnologicalMapGroup.class);
        }
        query.setParameter("idOfConfigurationProvider",idOfConfigurationProvider);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",TechnologicalMapGroup.class);
        }
        query.setParameter("idOfConfigurationProvider",idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMapGroup order by globalId",TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMapGroup where deletedState=false order by globalId",TechnologicalMapGroup.class);
        }
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMapGroup where orgOwner in :orgOwners order by globalId",TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMapGroup where orgOwner in :orgOwners and deletedState=false order by globalId",TechnologicalMapGroup.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(String filter) {
        TypedQuery<TechnologicalMapGroup> query = entityManager.createQuery("from TechnologicalMapGroup where UPPER(nameOfGroup) like '%"+filter.toUpperCase()+"%' and deletedState=false order by globalId",TechnologicalMapGroup.class);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(List<Long> orgOwners,
            String filter) {
        TypedQuery<TechnologicalMapGroup> query = entityManager.createQuery("from TechnologicalMapGroup where UPPER(nameOfGroup) like '%"+filter.toUpperCase()+"%' and orgOwner in :orgOwners and deletedState=false order by globalId",TechnologicalMapGroup.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<Product> findProduct(ProductGroup productGroup, ConfigurationProvider provider, String filter, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findProduct(session, productGroup, provider, filter, orgOwners, deletedStatusSelected);
    }


    public List<GoodGroup> findGoodGroup(ConfigurationProvider provider, String filter, List<Long> orgOwners, Boolean deletedStatusSelected) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findGoodGroup(session, provider, filter, orgOwners, deletedStatusSelected);
    }

    public Long countProductsByProductGroup(ProductGroup currentProductGroup) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.countProductByProductGroup(session, currentProductGroup);
    }

    public List<Product> findProductByProductGroup(ProductGroup currentProductGroup) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findProductByProductGroup(session, currentProductGroup);
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMap order by globalId",TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where deletedState=false order by globalId",TechnologicalMap.class);
        }
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(Long idOfConfigurationProvider, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider",idOfConfigurationProvider);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider",idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMap where orgOwner in :orgOwners order by globalId",TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where orgOwner in :orgOwners and deletedState=false order by globalId",TechnologicalMap.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(TechnologicalMapGroup technologicalMapGroup, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup order by globalId",TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and deletedState=false order by globalId",TechnologicalMap.class);
        }
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(TechnologicalMapGroup technologicalMapGroup, Long idOfConfigurationProvider, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider",idOfConfigurationProvider);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(TechnologicalMapGroup technologicalMapGroup, Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider",idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(TechnologicalMapGroup technologicalMapGroup, List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and orgOwner in :orgOwners order by globalId",TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and orgOwner in :orgOwners and deletedState=false order by globalId",TechnologicalMap.class);
        }
        query.setParameter("orgOwners", orgOwners);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }


    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(String filter) {
        TypedQuery<TechnologicalMap> query = entityManager.createQuery("from TechnologicalMap where UPPER(nameOfTechnologicalMap) like '%"+filter.toUpperCase()+"%' and deletedState=false order by globalId",TechnologicalMap.class);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(List<Long> orgOwners, String filter) {
        TypedQuery<TechnologicalMap> query = entityManager.createQuery("from TechnologicalMap where UPPER(nameOfTechnologicalMap) like '%"+filter.toUpperCase()+"%' and orgOwner in :orgOwners and deletedState=false order by globalId",TechnologicalMap.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }


    public boolean updateBasicGood(Long idOfBasicGood, String nameOfGood, UnitScale unitsScale, Long netWeight) {
        Query query = entityManager.createQuery("update GoodsBasicBasket set nameOfGood=:nameOfGood, unitsScale=:unitsScale, netWeight=:netWeight, lastUpdate=:lastUpdate where idOfBasicGood=:idOfBasicGood");
        query.setParameter("idOfBasicGood",idOfBasicGood);
        query.setParameter("nameOfGood",nameOfGood);
        query.setParameter("unitsScale",unitsScale);
        query.setParameter("netWeight",netWeight);
        query.setParameter("lastUpdate",new Date());
        return query.executeUpdate()!=0;
    }


    @Transactional
    public List<Client> getClientsByOrgId (long idOfOrg) {
        TypedQuery<Client> query = entityManager.createQuery("from Client where IdOfOrg=:idoforg", Client.class);
        query.setParameter("idoforg",idOfOrg);
        List <Client> clients = query.getResultList();
        for (Client cl : clients) {
            try {
                cl.getPerson().getFirstName();
                cl.getOrg().getOfficialName();
                cl.getClientGroup().getGroupName();
            } catch (Exception e) {

            }
        }
        return clients;
    }

    public void applyFullSyncOperationByOrgList(List<Long> idOfOrgList) throws Exception{
        Query query = entityManager.createQuery("update Org set fullSyncParam=1 where idOfOrg in :idOfOrgList");
        query.setParameter("idOfOrgList", idOfOrgList);
        query.executeUpdate();
    }

    public List<ComplexRole> findComplexRoles() {
        return entityManager.createQuery("from ComplexRole order by idOfRole",ComplexRole.class).getResultList();
    }

    public List<ComplexRole> updateComplexRoles(List<ComplexRole> complexRoles) throws Exception{
        List<ComplexRole> roles = new ArrayList<ComplexRole>(complexRoles.size());
        for (ComplexRole complexRole: complexRoles){
            ComplexRole role = entityManager.find(ComplexRole.class, complexRole.getIdOfRole());
            String roleName = complexRole.getRoleName();
            if(StringUtils.isEmpty(roleName) || StringUtils.isEmpty(roleName.trim())){
                role.setRoleName(String.format("Комплекс %d", complexRole.getIdOfRole()));
            } else {
                role.setRoleName(roleName);
            }
            role.setExtendRoleName(complexRole.getExtendRoleName());
            entityManager.persist(role);
            roles.add(role);
        }
        return roles;
    }

    public List<CategoryDiscount> getCategoryDiscountListWithIds(List<Long> idOfCategoryList) {
        TypedQuery<CategoryDiscount> q = entityManager.createQuery("from CategoryDiscount where idOfCategoryDiscount in (:idOfCategoryList)", CategoryDiscount.class);
        q.setParameter("idOfCategoryList", idOfCategoryList);
        return q.getResultList();
    }

    public List<CategoryOrg> getCategoryOrgWithIds(List<Long> idOfCategoryOrgList) {
        TypedQuery<CategoryOrg> q = entityManager.createQuery("from CategoryOrg where idOfCategoryOrg in (:idOfCategoryOrgList)", CategoryOrg.class);
        q.setParameter("idOfCategoryOrgList", idOfCategoryOrgList);
        return q.getResultList();
    }

    public List<Org> findOrgsByConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        TypedQuery<Org> query = entityManager.createQuery("from Org where configurationProvider=:configurationProvider",Org.class);
        query.setParameter("configurationProvider",currentConfigurationProvider);
        return query.getResultList();
    }

    public Good getGood(Long globalId) {
        return entityManager.find(Good.class, globalId);
    }

    public List<Client> findClientsForOrgAndFriendly(Long idOfOrg, boolean lazyLoadInit) throws Exception {
        List<Client> cl = DAOUtils.findClientsForOrgAndFriendly(entityManager, entityManager.find(Org.class, idOfOrg));
        if (lazyLoadInit) {
            for (Client c: cl) {
                if (c == null) {
                    continue;
                }
                Person p = c.getPerson();
                if (p != null) {
                    p.getSurname();
                }
                ClientGroup clg = c.getClientGroup();
                if (clg != null) {
                    clg.getGroupName();
                }
            }
        }
        return cl;
    }

    public Set<Org> getFriendlyOrgs(Long idOfOrg) {
        Org org = entityManager.find(Org.class, idOfOrg);
        return org.getFriendlyOrg();
    }

    public List<RegistryChange> getLastRegistryChanges(long idOfOrg, long revisionDate) throws Exception {
        if (revisionDate < 1L) {
            revisionDate = getLastRegistryChangeUpdate(idOfOrg);
}
        if (revisionDate < 1) {
            return Collections.EMPTY_LIST;
        }
        TypedQuery<RegistryChange> query = entityManager.createQuery("from RegistryChange where idOfOrg=:idOfOrg and createDate=:lastUpdate order by groupName, surname, firstName, secondName",RegistryChange.class);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("lastUpdate", revisionDate);
        return query.getResultList();
    }

    public long getLastRegistryChangeUpdate(long idOfOrg) throws Exception {
        Query q = entityManager.createNativeQuery("SELECT max(createDate) FROM cf_RegistryChange where idOfOrg=:idOfOrg");
        q.setParameter("idOfOrg", idOfOrg);
        Object res = q.getSingleResult();
        return Long.parseLong("" + (res == null || res.toString().length() < 1 ? 0 : res.toString()));
    }

    public List<Long> getRegistryChangeRevisions(long idOfOrg) throws Exception {
        TypedQuery<Long> query = entityManager.createQuery("select distinct createDate from RegistryChange where idOfOrg=:idOfOrg order by createDate desc",Long.class);
        query.setParameter("idOfOrg",idOfOrg);
        return query.getResultList();
    }
    
    public List<RegistryChangeError> getRegistryChangeErrors(long idOfOrg) throws Exception{
        String orgClause = "";
        if (idOfOrg > -1) {
            orgClause = "where idOfOrg=:idOfOrg";
        }
        TypedQuery<RegistryChangeError> query = entityManager.createQuery("from RegistryChangeError " + orgClause + " order by createDate desc",RegistryChangeError.class);
        if (idOfOrg > -1) {
            query.setParameter("idOfOrg", idOfOrg);
        }
        return query.getResultList();
    }

    public void addRegistryChangeErrorComment(long idOfRegistryChangeError, String comment, String author) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        RegistryChangeError e = entityManager.find(RegistryChangeError.class, idOfRegistryChangeError);
        e.setComment(comment);
        e.setCommentAuthor(author);
        e.setCommentCreateDate(System.currentTimeMillis());
        session.update(e);
    }

    public void addRegistryChangeError(long idOfOrg, long revisionDate, String error, String errorDetails) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        RegistryChangeError e = new RegistryChangeError();
        e.setIdOfOrg(idOfOrg);
        e.setRevisionCreateDate(revisionDate);
        e.setError(error);
        e.setCreateDate(System.currentTimeMillis());
        e.setErrorDetail(errorDetails);
        session.save(e);
    }

    public List<String> getCurrentRepositoryReportNames() {
        TypedQuery<String> query = entityManager.createQuery("select distinct ruleName from ReportInfo order by ruleName",String.class);
        return query.getResultList();

    }

    public void renameRepositoryReports(String previousName, String newName) throws Exception {
        if (previousName == null || previousName.trim().length() < 1) {
            throw new Exception("Отсутствует старое наименование отчета");
        }
        if (newName == null || newName.trim().length() < 1) {
            throw new Exception("Отсутствует новое наименование отчета");
        }
        Session session = (Session) entityManager.getDelegate();
        //  cf_reporthandlerules
        org.hibernate.Query q = session.createSQLQuery("update cf_reportinfo set rulename=:newName where rulename=:previousName");
        q.setString("newName", newName.trim());
        q.setString("previousName", previousName.trim());
        q.executeUpdate();
    }

    public List<BigInteger> getCleanupRepositoryReportsByDate() throws Exception{
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery(
                "select idofreportinfo "
                        + "from cf_reportinfo "
                        + "left join cf_reporthandlerules on cf_reportinfo.rulename=cf_reporthandlerules.rulename "
                        + "where cf_reporthandlerules.storageperiod<>-1 and "
                        + "      (cf_reporthandlerules.storageperiod=0 or "
                        + "       createddate<EXTRACT(EPOCH FROM now())*1000-cf_reporthandlerules.storageperiod)");
        List <BigInteger> list = (List <BigInteger>) q.list();
        return list;
    }

    public Contragent findContragentByClient(Long clientContractId) {
        TypedQuery<Contragent> query = entityManager.createQuery(
                "select c from Contragent c join c.orgsInternal o join o.clientsInternal cl \n"
                        + "where cl.contractId = :contractId", Contragent.class)
                .setParameter("contractId", clientContractId);
        List<Contragent> res = query.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }
}
