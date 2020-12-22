/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

@Service
@Transactional (readOnly = true)
public class ReportDAOService {

    private final static Logger logger = LoggerFactory.getLogger(ReportDAOService.class);

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public static ReportDAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(ReportDAOService.class);
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
        Query q = entityManager.createQuery("from User where userName=:userName");
        q.setParameter("userName", userName);
        return (User) q.getSingleResult();
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
        Query query1 = entityManager.createNativeQuery(
                "DELETE FROM cf_technological_map_products where idoftechnologicalmaps=" + idOfTechnologicalMaps);
        query1.executeUpdate();
        Query query = entityManager.createNativeQuery("DELETE FROM cf_technological_map where idoftechnologicalmaps="+idOfTechnologicalMaps);
        query.executeUpdate();
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


    public boolean setClientMobilePhone(Long contractId, String mobile, ClientsMobileHistory clientsMobileHistory) {
        Query q = entityManager.createQuery("update Client set mobile=:mobile where contractId=:contractId");
        q.setParameter("mobile", mobile);
        q.setParameter("contractId", contractId);
        //Сохраняем историю изменения клиента
        Client client = DAOUtils.findClientByContractId(entityManager, contractId);
        if (client != null) {
            client.initClientMobileHistory(clientsMobileHistory);
            client.setMobile(mobile);
        }
        //
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


    public Client findClientById(long idOfClient) {
        return entityManager.find(Client.class, idOfClient);
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

    public List<String> getReportHandleRuleNames() {
        TypedQuery<String> query = entityManager
                .createQuery("select ruleName from ReportHandleRule order by ruleName", String.class);
        return query.getResultList();
    }


    public void updateLastSuccessfulBalanceSync(long idOfOrg) {
        Query q = entityManager.createQuery("update OrgSync set lastSuccessfulBalanceSync=:date where idOfOrg=:idOfOrg");
        q.setParameter("date", new Date());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }


    public void updateLastUnsuccessfulBalanceSync(long idOfOrg) {
        Query q = entityManager.createQuery("update OrgSync set lastUnSuccessfulBalanceSync=:date where idOfOrg=:idOfOrg");
        q.setParameter("date", new Date());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }


    public List<Org> getOrderedSynchOrgsList() {
        TypedQuery<Org> query = entityManager.createQuery("select os.org from OrgSync os order by lastSuccessfulBalanceSync", Org.class);
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
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_orders where cf_orders.state=0");
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
        Query q = entityManager.createNativeQuery("select count(distinct idofclient) from cf_orders o, cf_orderdetails od where o.idoforder=od.idoforder and o.idoforg=od.idoforg and o.state=0 and od.state=0 ");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsWithPurchaseOfMealBenefits() {
        Query q = entityManager.createNativeQuery("select count(distinct idofclient) from cf_orders o, cf_orderdetails od where o.idoforder=od.idoforder and o.idoforg=od.idoforg and od.menutype>50 and o.state=0 and od.state=0");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsPayPowerPurchase() {
        Query q = entityManager.createNativeQuery("select count(distinct idofclient) from cf_orders o, cf_orderdetails od where o.idoforder=od.idoforder and o.idoforg=od.idoforg and od.menutype=0 and o.state=0 and od.state=0");
        return Long.parseLong("" + q.getSingleResult());
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getStatPaymentsByContragents(Date fromDate, Date toDate, Contragent contragent) {
        Query q = null;
        if (contragent != null) {
            q = entityManager.createNativeQuery(
                    "SELECT contragentName, paymentMethod, AVG(paysum), SUM(paysum), COUNT(*) FROM cf_clientpayments cp JOIN cf_contragents cc ON cp.idOfContragent=cc.idOfContragent WHERE cp.createdDate>=:fromDate AND cp.createdDate<=:toDate AND cp.idOfContragentReceiver in (:idOfContragentReceiver) GROUP BY contragentName, paymentMethod ORDER BY contragentName, paymentMethod");
            q.setParameter("fromDate", fromDate.getTime());
            q.setParameter("toDate", toDate.getTime());
            q.setParameter("idOfContragentReceiver", contragent.getIdOfContragent());
        } else {
            q = entityManager.createNativeQuery(
                    "SELECT contragentName, paymentMethod, AVG(paysum), SUM(paysum), COUNT(*) FROM cf_clientpayments cp JOIN cf_contragents cc ON cp.idOfContragent=cc.idOfContragent WHERE cp.createdDate>=:fromDate AND cp.createdDate<=:toDate GROUP BY contragentName, paymentMethod ORDER BY contragentName, paymentMethod");
            q.setParameter("fromDate", fromDate.getTime());
            q.setParameter("toDate", toDate.getTime());
        }
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
    public Map<Long, Integer> getOrgOrdersCount(Date at, Date to) {
        try {
            Map<Long, Integer> res = new HashMap<Long, Integer>();
            Query q = entityManager.createNativeQuery("SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idoforder) " +
                    "FROM cf_orders " +
                    "WHERE cf_orders.state=0 and cf_orders.createddate BETWEEN :dateAt AND :dateTo " +
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
        Query q = entityManager.createQuery("from Org where guid=:guid");
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
        TypedQuery<RuleCondition> query = entityManager.createQuery("from RuleCondition where reportHandleRule.idOfReportHandleRule=:handler", RuleCondition.class);
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
                if (org != null && !org.getConfigurationProvider().getIdOfConfigurationProvider()
                        .equals(currentConfigurationProvider.getIdOfConfigurationProvider())) {
                    org.setConfigurationProvider(currentConfigurationProvider);
                    org.setTradeAccountConfigChangeDirective(TradeAccountConfigChange.CHANGED);
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
        TypedQuery<GoodGroup> query = entityManager.createQuery("from GoodGroup where UPPER(nameOfGoodsGroup) like '%"+filter.toUpperCase()+"%' and deletedState=false order by globalId", GoodGroup.class);
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(List<Long> orgOwners, String filter) {
        TypedQuery<GoodGroup> query = entityManager.createQuery("from GoodGroup where UPPER(nameOfGoodsGroup) like '%"+filter.toUpperCase()+"%' and orgOwner in :orgOwners and deletedState=false  order by globalId", GoodGroup.class);
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


    public List<Good> findGoods(Boolean deletedStatusSelected){
        TypedQuery<Good> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from Good order by globalId",Good.class);
        } else {
            query = entityManager.createQuery("from Good where deletedState=false order by globalId",Good.class);
        }
        return query.getResultList();
    }


    public List<Good> findGoods(List<Long> orgOwners, Boolean deletedStatusSelected){
        TypedQuery<Good> query;
        if(deletedStatusSelected){
            query = entityManager.createQuery("from Good where orgOwner in :orgOwner order by globalId",Good.class);
        } else {
            query = entityManager.createQuery("from Good where orgOwner in :orgOwner and deletedState=false order by globalId",Good.class);
        }
        query.setParameter("orgOwner", orgOwners);
        return query.getResultList();
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

}
