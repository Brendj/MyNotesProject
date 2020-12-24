/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.IPreorderDAOOperations;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ExternalSystemStats;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

@Component(value = "daoService")
@Scope("singleton")
@Transactional
public class DAOService {

    private final static Logger logger = LoggerFactory.getLogger(DAOService.class);

    private IPreorderDAOOperations preorderDAOOperationsImpl;
    //public final static int GROUP_TYPE_STUDENTS = 0, GROUP_TYPE_NON_STUDENTS = 1;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public static DAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOService.class);
    }

    public void createStatTable() {
        entityManager.createNativeQuery("CREATE TABLE IF NOT EXISTS srv_clear_menu_stat\n"
                + "(\n"
                + "    idoforg BIGINT,\n"
                + "    startdate BIGINT,\n"
                + "    enddate BIGINT,\n"
                + "    datefrom BIGINT,\n"
                + "    amount integer\n"
                + ");\n"
                + "COMMENT ON TABLE srv_clear_menu_stat is 'Статистика сервиса очистки меню'")
                .executeUpdate();
    }

    public List<Long> getOrgIdsForClearMenu() {
        List<Long> result = new ArrayList<>();
        Query q = entityManager.createNativeQuery("select idOfOrg from cf_orgs o "
                + "where o.idoforg > (select coalesce(max(idoforg), 0) from srv_clear_menu_stat where automatic = true) order by o.idoforg");
        List list = q.getResultList();
        for (Object row : list) {
            Long value = ((BigInteger) row).longValue();
            result.add(value);
        }
        return result;
    }

    public List<CategoryDiscount> getCategoryDiscountList() {
        TypedQuery<CategoryDiscount> q = entityManager
                .createQuery("from CategoryDiscount order by idOfCategoryDiscount", CategoryDiscount.class);
        return q.getResultList();
    }

    public List<CategoryDiscount> getCategoryDiscountListByCategoryName(String categoryName) {
        String strQuery = "from CategoryDiscount where lower(categoryName) = lower('" + categoryName
                + "') order by idOfCategoryDiscount";
        TypedQuery<CategoryDiscount> q = entityManager.createQuery(strQuery, CategoryDiscount.class);
        return q.getResultList();
    }

    public List<CategoryDiscountDSZN> getCategoryDiscountDSZNList() {
        TypedQuery<CategoryDiscountDSZN> q = entityManager
                .createQuery("from CategoryDiscountDSZN where deleted = false order by code",
                        CategoryDiscountDSZN.class);
        return q.getResultList();
    }

    public List<Contragent> getContragentsWithClassIds(List<Integer> classIds) {
        TypedQuery<Contragent> q = entityManager
                .createQuery("from Contragent where classId in (:classIds)", Contragent.class);
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

    public User findUserById(long idOfUser) throws Exception {
        User user = entityManager.find(User.class, idOfUser);
        return user;
    }

    public User setUserInfo(User user) {
        return entityManager.merge(user);
    }

    public void writeAuthJournalRecord(SecurityJournalAuthenticate record) {
        entityManager.persist(record);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeProcessJournalRecord(SecurityJournalProcess record) {
        entityManager.persist(record);
    }

    public void writeReportJournalRecord(SecurityJournalReport record) {
        entityManager.persist(record);
    }

    public Boolean isMenuExchange(Long idOfOrg) {
        TypedQuery<Long> query = entityManager
                .createQuery("select idOfSourceOrg from MenuExchangeRule where idOfSourceOrg = :idOfSourceOrg",
                        Long.class);
        query.setParameter("idOfSourceOrg", idOfOrg);
        List<Long> list = query.getResultList();
        return !list.isEmpty();
    }

    public List<Long> getNextFreeLastClientContractId(long divider, long idOfOrg, long lastClientContractId,
            int count) {
        //Запрос правильный, не менять.
        String num = "";
        String end = "";
        if (count == 1) {
            num = "min(num)";
        } else {
            num = "num";
            end = " order by num limit " + count;
        }
        String qstr = "SELECT " + num + " FROM (SELECT num FROM generate_series(:lastClientContractId, 99999) num\n"
                + "                EXCEPT\n" + "                SELECT\n" + "        CASE\n"
                + "                WHEN contractid/:divider > 0 THEN (contractid/:divider)*10000 + (contractid%100000)/10\n"
                + "        WHEN contractid/:divider = 0 THEN (contractid%100000)/10\n" + "        END AS num\n"
                + "        FROM cf_clients WHERE (contractid/:divider > 0 AND (contractid%:divider)/100000=:idoforg) "
                + "OR (contractid/:divider = 0 AND contractid/100000=:idoforg)) AS list" + end;
        Query nativeQuery = entityManager.createNativeQuery(qstr);
        nativeQuery.setParameter("lastClientContractId", lastClientContractId);
        nativeQuery.setParameter("divider", divider);
        nativeQuery.setParameter("idoforg", idOfOrg);
        List res = nativeQuery.getResultList();
        List<Long> result = new ArrayList<Long>();
        if (res != null) {
            for (Object o : res) {
                Long value = ((BigInteger) o).longValue();
                result.add(value);
            }
        }
        return result;
    }

    public void saveOrgPreContractIds(long idOfOrg, List<Long> list) {
        String sql = "insert into cf_orgs_precontract_ids(idOfOrg, contractId, createddate) select :idOfOrg, contractid, :date from unnest(array[%s]) contractid";
        String contractIds = "";
        for (Long contractId : list) {
            contractIds += contractId + ",";
        }
        contractIds = contractIds.substring(0, contractIds.length()-1);
        sql = String.format(sql, contractIds);
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("date", (new Date()).getTime());
        query.executeUpdate();
    }

    public List<Long> getOrgPreContractIdList(long idOfOrg, int count) {
        Query query = entityManager.createQuery("select opc.contractId from OrgPreContractId opc where opc.idOfOrg = :idOfOrg and opc.used = false order by opc.contractId");
        query.setParameter("idOfOrg", idOfOrg);
        query.setMaxResults(count);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<ECafeSettings> geteCafeSettingses(final Long idOfOrg, final SettingsIds settingsIds,
            final Boolean deleted) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ECafeSettings.class);
        if (idOfOrg == null && settingsIds == null) {
            return new ArrayList<ECafeSettings>(0);
        }
        if (idOfOrg != null) {
            criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        }
        if (settingsIds != null) {
            criteria.add(Restrictions.eq("settingsId", settingsIds));
        }
        if (!deleted) {
            criteria.add(Restrictions.eq("deletedState", false));
        }
        return (List<ECafeSettings>) criteria.list();
    }

    public List<ECafeSettings> geteCafeSettingsesWithoutFiveElm(final Long idOfOrg, final SettingsIds settingsIds,
            final Boolean deleted) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ECafeSettings.class);
        if (idOfOrg == null && settingsIds == null) {
            return Collections.emptyList();
        }
        if (idOfOrg != null) {
            criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        }
        if (settingsIds != null) {
            criteria.add(Restrictions.eq("settingsId", settingsIds));
        } else {
            criteria.add(Restrictions.ne("settingsId", SettingsIds.fromInteger(5)));
        }
        if (!deleted) {
            criteria.add(Restrictions.eq("deletedState", false));
        }
        return (List<ECafeSettings>) criteria.list();
    }

    public ConfigurationProvider onSave(ConfigurationProvider configurationProvider, User currentUser,
            List<Long> idOfOrgList) throws Exception {
        ConfigurationProvider cp = entityManager
                .find(ConfigurationProvider.class, configurationProvider.getIdOfConfigurationProvider());
        cp.setName(configurationProvider.getName());
        cp.setLastUpdate(new Date());
        cp.setUserEdit(currentUser);
        if (!cp.getOrgs().isEmpty()) {
            for (Org org : cp.getOrgs()) {
                org = entityManager.merge(org);
                org.setConfigurationProvider(null);
                org.setTradeAccountConfigChangeDirective(TradeAccountConfigChange.CHANGED);
                org = entityManager.merge(org);
            }
        }
        cp.getOrgs().clear();
        configurationProvider = entityManager.merge(cp);
        if (!idOfOrgList.isEmpty()) {
            for (Long idOfOrg : idOfOrgList) {
                Org org = entityManager.find(Org.class, idOfOrg);
                if (org != null && !org.getConfigurationProvider().getIdOfConfigurationProvider()
                        .equals(configurationProvider.getIdOfConfigurationProvider())) {
                    org.setConfigurationProvider(configurationProvider);
                    org.setTradeAccountConfigChangeDirective(TradeAccountConfigChange.CHANGED);
                    entityManager.persist(org);
                }
            }
        }
        return configurationProvider;
    }

    public Contragent getContragentByName(String name) {
        TypedQuery<Contragent> query = entityManager
                .createQuery("from Contragent where contragentName=:name", Contragent.class);
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
            Query query = entityManager.createQuery(
                    "update Client set clientGUID=:uuid, clientRegistryVersion=:version where idOfClient=:idOfClient");
            query.setParameter("idOfClient", id);
            query.setParameter("version", clientRegistryVersion);
            query.setParameter("uuid", UUID.randomUUID().toString());
            int result = query.executeUpdate();
            if (result == 0) {
                logger.error("Error edit uuid by client: idOfClient=" + id);
            }
        }

    }

    public void setDeletedState(DistributedObject distributedObject) {
        distributedObject = entityManager.find(distributedObject.getClass(), distributedObject.getGlobalId());
        distributedObject.setDeletedState(true);
        entityManager.persist(distributedObject);
    }

    public <T> T findDistributedObjectByRefGUID(Class<T> clazz, String guid) {
        TypedQuery<T> query = entityManager
                .createQuery("from " + clazz.getSimpleName() + " where guid='" + guid + "'", clazz);
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

    public void removeTechnologicalMap(Long idOfTechnologicalMaps) {
        Query query1 = entityManager.createNativeQuery(
                "DELETE FROM cf_technological_map_products where idoftechnologicalmaps=" + idOfTechnologicalMaps);
        query1.executeUpdate();
        Query query = entityManager.createNativeQuery(
                "DELETE FROM cf_technological_map where idoftechnologicalmaps=" + idOfTechnologicalMaps);
        query.executeUpdate();
    }

    public void removeGoodGroup(GoodGroup goodGroup) {
        GoodGroup group = entityManager.merge(goodGroup);
        entityManager.remove(group);
    }

    public void removeSetting(ECafeSettings eCafeSettings) {
        ECafeSettings settings = entityManager.merge(eCafeSettings);
        entityManager.remove(settings);
    }

    public void removeGood(Good good) {
        Good g = entityManager.merge(good);
        entityManager.remove(g);
    }

    public void removeProduct(Product product) {
        Product p = entityManager.merge(product);
        entityManager.remove(p);
    }

    public Boolean isEmptyOrgConfigurationProvider(ConfigurationProvider configurationProvider) {
        ConfigurationProvider cp = entityManager.merge(configurationProvider);
        return cp.getOrgEmpty();
    }

    public void removeConfigurationProvider(ConfigurationProvider configurationProvider) throws Exception {
        ConfigurationProvider cp = entityManager.merge(configurationProvider);
        entityManager.remove(cp);
    }

    // не рекомендуется к использованию следует переписать
    public DistributedObject mergeDistributedObject(DistributedObject distributedObject, Long globalVersion) {
        TypedQuery<DistributedObject> query = entityManager.createQuery(
                "from " + distributedObject.getClass().getSimpleName() + " where guid='" + distributedObject.getGuid()
                        + "'", DistributedObject.class);
        List<DistributedObject> distributedObjectList = query.getResultList();
        if (distributedObjectList.isEmpty()) {
            return null;
        }
        DistributedObject d = entityManager
                .find(distributedObject.getClass(), distributedObjectList.get(0).getGlobalId());
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

    public void mergeEntity(Object entity) throws Exception {
        entityManager.merge(entity);
    }

    public Object detachEntity(Object entity) throws Exception {
        entityManager.detach(entity);
        return entity;
    }

    public Long getContractIdByCardNo(long lCardId) throws Exception {
        Client client = DAOUtils.findClientByCardNo(entityManager, lCardId);
        if (client != null) {
            return client.getContractId();
        }
        return null;
    }

    public Long getContractIdByTempCardNoAndCheckValidDate(long lCardId, int days) throws Exception {
        /* так как в поле хранится дата на 00:00 ночи текущего дня вычтем из текущего дня 24 часа в милисекудах */
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(CardTemp.class);
        criteria.createAlias("client", "cl");
        criteria.add(Restrictions.eq("cardNo", lCardId));
        if (days > 0) {
            Date currentDay = CalendarUtils.truncateToDayOfMonth(new Date());
            currentDay = CalendarUtils.addDays(currentDay, -days);
            criteria.add(Restrictions.ge("validDate", currentDay));
        }
        criteria.setProjection(Projections.property("cl.contractId"));
        criteria.setMaxResults(1);
        return (Long) criteria.uniqueResult();
    }

    public boolean enableClientNotificationBySMS(List<Long> contractId, boolean state) {
        Query q = entityManager
                .createQuery("update Client set notifyViaSMS=:notifyViaSMS where contractId in (:contractId)");
        q.setParameter("notifyViaSMS", state);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    public boolean enableClientNotificationByPUSH(List<Long> contractId, boolean state) {
        Query q = entityManager
                .createQuery("update Client set notifyViaPUSH=:notifyViaPUSH where contractId in (:contractId)");
        q.setParameter("notifyViaPUSH", state);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    public boolean enableClientNotificationByEmail(List<Long> contractId, boolean state) {
        Query q = entityManager
                .createQuery("update Client set notifyViaEmail=:notifyViaEmail where contractId in (:contractId)");
        q.setParameter("notifyViaEmail", state);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    public boolean setClientMobilePhone(Long contractId, String mobile, Date dateConfirm,
            ClientsMobileHistory clientsMobileHistory) {
        Query q = entityManager.createQuery(
                "update Client set mobile=:mobile, lastConfirmMobile = :lastConfirmMobile where contractId=:contractId");
        q.setParameter("mobile", mobile);
        q.setParameter("contractId", contractId);
        q.setParameter("lastConfirmMobile", dateConfirm);
        logger.info("class : DAOService, method : setClientMobilePhone line : 382, contractId : " + contractId
                + " mobile : " + mobile);
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
        logger.info("class : DAOService, method : setClientPhone line : 390, contractId : " + contractId + " phone : "
                + phone);
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
        Query q = entityManager
                .createQuery("update Client set cypheredPassword=:base64passwordHash where contractId=:contractId");
        q.setParameter("base64passwordHash", base64passwordHash);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    public boolean setClientExpenditureLimit(Long contractId, long limit) {
        Query q = entityManager
                .createQuery("update Client set expenditureLimit=:expenditureLimit where contractId=:contractId");
        q.setParameter("expenditureLimit", limit);
        q.setParameter("contractId", contractId);
        return q.executeUpdate() != 0;
    }

    public boolean setClientBalanceToNotify(Long contractId, long threshold) {
        Query q = entityManager
                .createQuery("update Client set balanceToNotify=:threshold where contractId=:contractId");
        q.setParameter("threshold", threshold);
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

    public Client getClientByContractId(long contractId) {
        return DAOUtils.findClientByContractId(entityManager, contractId);
    }

    public List<Client> findClientsBySan(String san) {
        return DAOUtils.findClientsBySan(entityManager, san);
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

    public List<TechnologicalMap> findTechnologicalMapByTechnologicalMapGroup(
            TechnologicalMapGroup technologicalMapGroup) {
        TypedQuery<TechnologicalMap> query = entityManager
                .createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup",
                        TechnologicalMap.class);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public Org findOrById(long idOfOrg) {
        return entityManager.find(Org.class, idOfOrg);
    }

    public List<Long> findFriendlyOrgsIds(long idOfOrg) {
        List<Long> ids = new ArrayList<Long>();
        Session session = entityManager.unwrap(Session.class);
        Org org = (Org) session.load(Org.class, idOfOrg);
        for (Org org1 : org.getFriendlyOrg()) {
            ids.add(org1.getIdOfOrg());
        }
        return ids;
    }

    public ReportInfo findReportInfoById(long idOfReportInfo) {
        return entityManager.find(ReportInfo.class, idOfReportInfo);
    }

    public Client findClientById(Long idOfClient) {
        return entityManager.find(Client.class, idOfClient);
    }

    public String getClientFullNameById(Long idOfClient) {
        Client client = findClientById(idOfClient);
        if (client != null) {
            return client.getPerson().getFullName();
        } else {
            return null;
        }
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

    public List<String> getReportHandleRuleNames() {
        TypedQuery<String> query = entityManager
                .createQuery("select ruleName from ReportHandleRule order by ruleName", String.class);
        return query.getResultList();
    }

    public void updateLastSuccessfulBalanceSync(long idOfOrg) {
        Query q = entityManager
                .createQuery("update OrgSync set lastSuccessfulBalanceSync=:date where idOfOrg=:idOfOrg");
        q.setParameter("date", new Date());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }

    public void updateLastUnsuccessfulBalanceSync(long idOfOrg) {
        Query q = entityManager
                .createQuery("update OrgSync set lastUnSuccessfulBalanceSync=:date where idOfOrg=:idOfOrg");
        q.setParameter("date", new Date());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }

    public List<Org> getOrderedSynchOrgsList() {
        return getOrderedSynchOrgsList(false);
    }

    public List<Org> getActiveOrgsList() {
        return getOrderedSynchOrgsList(true);
    }

    public List<Org> getOrderedSynchOrgsList(boolean excludeDisabled) {
        String disabledClause = "";
        if (excludeDisabled) {
            disabledClause = " where os.state <> 0 ";
        }
        TypedQuery<Org> query = entityManager
                .createQuery("select os.org from OrgSync os " + disabledClause + " order by lastSuccessfulBalanceSync",
                        Org.class);
        return query.getResultList();
    }

    public long getStatClientsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientsWithMobile() {
        Query q = entityManager
                .createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE mobile IS NOT NULL AND mobile<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientsWithEmail() {
        Query q = entityManager
                .createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE email IS NOT NULL AND email<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatUniqueClientsWithPaymentTransaction() {
        Query q = entityManager.createNativeQuery(
                "SELECT count(DISTINCT idofclient) FROM cf_clientpayments cp, cf_transactions t WHERE cp.idoftransaction=t.idoftransaction");
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
        Query q = entityManager.createNativeQuery(
                "SELECT count(DISTINCT idofclient) FROM cf_orders o, cf_orderdetails od WHERE o.idoforder=od.idoforder AND o.idoforg=od.idoforg ");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsWithPurchaseOfMealBenefits() {
        Query q = entityManager.createNativeQuery(
                "SELECT count(DISTINCT idofclient) FROM cf_orders o, cf_orderdetails od WHERE o.idoforder=od.idoforder AND o.idoforg=od.idoforg AND od.menutype>50");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsPayPowerPurchase() {
        Query q = entityManager.createNativeQuery(
                "SELECT count(DISTINCT idofclient) FROM cf_orders o, cf_orderdetails od WHERE o.idoforder=od.idoforder AND o.idoforg=od.idoforg AND od.menutype=0");
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
        Query q = entityManager
                .createNativeQuery("UPDATE cf_cards SET state=:state, lockreason=:reason WHERE idofCard=:idOfCard");
        q.setParameter("state", state);
        q.setParameter("reason", reason);
        q.setParameter("idOfCard", idOfCard);
        return q.executeUpdate() > 0;
    }

    //
    //public Map<Long, Integer> getOrgEntersCountByGroupType(Date at, Date to, int groupType) {
    //    Session session = (Session) entityManager.getDelegate();
    //    return getOrgEntersCountByGroupType(at, to, groupType, session);
    //}
    //


    @SuppressWarnings("unchecked")
    public boolean bindClientToGroup(long idofclient, long idofclientgroup) {
        if (idofclient < 0) {
            return false;
        }

        try {
            Query q = entityManager.createNativeQuery(
                    "UPDATE cf_clients SET idofclientgroup=:idofclientgroup WHERE idofclient=:idofclient");
            q.setParameter("idofclient", idofclient);
            q.setParameter("idofclientgroup", idofclientgroup);
            return q.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Failed to rebind client " + idofclient + " to group " + idofclientgroup, e);
        }
        return false;
    }


    public List<Contragent> getContragentsList() {
        /*TypedQuery<Contragent> query = entityManager.createQuery("from Contragent", Contragent.class);
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result;*/
        return getContragentsList(null);
    }

    @Transactional
    public List<Contragent> getContragentsListFromOrders() {
        String q = "SELECT distinct c from Contragent c, Order o WHERE o.contragent=c";
        TypedQuery<Contragent> query = entityManager.createQuery(q, Contragent.class);
        List<Contragent> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }


    public List<Contragent> getContragentsList(Integer classId) {
        String q = "from Contragent";
        if (classId != null) {
            q += " c WHERE c.classId=:classId order by idOfContragent";
        }
        TypedQuery<Contragent> query = entityManager.createQuery(q, Contragent.class);
        if (classId != null) {
            query.setParameter("classId", classId);
        }
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    //public ReportInfo getReportInfo(Long idOfOrg, Date startDate, Date endDate, String reportName) {
    //    String sql = "from ReportInfo where idOfOrg=:idOfOrg and startDate=:startDate and endDate=:endDate and reportFile like '%"+reportName+"%'";
    //    TypedQuery<ReportInfo> query = entityManager.createQuery(sql, ReportInfo.class);
    //    query.setParameter("idOfOrg",idOfOrg);
    //    query.setParameter("startDate",startDate);
    //    query.setParameter("endDate",endDate);
    //    List<ReportInfo> reportInfoList = query.getResultList();
    //    if (reportInfoList.isEmpty()){
    //        return null;
    //    } else {
    //        return reportInfoList.get(0);
    //    }
    //}

    @SuppressWarnings("unchecked")
    public Contragent getContragentByBIC(String bic) {
        TypedQuery<Contragent> query = entityManager
                .createQuery("from Contragent where bic=:bic and classId=:classId", Contragent.class);
        query.setParameter("bic", bic);
        query.setParameter("classId", Contragent.PAY_AGENT);
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public Contragent getRNIPContragent() {
        TypedQuery<Contragent> query = entityManager
                .createQuery("from Contragent where remarks=:remarks", Contragent.class);
        query.setParameter("remarks", "RNIP_DEFAULT");
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public Org getOrgByGuid(String guid) {
        javax.persistence.Query q = entityManager.createQuery("from Org where guid=:guid");
        q.setParameter("guid", guid);
        List l = q.getResultList();
        if (l.size() == 0) {
            return null;
        }
        return ((Org) l.get(0));
    }

    public List<Org> findOrgByRegistryIdOrGuid(Long registryId, String guid) {
        javax.persistence.Query q = entityManager
                .createQuery("from Org where guid=:guid or uniqueaddressid=:registryId");
        q.setParameter("guid", guid);
        q.setParameter("registryId", registryId);
        return q.getResultList();
    }

    public Org findOrgByRegistryDataByMainField(Long uniqueAddressId, String field_name, Object field_value, String inn,
            Long unom, Long unad, Boolean skipThirdPart) {
        //Новый алгоритм поиска организации в нашей БД по данным от реестров. Сраниваем в 3 этапа по разным наборам полей
        entityManager.setFlushMode(FlushModeType.COMMIT);
        javax.persistence.Query q;
        if (uniqueAddressId != null) {
            q = entityManager.createQuery(String.format(
                    "from Org o left join fetch o.officialPerson p where o.%s=:parameter and o.uniqueAddressId=:uniqueAddressId",
                    field_name));
            q.setParameter("parameter", field_value);
            q.setParameter("uniqueAddressId", uniqueAddressId);
            List<Org> orgs = q.getResultList();   //1 этап
            if (orgs != null && orgs.size() > 0) {
                return orgs.get(0);
            } else {
                q = entityManager.createQuery(
                        "from Org o left join fetch o.officialPerson p where o.INN=:inn and o.uniqueAddressId=:uniqueAddressId");
                q.setParameter("inn", inn);
                q.setParameter("uniqueAddressId", uniqueAddressId);
                List<Org> orgs2 = q.getResultList();  //2 этап
                if (orgs2 != null && orgs2.size() > 0) {
                    return orgs2.get(0);
                }
            }
        }
        if (null != skipThirdPart && skipThirdPart && (null == unom || null == unad)) {
            return null;
        }
        q = entityManager
                .createQuery("from Org o left join fetch o.officialPerson p where o.btiUnom=:unom and o.btiUnad=:unad");
        q.setParameter("unom", unom);
        q.setParameter("unad", unad);
        List<Org> orgs3 = q.getResultList();   //3 этап
        if (orgs3 != null && orgs3.size() > 0) {
            return orgs3.get(0);
        } else {
            return null;
        }

    }

    public Org findOrgByRegistryData(Long uniqueAddressId, String guid, String inn, Long unom, Long unad,
            Boolean skipThirdPart) {
        return findOrgByRegistryDataByMainField(uniqueAddressId, "guid", guid, inn, unom, unad, skipThirdPart);
    }

    public List<Org> findOrgsByEkisId(Long ekisId) {
        Query q = entityManager.createQuery("select org from Org org where org.ekisId = :ekisId");
        q.setParameter("ekisId", ekisId);
        return q.getResultList();
    }

    public List<Org> findOrgsByGuidAddressINNOrNumber(String guid, String address, String inn, String number) {
        String queryStr;
        if (StringUtils.isEmpty(address)) {
            queryStr = "from Org where 1=0";
        } else {
            queryStr = "from Org where address = :address";
        }

        if (!StringUtils.isEmpty(guid)) {
            queryStr += " or guid = :guid";
        }
        if (!StringUtils.isEmpty(inn)) {
            queryStr += " or INN = :inn";
        }
        if (!StringUtils.isEmpty(number)) {
            queryStr += " or shortName like :number";
        }
        javax.persistence.Query q = entityManager.createQuery(queryStr);
        if (!StringUtils.isEmpty(address)) {
            q.setParameter("address", address);
        }
        if (!StringUtils.isEmpty(guid)) {
            q.setParameter("guid", guid);
        }
        if (!StringUtils.isEmpty(inn)) {
            q.setParameter("inn", inn);
        }
        if (!StringUtils.isEmpty(number)) {
            q.setParameter("number", number);
        }
        return q.getResultList();
    }

    public Client getClientByGuid(String guid) {
        return DAOUtils.findClientByGuid(entityManager, guid);
    }

    public Client getClientByMeshGuid(String guid) {
        return DAOUtils.findClientByMeshGuid(entityManager.unwrap(Session.class), guid);
    }

    public Client getClientByMobilePhone(String mobile) {
        return DAOUtils.getClientByMobilePhone(entityManager, mobile);
    }

    public List<Client> getClientsListByMobilePhone(String mobile) {
        if (mobile == null || mobile.length() < 1) {
            return Collections.EMPTY_LIST;
        }
        List res = DAOUtils.getClientsListByMobilePhone(entityManager, mobile);
        if (res == null || res.size() < 1) {
            return Collections.EMPTY_LIST;
        }
        return (List<Client>) res;
    }

    public ReportHandleRule getReportHandleRule(long idOfReportHandleRule) {
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

    public String getReportHandlerType(long idOfReportHandleRule) {
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

    public List<ReportHandleRule> getReportHandlerRules(boolean manualAllowed) {
        try {
            Criteria reportRulesCriteria = ReportHandleRule
                    .createAllReportRulesCriteria(manualAllowed, (Session) entityManager.getDelegate());
            List<ReportHandleRule> rules = reportRulesCriteria.list();
            return rules;
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public List<RuleCondition> getReportHandlerRules(Long ruleId) {
        TypedQuery<RuleCondition> query = entityManager
                .createQuery("from RuleCondition where idOfRuleCondition=:handler", RuleCondition.class);
        query.setParameter("handler", ruleId);
        List<RuleCondition> result = query.getResultList();
        return result;
    }

    public Contragent getContragentById(Long idOfContragent) throws Exception {
        return DAOUtils.findContragent((Session) entityManager.getDelegate(), idOfContragent);
    }

    public Contract getContractById(Long idOfContract) throws Exception {
        return DAOUtils.findContract((Session) entityManager.getDelegate(), idOfContract);
    }

    public String getContractNameById(Long idOfContract) throws Exception {
        Contract contract = DAOUtils.findContract((Session) entityManager.getDelegate(), idOfContract);
        return contract.getContractNumber();
    }

    @Transactional(readOnly = true)
    public List<ConfigurationProvider> findConfigurationProvidersList() {
        return entityManager.createQuery("from ConfigurationProvider order by id", ConfigurationProvider.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<ConfigurationProvider> findConfigurationProvidersList(String filter) {
        return entityManager.createQuery(
                "from ConfigurationProvider where UPPER(name) like '%" + filter.toUpperCase() + "%' order by id",
                ConfigurationProvider.class).getResultList();
    }

    public void persistConfigurationProvider(ConfigurationProvider currentConfigurationProvider, List<Long> idOfOrgList)
            throws Exception {
        entityManager.persist(currentConfigurationProvider);
        if (!idOfOrgList.isEmpty()) {
            for (Long idOfOrg : idOfOrgList) {
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
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from GoodGroup order by globalId", GoodGroup.class);
        } else {
            query = entityManager
                    .createQuery("from GoodGroup where deletedState=false order by globalId", GoodGroup.class);
        }
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<GoodGroup> query;
        if (deletedStatusSelected) {
            query = entityManager
                    .createQuery("from GoodGroup where orgOwner in :orgOwners order by globalId", GoodGroup.class);
        } else {
            query = entityManager
                    .createQuery("from GoodGroup where orgOwner in :orgOwners and deletedState=false order by globalId",
                            GoodGroup.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(String filter) {
        TypedQuery<GoodGroup> query = entityManager.createQuery(
                "from GoodGroup where UPPER(nameOfGoodsGroup) like '%" + filter.toUpperCase()
                        + "%' and deletedState=false order by globalId", GoodGroup.class);
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(List<Long> orgOwners, String filter) {
        TypedQuery<GoodGroup> query = entityManager.createQuery(
                "from GoodGroup where UPPER(nameOfGoodsGroup) like '%" + filter.toUpperCase()
                        + "%' and orgOwner in :orgOwners and deletedState=false  order by globalId", GoodGroup.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<Good> findGoodsByGoodGroup(GoodGroup goodGroup, Boolean deletedStatusSelected) {
        TypedQuery<Good> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from Good where goodGroup=:goodGroup order by globalId", Good.class);
        } else {
            query = entityManager
                    .createQuery("from Good where goodGroup=:goodGroup and deletedState=false order by globalId",
                            Good.class);
        }
        query.setParameter("goodGroup", goodGroup);
        return query.getResultList();
    }

    public List<Good> findGoodsByGoodGroup(GoodGroup goodGroup, List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<Good> query;
        if (deletedStatusSelected) {
            query = entityManager
                    .createQuery("from Good where goodGroup=:goodGroup and orgOwner in :orgOwner order by globalId",
                            Good.class);
        } else {
            query = entityManager.createQuery(
                    "from Good where goodGroup=:goodGroup and orgOwner in :orgOwner and deletedState=false order by globalId",
                    Good.class);
        }
        query.setParameter("goodGroup", goodGroup);
        query.setParameter("orgOwner", orgOwners);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Good> findGoods(ConfigurationProvider configurationProvider, GoodGroup goodGroup, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(Good.class);
        if (goodGroup != null) {
            criteria.add(Restrictions.eq("goodGroup", goodGroup));
        }
        if (orgOwners != null && !orgOwners.isEmpty()) {
            criteria.add(Restrictions.in("orgOwner", orgOwners));
        }
        if (configurationProvider != null) {
            criteria.add(
                    Restrictions.eq("idOfConfigurationProvider", configurationProvider.getIdOfConfigurationProvider()));
        }
        if (deletedStatusSelected != null && !deletedStatusSelected) {
            criteria.add(Restrictions.eq("deletedState", false));
        }
        //if(StringUtils.isNotEmpty(nameOfGoodsGroup))
        //    criteria.add(Restrictions.ilike("nameOfGoodsGroup",nameOfGoodsGroup, MatchMode.ANYWHERE));
        return criteria.list();
    }

    public List<GoodsBasicBasket> findGoodsBasicBasket() {
        TypedQuery<GoodsBasicBasket> query = entityManager
                .createQuery("from GoodsBasicBasket order by idOfBasicGood", GoodsBasicBasket.class);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(Long idOfConfigurationProvider,
            Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",
                    ProductGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",
                    ProductGroup.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",
                    ProductGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",
                    ProductGroup.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from ProductGroup where orgOwner in :orgOwners order by globalId",
                    ProductGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from ProductGroup where orgOwner in :orgOwners and deletedState=false order by globalId",
                    ProductGroup.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(String filter) {
        TypedQuery<ProductGroup> query = entityManager.createQuery(
                "from ProductGroup where UPPER(nameOfGroup) like '%" + filter.toUpperCase()
                        + "%' and deletedState=false order by globalId", ProductGroup.class);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from ProductGroup order by globalId", ProductGroup.class);
        } else {
            query = entityManager
                    .createQuery("from ProductGroup where deletedState=false order by globalId", ProductGroup.class);
        }
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(List<Long> orgOwners, String filter) {
        TypedQuery<ProductGroup> query = entityManager.createQuery(
                "from ProductGroup where UPPER(nameOfGroup) like '%" + filter.toUpperCase()
                        + "%' and orgOwner in :orgOwners and deletedState=false order by globalId", ProductGroup.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> findProductByConfigurationProvider(ProductGroup pg, Long confProviderId, Boolean deleteSt,
            List<Long> orgOwners, String productName) {
        Session session = (Session) entityManager.getDelegate();
        Criteria cr = session.createCriteria(Product.class);
        Conjunction conj = Restrictions.conjunction();
        if (pg != null) {
            conj.add(Restrictions.eq("productGroup", pg));
        }
        if (confProviderId != null) {
            conj.add(Restrictions.eq("idOfConfigurationProvider", confProviderId));
        }
        if (deleteSt != null && !deleteSt) {
            conj.add(Restrictions.eq("deletedState", false));
        }
        if (orgOwners != null && !orgOwners.isEmpty()) {
            conj.add(Restrictions.in("orgOwner", orgOwners));
        }
        if (productName != null && !productName.isEmpty()) {
            conj.add(Restrictions.ilike("productName", productName, MatchMode.ANYWHERE));
        }
        cr.add(conj).addOrder(Order.asc("globalId"));
        return (List<Product>) cr.list();
    }

    public List<Product> findProductByConfigurationProvider(List<Long> orgOwners, String productName) {
        return findProductByConfigurationProvider(orgOwners, false, productName);
    }

    public List<Product> findProductByConfigurationProvider(List<Long> orgOwners, Boolean deletedStatusSelected,
            String productName) {
        return findProductByConfigurationProvider(null, null, deletedStatusSelected, orgOwners, productName);
    }

    public TechnologicalMapGroup findTechnologicalMapGroupByTechnologicalMap(TechnologicalMap technologicalMap) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findTechnologicalMapGroupByTechnologicalMap(session, technologicalMap);
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(Long idOfConfigurationProvider,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",
                    TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",
                    TechnologicalMapGroup.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",
                    TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMapGroup.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if (deletedStatusSelected) {
            query = entityManager
                    .createQuery("from TechnologicalMapGroup order by globalId", TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMapGroup where deletedState=false order by globalId",
                    TechnologicalMapGroup.class);
        }
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if (deletedStatusSelected) {
            query = entityManager
                    .createQuery("from TechnologicalMapGroup where orgOwner in :orgOwners order by globalId",
                            TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMapGroup.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(String filter) {
        TypedQuery<TechnologicalMapGroup> query = entityManager.createQuery(
                "from TechnologicalMapGroup where UPPER(nameOfGroup) like '%" + filter.toUpperCase()
                        + "%' and deletedState=false order by globalId", TechnologicalMapGroup.class);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(List<Long> orgOwners,
            String filter) {
        TypedQuery<TechnologicalMapGroup> query = entityManager.createQuery(
                "from TechnologicalMapGroup where UPPER(nameOfGroup) like '%" + filter.toUpperCase()
                        + "%' and orgOwner in :orgOwners and deletedState=false order by globalId",
                TechnologicalMapGroup.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<Product> findProduct(ProductGroup productGroup, ConfigurationProvider provider, String filter,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findProduct(session, productGroup, provider, filter, orgOwners, deletedStatusSelected);
    }


    public List<GoodGroup> findGoodGroup(ConfigurationProvider provider, String filter, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
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

    public List<TechnologicalMapProduct> findTechnologicalMapProductByTechnologicalMap(Long idOfTechnologicalMap) {
        String sql = "from TechnologicalMapProduct where technologicalMap.globalId=:idOfTechnologicalMap and deletedState=false order by globalId";
        TypedQuery<TechnologicalMapProduct> query = entityManager.createQuery(sql, TechnologicalMapProduct.class);
        query.setParameter("idOfTechnologicalMap", idOfTechnologicalMap);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from TechnologicalMap order by globalId", TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(Long idOfConfigurationProvider,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from TechnologicalMap where orgOwner in :orgOwners order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(
            TechnologicalMapGroup technologicalMapGroup, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(
            TechnologicalMapGroup technologicalMapGroup, Long idOfConfigurationProvider,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(
            TechnologicalMapGroup technologicalMapGroup, Long idOfConfigurationProvider, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(
            TechnologicalMapGroup technologicalMapGroup, List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and orgOwner in :orgOwners order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("orgOwners", orgOwners);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }


    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(String filter) {
        TypedQuery<TechnologicalMap> query = entityManager.createQuery(
                "from TechnologicalMap where UPPER(nameOfTechnologicalMap) like '%" + filter.toUpperCase()
                        + "%' and deletedState=false order by globalId", TechnologicalMap.class);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(List<Long> orgOwners, String filter) {
        TypedQuery<TechnologicalMap> query = entityManager.createQuery(
                "from TechnologicalMap where UPPER(nameOfTechnologicalMap) like '%" + filter.toUpperCase()
                        + "%' and orgOwner in :orgOwners and deletedState=false order by globalId",
                TechnologicalMap.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }


    public boolean updateBasicGood(Long idOfBasicGood, String nameOfGood, UnitScale unitsScale, Long netWeight,
            List<Long> idOfProviders) {
        GoodsBasicBasket goodsBasicBasket = entityManager.find(GoodsBasicBasket.class, idOfBasicGood);
        Set<ConfigurationProvider> set = new HashSet<ConfigurationProvider>();
        for (Long id : idOfProviders) {
            ConfigurationProvider provider = entityManager.find(ConfigurationProvider.class, id);
            set.add(provider);
        }
        goodsBasicBasket.setNameOfGood(nameOfGood);
        goodsBasicBasket.setUnitsScale(unitsScale);
        goodsBasicBasket.setNetWeight(netWeight);
        goodsBasicBasket.setLastUpdate(new Date());
        goodsBasicBasket.setConfigurationProviders(set);
        entityManager.persist(goodsBasicBasket);
        return true;
    }

    public boolean createBasicGood(String nameOfGood, UnitScale unitsScale, Long netWeight, List<Long> idOfProviders)
            throws Exception {
        GoodsBasicBasket goodsBasicBasket = new GoodsBasicBasket(UUID.randomUUID().toString());
        Set<ConfigurationProvider> set = new HashSet<ConfigurationProvider>();
        for (Long id : idOfProviders) {
            ConfigurationProvider provider = entityManager.find(ConfigurationProvider.class, id);
            if (provider == null) {
                throw new Exception("Не найдена производственная конфигурация с ид.=" + id);
            }
            set.add(provider);
        }
        goodsBasicBasket.setNameOfGood(nameOfGood);
        goodsBasicBasket.setUnitsScale(unitsScale);
        goodsBasicBasket.setNetWeight(netWeight);
        goodsBasicBasket.setLastUpdate(new Date());
        goodsBasicBasket.setConfigurationProviders(set);
        entityManager.persist(goodsBasicBasket);
        return true;
    }

    // не рекомендуется к использованию
    @Deprecated
    @Transactional
    public List<Client> getClientsByOrgId(long idOfOrg) {
        TypedQuery<Client> query = entityManager.createQuery("from Client where idOfOrg=:idoforg", Client.class);
        query.setParameter("idoforg", idOfOrg);
        List<Client> clients = query.getResultList();
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

    public void applyFullSyncOperationByOrgList(List<Long> idOfOrgList) throws Exception {
        Query query = entityManager.createQuery("update Org set fullSyncParam=1 where idOfOrg in :idOfOrgList");
        query.setParameter("idOfOrgList", idOfOrgList);
        query.executeUpdate();
    }

    public void applyUsePlanOrdersOperationByOrgList(List<Long> idOfOrgList) throws Exception {
        Query query = entityManager.createQuery("update Org set usePlanOrders=1 where idOfOrg in :idOfOrgList");
        query.setParameter("idOfOrgList", idOfOrgList);
        query.executeUpdate();
    }

    public void applyHaveNewLPForOrg(Long idOfOrg, boolean value) throws Exception {
        Query query = entityManager.createQuery("update Org set haveNewLP=:valueB where idOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("valueB", value);
        query.executeUpdate();
    }

    public List<ComplexRole> findComplexRoles() {
        return entityManager.createQuery("from ComplexRole order by idOfRole", ComplexRole.class).getResultList();
    }

    public List<ComplexRole> updateComplexRoles(List<ComplexRole> complexRoles) throws Exception {
        List<ComplexRole> roles = new ArrayList<ComplexRole>(complexRoles.size());
        for (ComplexRole complexRole : complexRoles) {
            ComplexRole role = entityManager.find(ComplexRole.class, complexRole.getIdOfRole());
            String roleName = complexRole.getRoleName();
            if (StringUtils.isEmpty(roleName) || StringUtils.isEmpty(roleName.trim())) {
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
        TypedQuery<CategoryDiscount> q = entityManager
                .createQuery("from CategoryDiscount where idOfCategoryDiscount in (:idOfCategoryList)",
                        CategoryDiscount.class);
        q.setParameter("idOfCategoryList", idOfCategoryList);
        return q.getResultList();
    }

    public List<CategoryOrg> getCategoryOrgWithIds(List<Long> idOfCategoryOrgList) {
        TypedQuery<CategoryOrg> q = entityManager
                .createQuery("from CategoryOrg where idOfCategoryOrg in (:idOfCategoryOrgList)", CategoryOrg.class);
        q.setParameter("idOfCategoryOrgList", idOfCategoryOrgList);
        return q.getResultList();
    }

    public List<CategoryOrg> getCategoryOrgByCategoryName(String categoryName) {
        TypedQuery<CategoryOrg> q = entityManager
                .createQuery("from CategoryOrg where lower(categoryName) = lower(:categoryName)", CategoryOrg.class);
        q.setParameter("categoryName", categoryName);
        return q.getResultList();
    }

    public List<Org> findOrgsByConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        TypedQuery<Org> query = entityManager
                .createQuery("from Org where configurationProvider=:configurationProvider", Org.class);
        query.setParameter("configurationProvider", currentConfigurationProvider);
        return query.getResultList();
    }

    public Good getGood(Long globalId) {
        return entityManager.find(Good.class, globalId);
    }

    public List<Client> findClientsForOrgAndFriendly(Long idOfOrg, boolean lazyLoadInit) throws Exception {
        List<Client> cl = DAOUtils.findClientsForOrgAndFriendly(entityManager, entityManager.find(Org.class, idOfOrg));
        if (lazyLoadInit) {
            for (Client c : cl) {
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

    public Boolean isOrgFriendly(Long targetOrgId, Long testOrgId) {
        Org testOrg = entityManager.find(Org.class, testOrgId);
        Set<Org> set = testOrg.getFriendlyOrg();
        for (Org o : set) {
            if (targetOrgId.equals(o.getIdOfOrg())) {
                return true;
            }
        }
        return false;
    }

    public List<RegistryChange> getLastRegistryChanges_WithFullFIO(long idOfOrg, long revisionDate, Integer actionFilter,
            String lastName, String firstName, String patronymic, String className) throws Exception {
        if (revisionDate < 1L) {
            revisionDate = getLastRegistryChangeUpdate(idOfOrg, className);
        }
        if (revisionDate < 1) {
            return Collections.EMPTY_LIST;
        }
        String nameStatement = "";
        if (!StringUtils.isEmpty(lastName)) {
            String filter = lastName.trim().toLowerCase().replaceAll(" ", "");
            nameStatement += " and lower(surname) like lower('%" + filter + "%') ";
        }
        if (!StringUtils.isEmpty(firstName)) {
            String filter = firstName.trim().toLowerCase().replaceAll(" ", "");
            nameStatement += " and lower(firstname) like lower('%" + filter + "%') ";
        }
        if (!StringUtils.isEmpty(patronymic)) {
            String filter = patronymic.trim().toLowerCase().replaceAll(" ", "");
            nameStatement += " and lower(secondname) like lower('%" + filter + "%') ";
        }

        String actionStatement = "";
        if (actionFilter != null && actionFilter > 0) {
            actionStatement = " and operation=:operation ";
        }
        String q = "from " + className + " where idOfOrg=:idOfOrg and createDate=:lastUpdate" + nameStatement
                + actionStatement + " order by groupName, surname, firstName, secondName";
        //TypedQuery<RegistryChange> query = entityManager.createQuery(q, RegistryChange.class);
        Query query = entityManager.createQuery(q);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("lastUpdate", revisionDate);
        if (actionFilter != null && actionFilter > 0) {
            query.setParameter("operation", actionFilter);
        }
        return query.getResultList();
    }

    public List<RegistryChange> getLastRegistryChanges(long idOfOrg, long revisionDate, Integer actionFilter,
            String nameFilter, String className) throws Exception {
        return getLastRegistryChanges_WithFullFIO(idOfOrg, revisionDate, actionFilter,
                nameFilter, null, null, className);
    }

    public long getLastRegistryChangeUpdate(long idOfOrg, String className) throws Exception {
        String tableName = className.equals("RegistryChange") ? "cf_registrychange" : "cf_registrychange_employee";
        Query q = entityManager
                .createNativeQuery("SELECT max(createDate) FROM " + tableName + " WHERE idOfOrg=:idOfOrg");
        q.setParameter("idOfOrg", idOfOrg);
        Object res = q.getSingleResult();
        return Long.parseLong("" + (res == null || res.toString().length() < 1 ? 0 : res.toString()));
    }

    public List<Object[]> getRegistryChangeRevisions(long idOfOrg, String className) throws Exception {
        Query query = entityManager.createQuery(
                "select distinct createDate, type from " + className + " where idOfOrg=:idOfOrg order by createDate desc");
        query.setParameter("idOfOrg", idOfOrg);
        return query.getResultList();
    }

    public List<RegistryChangeError> getRegistryChangeErrors(long idOfOrg) throws Exception {
        String orgClause = "";
        if (idOfOrg > -1) {
            orgClause = "where idOfOrg=:idOfOrg";
        }
        TypedQuery<RegistryChangeError> query = entityManager
                .createQuery("from RegistryChangeError " + orgClause + " order by createDate desc",
                        RegistryChangeError.class);
        if (idOfOrg > -1) {
            query.setParameter("idOfOrg", idOfOrg);
        }
        return query.getResultList();
    }

    public void addRegistryChangeErrorComment(long idOfRegistryChangeError, String comment, String author)
            throws Exception {
        Session session = (Session) entityManager.getDelegate();
        RegistryChangeError e = entityManager.find(RegistryChangeError.class, idOfRegistryChangeError);
        e.setComment(comment);
        e.setCommentAuthor(author);
        e.setCommentCreateDate(System.currentTimeMillis());
        session.update(e);
    }

    public void addRegistryChangeError(long idOfOrg, long revisionDate, String error, String errorDetails)
            throws Exception {
        Session session = (Session) entityManager.getDelegate();
        RegistryChangeError e = new RegistryChangeError();
        e.setIdOfOrg(idOfOrg);
        e.setRevisionCreateDate(revisionDate);
        e.setError(error);
        e.setCreateDate(System.currentTimeMillis());
        e.setErrorDetail(errorDetails);
        session.save(e);
    }

    public Long getMainRegistryByItemId(Long idOfItem) {
        Session session = (Session) entityManager.getDelegate();
        try {
            OrgRegistryChangeItem item = (OrgRegistryChangeItem) session.load(OrgRegistryChangeItem.class, idOfItem);
            return item.getOrgRegistryChange().getIdOfOrgRegistryChange();
        } catch (Exception e) {
            logger.error("Error retrieving MainRegistryChange by OrgRegistryChangeItem ID", e);
            return null;
        }
    }

    public List<String> getCurrentRepositoryReportNames() {
        TypedQuery<String> query = entityManager
                .createQuery("select distinct ruleName from ReportInfo order by ruleName", String.class);
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
        org.hibernate.Query q = session
                .createSQLQuery("UPDATE cf_reportinfo SET rulename=:newName WHERE rulename=:previousName");
        q.setString("newName", newName.trim());
        q.setString("previousName", previousName.trim());
        q.executeUpdate();
    }

    public List<BigInteger> getCleanupRepositoryReportsByDate() throws Exception {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery("SELECT idofreportinfo " + "FROM cf_reportinfo "
                + "LEFT JOIN cf_reporthandlerules ON cf_reportinfo.rulename=cf_reporthandlerules.rulename "
                + "WHERE cf_reporthandlerules.storageperiod<>-1 AND "
                + "      (cf_reporthandlerules.storageperiod=0 OR "
                + "       createddate<EXTRACT(EPOCH FROM now())*1000-cf_reporthandlerules.storageperiod)");
        List<BigInteger> list = (List<BigInteger>) q.list();
        return list;
    }

    public Contragent findContragentByClient(Long clientContractId) {
        TypedQuery<Contragent> query = entityManager.createQuery(
                "select o.defaultSupplier from Org o join o.clientsInternal cl \n"
                        + "where cl.contractId = :contractId", Contragent.class)
                .setParameter("contractId", clientContractId);
        List<Contragent> res = query.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    public long getSynchErrorsCount(Org org) {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session
                .createSQLQuery("SELECT count(idoforg) FROM cf_synchistory_exceptions WHERE idoforg=:idoforg");
        q.setLong("idoforg", org.getIdOfOrg());
        return ((BigInteger) q.uniqueResult()).longValue();
    }

    public long getComplexPrice(long idoforg, int complex) {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery(
                "SELECT currentprice, max(menudate) AS d " + "FROM cf_complexinfo "
                        + "WHERE idoforg=:idoforg AND idofcomplex=:complex " + "GROUP BY currentprice "
                        + "ORDER BY d DESC");
        q.setLong("idoforg", idoforg);
        q.setInteger("complex", complex);
        List res = q.list();
        for (Object o : res) {
            Object entry[] = (Object[]) o;
            if (entry[0] == null) {
                return 0L;
            }
            return ((BigInteger) entry[0]).longValue();
        }
        return 0L;
    }

    public ComplexInfo getComplexInfo(Client client, Integer idOfComplex, Date date) {
        Query query = entityManager.createQuery("select ci from ComplexInfo ci where ci.org.idOfOrg = :idOfOrg "
                + "and ci.idOfComplex = :idOfComplex and ci.menuDate between :startDate and :endDate");
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfComplex", idOfComplex);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        try {
            return (ComplexInfo)query.getSingleResult();
        } catch (Exception e) {
            logger.error(String.format("Cant find complexInfo idOfComplex=%s, date=%s, idOfClient=%s", idOfComplex, date.getTime(), client.getIdOfClient()), e);
            return null;
        }
    }
    public long getNextIdOfOrder(Org org) {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery("SELECT max(idoforder) FROM cf_orders WHERE idoforg=:idoforg");
        q.setLong("idoforg", org.getIdOfOrg());
        return ((BigInteger) q.uniqueResult()).longValue() + 1;
    }

    public long getNextIdOfOrderDetail(Org org) {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session
                .createSQLQuery("SELECT max(idoforderdetail) FROM cf_orderdetails WHERE idoforg=:idoforg");
        q.setLong("idoforg", org.getIdOfOrg());
        return ((BigInteger) q.uniqueResult()).longValue() + 1;
    }

    public String[] getDistricts() {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session
                .createSQLQuery("SELECT DISTINCT district FROM cf_orgs WHERE district<>'' ORDER BY district");
        List<String> list = (List<String>) q.list();
        return list.toArray(new String[list.size()]);
    }

    public ReportInfo registerReport(String ruleName, int documentFormat, String reportName, Date createdDate,
            Long generationTime, Date startDate, Date endDate, String reportFile, String orgNum, Long idOfOrg,
            String tag, Long idOfContragentReceiver, String contragentReceiver, Long idOfContragent, String contragent,
            Integer createState) {
        if (endDate == null) {
            endDate = startDate;
        }
        ReportInfo ri = new ReportInfo(ruleName, documentFormat, reportName, createdDate, generationTime, startDate,
                endDate, reportFile, orgNum, idOfOrg, tag, idOfContragent, contragent, idOfContragentReceiver,
                contragentReceiver, createState);
        entityManager.merge(ri);
        return ri;
    }

    public ReportInfo saveReportInfo(ReportInfo reportInfo) {
        entityManager.persist(reportInfo);
        return reportInfo;
    }

    public ReportInfo updateReportInfo(ReportInfo reportInfo) {
        return entityManager.merge(reportInfo);
    }

    public Map<Long, String> getUserOrgses(Long userId, UserNotificationType type) {
        Session session = entityManager.unwrap(Session.class);
        Map<Long, String> map = new HashMap<Long, String>();
        Criteria criteria = session.createCriteria(UserOrgs.class);
        criteria.add(Restrictions.eq("user.idOfUser", userId));
        criteria.add(Restrictions.eq("userNotificationType", type));
        List list = criteria.list();
        for (Object obj : list) {
            UserOrgs userOrgs = (UserOrgs) obj;
            final Org org = userOrgs.getOrg();
            map.put(org.getIdOfOrg(), org.getShortName());
            //orgList.add(org);
        }
        return map;
    }

    @Transactional(readOnly = true)
    public void updateInfoCurrentUser(List<Long> orgIds, List<Long> orgIdsCancel, User user) {
        Session session = entityManager.unwrap(Session.class);
        session.refresh(user);
        user.getUserOrgses().clear();
        session.flush();
        for (Long orgId : orgIds) {
            Org org = (Org) session.load(Org.class, orgId);
            UserOrgs userOrgs = new UserOrgs(user, org, UserNotificationType.GOOD_REQUEST_CHANGE_NOTIFY);
            session.save(userOrgs);
        }

        for (Long orgIdCanceled : orgIdsCancel) {
            Org org1 = (Org) session.load(Org.class, orgIdCanceled);
            UserOrgs userOrgs1 = new UserOrgs(user, org1, UserNotificationType.ORDER_STATE_CHANGE_NOTIFY);
            session.save(userOrgs1);
        }
    }

    @Transactional(readOnly = true)
    public boolean existsOrgByIdAndTags(Long idOfOrg, String tag) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        criteria.add(Restrictions.ilike("tag", tag, MatchMode.ANYWHERE));
        List list = criteria.list();
        return list.size() > 0;
    }

    public List<String> getRegions() {
        Session session = (Session) entityManager.getDelegate();
        return DAOUtils.getRegions(session);
    }

    public List<Org> getOrgsByDefaultSupplier(Contragent supplier) {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createQuery("from Org where defaultSupplier = :supplier");
        q.setParameter("supplier", supplier);
        List<Org> list = (List<Org>) q.list();
        return list;
    }

    public List<Client> getNotBindedEMPClients(int clientsPerPackage) {
        String q = "from Client where (ssoid is null or ssoid='') and (mobile is not null and mobile<>'')";//and clientGUID<>''";
        TypedQuery<Client> query = entityManager.createQuery(q, Client.class);
        query.setMaxResults(clientsPerPackage);
        return query.getResultList();
    }

    public Person getPersonByClient(Client client) {
        Client cl = (Client) entityManager.merge(client);
        Person p = cl.getPerson();
        p.getFirstName();
        return p;
    }

    public long getNotBindedEMPClientsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE ssoid IS null AND mobile<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getBindedEMPClientsCount() {
        Query q = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM cf_clients WHERE ssoid IS NOT null AND ssoid<>'-1' AND mobile<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getBindWaitingEMPClients() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE ssoid='-1'");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getBindEMPErrorsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE ssoid LIKE 'E:%'");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long receiveIdOfOrgByAccessory(long idoforg, int accessoryType, Long accessoryNumber) {
        if (accessoryNumber == null) {
            return idoforg;
        }
        return receiveIdOfOrgByAccessory(idoforg, accessoryType, "" + accessoryNumber);
    }

    @Transactional
    public long receiveIdOfOrgByAccessory(long idoforg, int accessoryType, String accessoryNumber) {
        if (accessoryNumber == null || StringUtils.isBlank(accessoryNumber)) {
            return idoforg;
        }

        try {
            Query q = entityManager.createQuery(
                    "FROM Accessory where idOfSourceOrg=:idoforg and accessoryType=:accessoryType and accessoryNumber=:accessoryNumber",
                    Accessory.class);
            q.setParameter("idoforg", idoforg);
            q.setParameter("accessoryType", accessoryType);
            q.setParameter("accessoryNumber", accessoryNumber);

            List res = q.getResultList();
            if (res == null || res.size() < 1) {
                createAccessory(idoforg, accessoryType, accessoryNumber);
                return idoforg;
            }
            Accessory acc = (Accessory) res.get(0);
            return acc.getIdOfTargetOrg();
        } catch (Exception e) {
            logger.error("Failed to receive accessory", e);
            return idoforg;
        }
    }

    public Accessory createAccessory(long idoforg, int accessoryType, String accessoryNumber) {
        Accessory accessory = new Accessory();
        accessory.setIdOfSourceOrg(idoforg);
        accessory.setIdOfTargetOrg(idoforg);
        accessory.setAccessoryType(accessoryType);
        accessory.setAccessoryNumber(accessoryNumber);
        accessory.setUsedSinceSeptember(false);
        entityManager.merge(accessory);
        return accessory;
    }

    public ExternalSystemStats getAllPreviousStatsForExternalSystem(String systemName, String instance) {
        Query q = entityManager.createNativeQuery(
                "SELECT CreateDate, StatisticId, StatisticValue " + "FROM cf_external_system_stats "
                        + "WHERE CreateDate=(SELECT max(CreateDate) FROM cf_external_system_stats WHERE SystemName=:systemName AND Instance=:instance);");
        q.setParameter("systemName", systemName);
        q.setParameter("instance", instance);
        List res = q.getResultList();
        ExternalSystemStats result = null;
        for (Object o : res) {
            Object entry[] = (Object[]) o;
            BigInteger createDate = (BigInteger) entry[0];
            Integer typeId = (Integer) entry[1];
            BigDecimal value = (BigDecimal) entry[2];
            if (result == null) {
                result = new ExternalSystemStats(new Date(createDate.longValue()), systemName, instance);
            }
            result.setValue(typeId.intValue(), value.doubleValue());
        }
        if (result == null) {
            result = new ExternalSystemStats(new Date(System.currentTimeMillis()), systemName, instance);
        }
        return result;
    }

    @Transactional
    public ExternalSystemStats saveStatsForExtermalSystem(ExternalSystemStats stats) {
        if (stats == null || stats.getCreateDate() == null || stats.getName() == null || StringUtils
                .isBlank(stats.getName()) || stats == null || stats.getValues().size() < 1) {
            return null;
        }

        long newDate = System.currentTimeMillis();
        deleteStatsFromExternalSystem(stats, newDate);
        try {
            Query q = entityManager.createNativeQuery(
                    "INSERT INTO cf_external_system_stats (SystemName, Instance, CreateDate, StatisticId, StatisticValue) VALUES "
                            + "(:systemName, :instance, :createDate, :statisticId, :statisticValue)");
            q.setParameter("systemName", stats.getName());
            q.setParameter("instance", stats.getInstance());
            q.setParameter("createDate", newDate);
            for (Integer typeId : stats.getValues().keySet()) {
                BigDecimal val = new BigDecimal(stats.getValue(typeId)).setScale(4);
                q.setParameter("statisticId", typeId);
                q.setParameter("statisticValue", val);
                q.executeUpdate();
            }
            stats.setCreateDate(new Date(newDate));
            return stats;
        } catch (Exception e) {
            logger.error("Failed to update external system statistics", e);
        }
        return stats;
    }

    public boolean deleteStatsFromExternalSystem(ExternalSystemStats stats, long date) {
        if (stats == null || stats.getCreateDate() == null || stats.getName() == null || StringUtils
                .isBlank(stats.getName()) || stats == null || stats.getValues().size() < 1) {
            return false;
        }
        try {
            Query q = entityManager.createNativeQuery(
                    "DELETE FROM cf_external_system_stats " + "WHERE SystemName=:systemName AND Instance=:instance AND "
                            + "CreateDate=:createDate AND StatisticId=:statisticId");
            q.setParameter("systemName", stats.getName());
            q.setParameter("instance", stats.getInstance());
            q.setParameter("createDate", date);
            for (Integer typeId : stats.getValues().keySet()) {
                q.setParameter("statisticId", typeId);
                q.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            //logger.error("Failed to update external system statistics", e);
            return false;
        }
    }

    public List<OrgRegistryChange> getOrgRegistryChanges() throws Exception {
        return getOrgRegistryChanges(null);
    }

    public List<OrgRegistryChange> getOrgRegistryChanges(String nameFilter) throws Exception {
        return getOrgRegistryChanges(nameFilter, "", -1L, 0L, false);
    }

    public List<OrgRegistryChange> getOrgRegistryChangeByDate(long revisionDate) throws Exception {
        String q = "from OrgRegistryChange where createDate=:lastUpdate order by officialName";
        TypedQuery<OrgRegistryChange> query = entityManager.createQuery(q, OrgRegistryChange.class);
        query.setParameter("lastUpdate", revisionDate);
        return query.getResultList();
    }

    public OrgRegistryChange getOrgRegistryChange(Long idOfOrgRegistryChange) throws Exception {
        String q = "from OrgRegistryChange where idOfOrgRegistryChange=:id";
        TypedQuery<OrgRegistryChange> query = entityManager.createQuery(q, OrgRegistryChange.class);
        query.setParameter("id", idOfOrgRegistryChange);
        return query.getSingleResult();
    }

    public List<OrgRegistryChange> getOrgRegistryChanges(String nameFilter, String regionFilter, long revisionDate,
            long operationType, boolean hideApplied) throws Exception {
        if (revisionDate < 1L) {
            revisionDate = getLastOrgRegistryChangeRevision();
        }
        if (revisionDate < 1) {
            return Collections.EMPTY_LIST;
        }
        String nameStatement = "";
        if (nameFilter != null && nameFilter.length() > 0) {
            nameStatement = " and (lower(shortName||officialName) like lower('%" + nameFilter + "%')"
                    + " or lower(shortNameFrom||officialNameFrom) like lower('%%" + nameFilter + "%'))";
        }
        if (!StringUtils.isEmpty(regionFilter)) {
            nameStatement += String.format(" and region like '%s%s%s'", regionFilter, "%", "%");
        }
        if (operationType > 0) {
            nameStatement += " and OperationType = " + operationType + " ";
        }
        if (hideApplied) {
            nameStatement += " and applied = false ";
        }
        String q = "from OrgRegistryChange where createDate=:lastUpdate" + nameStatement + " order by officialName";
        TypedQuery<OrgRegistryChange> query = entityManager.createQuery(q, OrgRegistryChange.class);
        query.setParameter("lastUpdate", revisionDate);
        return query.getResultList();
    }

    public List<OrgRegistryChange> getOrgRegistryChangesThroughOrgRegistryChangeItems(String nameFilter,
            long revisionDate, String regionFilter, long operationType, boolean hideApplied,
            List<OrgRegistryChange> orgRegistryChangeList) throws Exception {
        boolean isEmptyRegionFilter = StringUtils.isEmpty(regionFilter);
        boolean isAllOperations = operationType <= 0;
        if (revisionDate < 1L) {
            revisionDate = getLastOrgRegistryChangeRevision();
        }
        if (revisionDate < 1) {
            return Collections.EMPTY_LIST;
        }
        String nameStatement = "";
        if (nameFilter != null && nameFilter.length() > 0) {
            nameStatement = " and (lower(rci.shortName||rci.officialName) like lower('%" + nameFilter + "%')"
                    + " or lower(rci.shortNameFrom||rci.officialNameFrom) like lower('%%" + nameFilter + "%'))";
        }
        String q = "select rci, rc from OrgRegistryChangeItem rci join rci.orgRegistryChange rc"
                + " where rci.createDate=:lastUpdate " + nameStatement + "order by rci.officialName";

        TypedQuery<Object[]> query = entityManager
                .createQuery(q, Object[].class); //JPA has no tool to get 2 or more objects
        query.setParameter("lastUpdate", revisionDate);
        List<Object[]> bufObjectList = query.getResultList();
        List<OrgRegistryChangeItem> resultOfQuery = new LinkedList<OrgRegistryChangeItem>();
        for (Object[] bufElm : bufObjectList) {
            OrgRegistryChangeItem item = (OrgRegistryChangeItem) bufElm[0];
            item.setOrgRegistryChange((OrgRegistryChange) bufElm[1]);
            resultOfQuery.add(item);
        }

        for (OrgRegistryChangeItem item : resultOfQuery) {
            OrgRegistryChange orgRegistryChange = item.getOrgRegistryChange();
            if (orgRegistryChange == null) {
                continue;
            }
            if (orgRegistryChangeList.indexOf(orgRegistryChange) == -1) {
                if ((orgRegistryChange.getRegion().equals(regionFilter) || isEmptyRegionFilter) && (
                        orgRegistryChange.getOperationType() == operationType || isAllOperations)) {
                    orgRegistryChangeList.add(orgRegistryChange);
                }
            }
        }
        return orgRegistryChangeList;
    }


    public long getLastOrgRegistryChangeRevision() throws Exception {
        Query q = entityManager.createNativeQuery("SELECT max(createDate) FROM cf_orgregistrychange");
        Object res = q.getSingleResult();
        return Long.parseLong("" + (res == null || res.toString().length() < 1 ? 0 : res.toString()));
    }

    public List<Long> getOrgRegistryChangeRevisionsList() throws Exception {
        Query q = entityManager.createNativeQuery(
                "SELECT DISTINCT(createDate) FROM cf_orgregistrychange ORDER BY createDate DESC LIMIT 200");
        List<BigInteger> result = q.getResultList();
        if (result == null || result.size() < 1) {
            return Collections.EMPTY_LIST;
        }
        List<Long> list = new ArrayList<Long>();
        for (BigInteger bi : result) {
            list.add(bi.longValue());
        }
        return list;
    }

    public OrgRegistryChange getOrgRegistryChange(long idOfOrgRegistryChange) {
        return DAOUtils.getOrgRegistryChange((Session) entityManager.unwrap(Session.class), idOfOrgRegistryChange);
    }

    public String getPersonNameByOrg(Org org) {
        Query query = entityManager.createNativeQuery(
                "SELECT (p.surname || ' ' || p.firstname || ' ' || p.secondname) AS fullname FROM cf_orgs cfo LEFT JOIN cf_persons p ON cfo.idofofficialperson = p.idofperson WHERE cfo.idoforg = :idOfOrg");
        query.setParameter("idOfOrg", org.getIdOfOrg());
        return (String) query.getSingleResult();
    }

    @Transactional
    public void registerSyncRequest(long idOfOrg, String idOfSync) {
        long dateAt = System.currentTimeMillis();
        try {
            if (idOfSync.length() > 30) {
                idOfSync = idOfSync.substring(0, 30);
            }
            Query q = entityManager.createNativeQuery(
                    "INSERT INTO cf_synchistory_daily (idofsync, idoforg, syncdate) VALUES "
                            + "(:idofsync, :idoforg, :syncdate)");
            q.setParameter("idofsync", idOfSync);
            q.setParameter("idoforg", idOfOrg);
            q.setParameter("syncdate", dateAt);
            q.executeUpdate();
        } catch (Exception e) {
            logger.error("Failed to add new synch daily history entry", e);
        }
        dateAt = System.currentTimeMillis() - dateAt;
        if (dateAt > 1000L) {
            logger.error(String.format("Time save cf_synchistory_daily = %s. idOfOrg = %s", dateAt, idOfOrg));
        }
    }

    public Long getClientGroupByClientId(Long idOfClient) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(Client.class);
        criteria.add(Restrictions.eq("idOfClient", idOfClient));
        Client client = (Client) criteria.uniqueResult();
        return client.getIdOfClientGroup();
    }

    public List findClientPaymentsByPaymentId(Contragent contragent, String idOfPayment) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findClientPaymentsForCorrectionOperation(session, contragent, idOfPayment);
    }

    public void saveCancelPayment(ClientPayment payment) {
        entityManager.persist(payment);
        entityManager.flush();
    }

    public Boolean isCancelPaymentExists(String idOfPayment) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ClientPayment.class);
        criteria.add(Restrictions.eq("idOfPayment", idOfPayment));
        criteria.addOrder(org.hibernate.criterion.Order.desc("createTime"));
        return !criteria.list().isEmpty();
        //return DAOUtils.existClientPayment(session, null, idOfPayment);
    }

    public List getClientBalanceInfos(String where, String where2, Date begDate, Date endDate, String clientWhere) {
        String str_query =
                "SELECT c.idofclient, o.shortname as shortname, g.groupname as groupname, c.contractId, p.surname as surname, p.firstname, p.secondname, c.limits, c.balance, "
                        + "coalesce((SELECT sum(t.transactionsum) FROM cf_transactions t WHERE t.idofclient = c.idofclient AND t.transactionDate >= :begDate AND t.transactionDate <= :endDate), 0), "
                        + "(SELECT min(t.transactiondate) FROM cf_transactions t WHERE t.idofclient = c.idofclient AND t.transactionDate > :begDate), "
                        + "o.idoforg as idoforg "
                        + "FROM cf_clients c INNER JOIN cf_orgs o ON c.idoforg = o.idoforg INNER JOIN cf_clientgroups g ON c.idofclientgroup = g.idofclientgroup AND c.idoforg = g.idoforg "
                        + where2 + " JOIN cf_persons p ON c.idofperson = p.idofperson WHERE c.idoforg in(" + where
                        + ") " + clientWhere
                        + " and c.idofclient not in (select idofclient from cf_clientmigrationhistory where registrationdate between :begDate and :endDate and idoforg in("
                        + where + ")) " + "union "
                        + "SELECT c.idofclient, shortname as shortname, h.oldgroupname as groupname, c.contractId, p.surname as surname, p.firstname, p.secondname, c.limits, c.balance, "
                        + "coalesce((SELECT sum(t.transactionsum) FROM cf_transactions t WHERE t.idofclient = h.idofclient AND t.transactionDate >= :begDate AND t.transactionDate <= :endDate), 0), "
                        + "(SELECT min(t.transactiondate) FROM cf_transactions t "
                        + "WHERE t.idofclient = c.idofclient AND t.transactionDate > :begDate), "
                        + "h.idofoldorg as idoforg "
                        + "FROM cf_clients c INNER JOIN cf_clientmigrationhistory h ON c.idofclient = h.idofclient "
                        + "JOIN cf_persons p ON c.idofperson = p.idofperson JOIN cf_orgs o ON h.idofoldorg = o.idoforg "
                        + "WHERE h.idofoldorg in(" + where + ") " + clientWhere
                        + " and h.registrationdate between :begDate and :endDate "
                        + "ORDER BY shortname, groupname, surname";
        Query q = entityManager.createNativeQuery(str_query);
        q.setParameter("begDate", begDate.getTime());
        q.setParameter("endDate", endDate.getTime());
        return q.getResultList();
    }

    public String getMenuSourceOrgName(Long idOfOrg) {
        Session session = (Session) entityManager.unwrap(Session.class);
        Long id = DAOUtils.findMenuExchangeSourceOrg(session, idOfOrg);
        if (id != null) {
            Org org = (Org) session.load(Org.class, id);
            return org.getOfficialName();
        }
        return null;
    }

    public String getDefaultSupplierNameByOrg(Long idOfOrg) {
        Contragent cc = getOrg(idOfOrg).getDefaultSupplier();
        if (cc != null) {
            return cc.getContragentName();
        } else {
            return null;
        }
    }

    public String getConfigurationProviderNameByOrg(Long idOfOrg) {
        ConfigurationProvider prov = getOrg(idOfOrg).getConfigurationProvider();
        if (prov != null) {
            return prov.getName();
        }
        return null;
    }

    public boolean isContractFromHistory(Long idOfOrg, Long idOfContract) {
        Session session = (Session) entityManager.unwrap(Session.class);
        Org org = (Org) session.load(Org.class, idOfOrg);
        if (org.getContract() == null) {
            return true;
        }
        return (org.getContract().getIdOfContract() != idOfContract);
    }

    public int runDebugTest2() {
        //разные тестовые плюшки
        return 0;
        /*return entityManager.createQuery("update Card set  state = 0 where cardNo = :cardNo")
                .setParameter("cardNo", 1666500521L)
                .executeUpdate();*/
    }

    public void saveSecurityJournalBalance(SecurityJournalBalance object) {
        entityManager.persist(object);
        entityManager.flush();
    }

    public void changeOrgSecurityLevel(Long idOfOrg, OrganizationSecurityLevel securityLevel) {
        Org org = findOrById(idOfOrg);
        org.setSecurityLevel(securityLevel);
        entityManager.persist(org);
        entityManager.flush();
    }

    public void setFullSyncByOrg(Long idOfOrg, boolean value) throws Exception {
        Transaction transaction = null;
        Session session = RuntimeContext.getInstance().createPersistenceSession();
        try {
            transaction = session.beginTransaction();
            org.hibernate.Query query = session.createQuery("update Org set fullSyncParam=:value where id=:idOfOrg");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("value", value);
            query.executeUpdate();
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("e", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    /*@Transactional
    public Boolean isSverkaEnabledByOrg(long idOfOrg) {
        try {
            String str_query = "select lastsyncstart, lastsyncend from cf_org_syncs_registry where idoforg = :idOfOrg";
            Query q = entityManager.createNativeQuery(str_query);
            q.setParameter("idOfOrg", idOfOrg);
            List list = q.getResultList();
            if (list.size() == 0) {
                return true;
            } else {
                Object[] row = (Object[])list.get(0);
                Long startDate = ((BigInteger)row[0]).longValue();
                Long endDate = ((BigInteger)row[1]).longValue();
                if (endDate == 1L) {
                    return false; //в настоящее время идет сверка
                } else if (System.currentTimeMillis() - endDate < ImportRegisterClientsService.TIME_DELTA_PER_REQUEST ||
                        System.currentTimeMillis() - startDate < ImportRegisterClientsService.TIME_DELTA_PER_REQUEST) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Can't get org last sync registry: ", e);
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOrgRegistrySync(long idOfOrg, long dateEnd) {
        try {
            Query q = entityManager.createNativeQuery("update cf_org_syncs_registry set lastsyncstart = :dateStart, lastsyncend = :dateEnd where idoforg = :idOfOrg");
            q.setParameter("idOfOrg", idOfOrg);
            q.setParameter("dateStart", new Date().getTime());
            q.setParameter("dateEnd", dateEnd);
            int res = q.executeUpdate();
            if (res == 0) {
                q = entityManager.createNativeQuery("insert into cf_org_syncs_registry(idoforg, lastsyncstart, lastsyncend) values(:idOfOrg, :dateStart, :dateEnd)");
                q.setParameter("idOfOrg", idOfOrg);
                q.setParameter("dateStart", new Date().getTime());
                q.setParameter("dateEnd", dateEnd);
                q.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("Can't save org last sync registry: ", e);
        }
    }*/

    private String getOnlineOptionValue(int option) throws Exception {
        String str_query = "select optiontext from cf_options where idofoption = :idofoption";
        Query q = entityManager.createNativeQuery(str_query);
        q.setParameter("idofoption", option);
        List list = q.getResultList();
        if (list.size() == 0) {
            throw new Exception(String.format("Option id=%s not found", option));
        }
        return (String) list.get(0);
    }

    @Transactional
    public String getReviseLastDate() {
        try {
            return getOnlineOptionValue(Option.OPTION_REVISE_LAST_DATE);
        } catch (Exception e) {
            return "";
        }
    }

    @Transactional
    public String getLastCountBlocked() {
        try {
            return getOnlineOptionValue(Option.OPTION_LAST_COUNT_CARD_BLOCK);
        } catch (Exception e) {
            return "";
        }
    }

    @Transactional
    public String getDeletedLastedDateMenu() {
        try {
            return getOnlineOptionValue(Option.OPTION_LAST_DELATED_DATE_MENU);
        } catch (Exception e) {
            return "";
        }
    }

    @Transactional
    public void setOnlineOptionValue(String value, int option) {
        String str_query = "select optiontext from cf_options where idofoption = :idofoption";
        Query q = entityManager.createNativeQuery(str_query);
        q.setParameter("idofoption", option);
        List list = q.getResultList();
        if (list.size() == 0) {
            str_query = "insert into cf_options (idofoption, optiontext) values(:idofoption, :value)";
        } else {
            str_query = "update cf_options set optiontext = :value where idofoption = :idofoption";
        }
        q = entityManager.createNativeQuery(str_query);
        q.setParameter("value", value);
        q.setParameter("idofoption", option);
        q.executeUpdate();
    }

    @Transactional
    public Boolean isSverkaEnabled() {
        try {
            String option = getOnlineOptionValue(Option.OPTION_SVERKA_ENABLED);
            return option.equals("1");
        } catch (Exception e) {
            logger.error("Can't get sverka permission value", e);
            return true;
        }
    }

    @Transactional
    public void setSverkaEnabled(Boolean value) {
        String val_str = value ? "1" : "0";
        setOnlineOptionValue(val_str, Option.OPTION_SVERKA_ENABLED);
    }

    public void saveTradeAccountConfigChangeDirective(Long idOfOrg) {
        Query q = entityManager
                .createNativeQuery("update cf_orgs set tradeconfigchanged = :value where idOfOrg = :idOfOrg");
        q.setParameter("value", TradeAccountConfigChange.NOT_CHANGED.getCode());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }

    public void saveDirective(Long idOfOrg, String directiveName, Integer directiveValue) {
        if (directiveValue == null) {
            return;
        }
        String query_str = String
                .format("update Org set %s=:value, updateTime = :lastUpdate where idOfOrg = :idOfOrg", directiveName);
        Query q = entityManager.createQuery(query_str);
        q.setParameter("value", directiveValue == 1);
        q.setParameter("lastUpdate", new Date());
        q.setParameter("idOfOrg", idOfOrg);
        q.executeUpdate();
    }

    public Org findOrgById(Long idOfOrg) {
        return entityManager.find(Org.class, idOfOrg);
    }

    //Загружаем производственный календарь из файла
    public void loadProductionCalendar(InputStream inputStream) throws Exception {
        Query query = entityManager.createQuery("select max(pc.version) from ProductionCalendar pc");
        Long version = (Long) query.getSingleResult();
        version = (version == null) ? 0L : (version + 1L);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        int lineNo = 0;
        String currLine = reader.readLine();
        while (null != currLine) {
            if (lineNo > 0) { //пропускаем заголовок
                String[] arr = currLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); //элементы массива - месяцы
                String sYear = arr[0];
                List<Integer> days = new ArrayList<Integer>();
                Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                for (int i = 1; i <= 12; i++) { //по месяцам
                    String[] sDays = arr[i].split(",");                                 //элементы - дни в месяце
                    days.clear();
                    map.clear();
                    for (int k = 0; k < sDays.length; k++) {
                        sDays[k] = sDays[k].replace("\"", "");
                    }
                    for (String d : sDays) {
                        Integer day = new Integer(d.replaceAll("\\*", ""));
                        days.add(day);
                        map.put(day, d.endsWith("*") ? ProductionCalendar.HOLIDAY : ProductionCalendar.REGULAR);
                    }
                    Calendar calendar = new GregorianCalendar(Integer.parseInt(sYear), i - 1, 1);
                    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    for (int j = 1; j <= daysInMonth; j++) { //по всем дням месяца
                        Date date = CalendarUtils.parseDate(
                                getStrForDate(new Integer(j).toString()) + "." + getStrForDate(
                                        new Integer(i).toString()) + "." + sYear);
                        if (!days.contains(j)) {
                            deleteProductionCalendarDay(date);
                            continue;
                        }

                        ProductionCalendar productionCalendar = getProductionCalendarByDate(date);
                        if (productionCalendar == null) {
                            productionCalendar = new ProductionCalendar(date, map.get(j), version);
                        } else {
                            productionCalendar.modify(map.get(j), version);
                        }
                        entityManager.merge(productionCalendar);
                    }
                }
            }
            currLine = reader.readLine();
            ++lineNo;
        }
    }

    private void deleteProductionCalendarDay(Date date) {
        Query query = entityManager.createQuery("delete from ProductionCalendar where day = :date");
        query.setParameter("date", date);
        query.executeUpdate();
    }

    public ProductionCalendar getProductionCalendarByDate(Date date) {
        Query query = entityManager.createQuery("select pc from ProductionCalendar pc where pc.day = :day");
        query.setParameter("day", date);
        try {
            return (ProductionCalendar) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public SpecialDate getSpecialCalendarByDate(Date date) {
        Query query = entityManager.createQuery("select sd from SpecialDate sd where sd.date = :day");
        query.setParameter("day", date);
        try {
            return (SpecialDate) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private String getStrForDate(String str) {
        return (str.length() == 1) ? "0" + str : str;
    }

    public IPreorderDAOOperations getPreorderDAOOperationsImpl() {
        return preorderDAOOperationsImpl;
    }

    public void setPreorderDAOOperationsImpl(IPreorderDAOOperations preorderDAOOperationsImpl) {
        this.preorderDAOOperationsImpl = preorderDAOOperationsImpl;
    }

    public String generateLinkingTokenForSmartWatch(Session session, String phone) throws Exception {
        org.hibernate.Query query = session
                .createQuery("delete from LinkingTokenForSmartWatch where phoneNumber like :phoneNumber");
        query.setParameter("phoneNumber", phone);
        query.executeUpdate();

        Date createDate = new Date();
        SecureRandom secureRandom = new SecureRandom();
        String randomToken;
        int nSize = 9;
        for (int nCycle = 0; ; nCycle++) {
            if (nCycle == 10) {
                nSize++;
                nCycle = 0;
            }
            randomToken = new BigInteger(nSize * 5, secureRandom).toString(32);
            query = session.createQuery("from LinkingToken where token=:token");
            query.setParameter("token", randomToken);
            List l = query.list();
            if (l.size() == 0) {
                break;
            }
        }
        LinkingTokenForSmartWatch token = new LinkingTokenForSmartWatch();
        token.setPhoneNumber(phone);
        token.setToken(randomToken);
        token.setCreateDate(createDate);
        session.save(token);
        return token.getToken();
    }

    public void updateLastProcessOrgChange(Date date) {
        entityManager.createQuery("update Option set optionText = :value where idOfOption = :idOfOption")
                .setParameter("value", new Long(date.getTime()).toString())
                .setParameter("idOfOption", new Long(Option.OPTION_LAST_ORG_CHANGE_PROCESS)).executeUpdate();
    }

    public void saveLogServiceMessage(String message, LogServiceType type) {
        LogService logService = new LogService(type, message);
        entityManager.merge(logService);
    }

    public List findCardsignByManufactureCodeForNewTypeProvider(Integer manufactureCode) {
        Query query = entityManager.createQuery(
                "select cs from CardSign cs where cs.manufacturerCode = :manufactureCode "
                        + "and cs.newtypeprovider = true and (cs.deleted = false or cs.deleted is null)");
        query.setParameter("manufactureCode", manufactureCode);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List findEMIASbyClientandBeetwenDates(Client client, Date startDate, Date endDate) {
        Query query = entityManager.createQuery("select em from EMIAS em where em.guid = :guid "
                + "and em.dateLiberate between :begDate and :endDate ");
        query.setParameter("guid", client.getClientGUID());
        query.setParameter("begDate", startDate);
        query.setParameter("endDate", endDate);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ExternalEvent getExternalEvent(Client client, String orgCode, String orgName, ExternalEventType evtType,
            Date evtDateTime, ExternalEventStatus evtStatus) {
        Query query = entityManager.createQuery("select ee from ExternalEvent ee where ee.client = :client "
                + "and ee.evtType = :evtType and ee.evtDateTime = :evtDateTime and ee.evtStatus = :evtStatus "
                + "and ee.orgCode = :orgCode and ee.orgName = :orgName");
        query.setParameter("client", client);
        query.setParameter("evtType", evtType);
        query.setParameter("evtDateTime", evtDateTime);
        query.setParameter("evtStatus", evtStatus);
        query.setParameter("orgCode", orgCode);
        query.setParameter("orgName", orgName);
        try {
            return (ExternalEvent) query.getResultList().get(0);
        } catch (NoResultException e) {
            return null;
        }
    }

    public void saveNotificationOrder(NotificationOrders object) {
        entityManager.persist(object);
        entityManager.flush();
    }

    public List<WtComplexGroupItem> getWtComplexGroupList() {
        TypedQuery<WtComplexGroupItem> q = entityManager
                .createQuery("from WtComplexGroupItem order by idOfComplexGroupItem", WtComplexGroupItem.class);
        return q.getResultList();
    }

    public List<WtAgeGroupItem> getWtAgeGroupList() {
        TypedQuery<WtAgeGroupItem> q = entityManager
                .createQuery("from WtAgeGroupItem order by idOfAgeGroupItem", WtAgeGroupItem.class);
        return q.getResultList();
    }

    public List<WtDietType> getWtDietTypeList() {
        TypedQuery<WtDietType> q = entityManager
                .createQuery("from WtDietType order by idOfDietType", WtDietType.class);
        return q.getResultList();
    }

    public Long getWtComplexGroupIdByDescription(String description) {
        try {
            return (Long) entityManager.createQuery("select cg.idOfComplexGroupItem from WtComplexGroupItem cg"
                    + " where lower(cg.description) like '%" + description + "%'").getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public Long getWtAgeGroupIdByDescription(String description) {
        try {
            return (Long) entityManager.createQuery("select ag.idOfAgeGroupItem from WtAgeGroupItem ag"
                    + " where lower(ag.description) like '%" + description + "%'").getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public List<CategoryDiscount> getCategoryDiscountListByWtRule(WtDiscountRule wtRule) {
        Query query = entityManager.createQuery("select cd from CategoryDiscount cd "
                + "inner join fetch cd.wtDiscountRules where cd.deletedState = false "
                + "and :wtRule in elements(cd.wtDiscountRules)");
        query.setParameter("wtRule", wtRule);
        return query.getResultList();
    }

    public List<WtComplex> getWtComplexesList() {
        TypedQuery<WtComplex> q = entityManager
                .createQuery("select wc from WtComplex wc left join fetch wc.wtComplexGroupItem complexItem "
                        + "left join fetch wc.wtAgeGroupItem ageItem "
                        + "left join fetch wc.wtDietType dietType "
                        + "left join fetch wc.contragent contragent "
                        + "where wc.deleteState = 0 order by wc.idOfComplex",
                        WtComplex.class);
        return q.getResultList();
    }

    public List<WtComplex> getWtComplexListByFilter(List<Long> wtComplexGroupIds, List<Long> wtAgeGroupIds, Long wtDietTypeId,
            List<Long> contragentIds, List<Long> orgIds, WtDiscountRule wtRule) {
        Query query = entityManager.createNativeQuery("select distinct wc.idofcomplex from cf_wt_complexes wc "
                + "left join cf_wt_org_group_relations wogr on wc.idoforggroup = wogr.idoforggroup "
                + "left join cf_wt_complexes_org wco on wco.idofcomplex = wc.idofcomplex "
                + "left join cf_wt_discountrules_complexes drc on drc.idofcomplex = wc.idofcomplex "
                + (wtRule == null ? "where wc.deleteState = 0" : "where drc.idofrule = :idOfRule")
                + (wtComplexGroupIds.size() == 0 ? "" : " and wc.idofcomplexgroupitem in (:wtComplexGroupIds)")
                + (wtAgeGroupIds.size() == 0 ? "" : " and wc.idofagegroupitem in (:wtAgeGroupIds)")
                + (wtDietTypeId == 0 ? "" : " and wc.idofdiettype = :wtDietTypeId")
                + (contragentIds.size() == 0 ? "" : " and wc.idofcontragent in (:contragentIds)")
                + (orgIds.size() == 0 ? "" : " and (wco.idoforg in (:orgIds) or wogr.idoforg in (:orgIds))"));
        if (wtComplexGroupIds.size() > 0) {
            query.setParameter("wtComplexGroupIds", wtComplexGroupIds);
        }
        if (wtAgeGroupIds.size() > 0) {
            query.setParameter("wtAgeGroupIds", wtAgeGroupIds);
        }
        if (wtDietTypeId > 0) {
            query.setParameter("wtDietTypeId", wtDietTypeId);
        }
        if (contragentIds.size() > 0) {
            query.setParameter("contragentIds", contragentIds);
        }
        if (orgIds.size() > 0) {
            query.setParameter("orgIds", orgIds);
        }
        if (wtRule != null) {
            query.setParameter("idOfRule", wtRule.getIdOfRule());
        }
        List list = query.getResultList();
        List<WtComplex> result = new ArrayList<>();
        for (Object obj : list) {
            Long idOfComplex = ((BigInteger) obj).longValue();
            result.add(getWtComplexById(idOfComplex));
        }
        return result;
    }
	
    public void setSendedNotificationforDTISZNDiscount(Long idofclientdtiszndiscountinfo, Boolean sendnotification) {
        Session session = (Session) entityManager.getDelegate();
        ClientDtisznDiscountInfo clientDtisznDiscountInfo = entityManager.find(ClientDtisznDiscountInfo.class, idofclientdtiszndiscountinfo);
        clientDtisznDiscountInfo.setSendnotification(sendnotification);
        session.update(clientDtisznDiscountInfo);
    }
	
    public boolean setFlagSendedNotification(Long idofregularpreorder, Boolean valuec) {
        Query q = entityManager
                .createNativeQuery("update cf_regular_preorders set sendeddailynotification = :valuec where idofregularpreorder = :idofregularpreorder");
        q.setParameter("valuec", valuec);
        q.setParameter("idofregularpreorder", idofregularpreorder);
        return q.executeUpdate() > 0;
    }

	public List<Contragent> contragentsListByUser(Long idOfUser) {
        Query q = entityManager.createQuery("select c from User u inner join u.contragents c"
                + " where u.idOfUser = :idOfUser order by c.contragentName");

        q.setParameter("idOfUser", idOfUser);
        return q.getResultList();
    }

    public Long getMeshIdByOrg(long idOfOrg) {
        Query query = entityManager.createNativeQuery("select organizationidfromnsi from cf_orgs where IdOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        Object obj = query.getSingleResult();
        return obj == null ? null : ((BigInteger)obj).longValue();
    }

    public WtComplex getWtComplexById(Long idOfComplex) {
        Query query = entityManager.createQuery("select wc from WtComplex wc "
                + "left join fetch wc.wtComplexGroupItem complexItem "
                + "left join fetch wc.wtAgeGroupItem ageItem "
                + "left join fetch wc.wtDietType dietType "
                + "left join fetch wc.contragent contragent "
                + "where wc.idOfComplex = :idOfComplex");
        query.setParameter("idOfComplex", idOfComplex);
        try {
            return (WtComplex) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public WtDish getWtDishById(Long idOfDish) {
        Query query = entityManager.createQuery("SELECT dish FROM WtDish dish "
                + "where dish.idOfDish = :idOfDish");
        query.setParameter("idOfDish", idOfDish);
        try {
            return (WtDish) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	//Список союзов организаций, кужа входит данная организация
    public List<Long> getOrgGroupsbyOrgForWEBARM(Long idOforg) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery(" select idoforggroup from cf_wt_org_group_relations "
                + "  where idoforg = " + idOforg);
        List<BigInteger> list = (List<BigInteger>) q.list();
        List<Long> result = new ArrayList<>();
        for (BigInteger value: list)
        {
            result.add(value.longValue());
        }
        return result;
    }

    public List<Long> getComplexesByOrgForWEBARM(Long idOforg) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery(" select idofcomplex from cf_wt_complexes_org where idoforg = "
                + idOforg);
        List<BigInteger> list = (List<BigInteger>) q.list();
        List<Long> result = new ArrayList<>();
        for (BigInteger value: list)
        {
            result.add(value.longValue());
        }
        return result;
    }
    public List getComplexesByGroupForWEBARM(List<Long> idOfgroups) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        String groupString = "";
        for (Long groupid: idOfgroups)
        {
            groupString = groupString + "'" + groupid.toString() + "',";
        }
        groupString = groupString.substring(0,groupString.length()-1);
        org.hibernate.Query q = session.createSQLQuery("select idofcomplex, name, begindate, enddate, idofagegroupitem from cf_wt_complexes "
                        + "where idofcomplexgroupitem in (1,3) and deletestate=0 and idoforggroup in (" + groupString + ")");
        return q.list();
    }

    public List getComplexesByComplexForWEBARM(List<Long> idOfComplexes) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        String idOfComplexString = "";
        for (Long idOfComplex: idOfComplexes)
        {
            idOfComplexString = idOfComplexString + "'" + idOfComplex.toString() + "',";
        }
        idOfComplexString = idOfComplexString.substring(0,idOfComplexString.length()-1);
        org.hibernate.Query q = session.createSQLQuery("select idofcomplex, name, begindate, enddate, idofagegroupitem from cf_wt_complexes "
                + "where idofcomplexgroupitem in (1,3) and deletestate=0 and idofcomplex in (" + idOfComplexString + ")");
        return q.list();
    }

    public List<WtComplex> getComplexesByWtDiscountRule(WtDiscountRule discountRule) {
        return DAOUtils.getComplexesByWtDiscountRule(entityManager, discountRule);
    }

    public List<CategoryDiscount> getCategoryDiscountsByWtDiscountRule(WtDiscountRule discountRule) {
        return DAOUtils.getCategoryDiscountsByWtDiscountRule(entityManager, discountRule);
    }

    public List<CategoryOrg> getCategoryOrgsByWtDiscountRule(WtDiscountRule discountRule) {
        return DAOUtils.getCategoryOrgsByWtDiscountRule(entityManager, discountRule);
    }
    public List<Client> getClientsBySoid(String ssoid) {
        return DAOUtils.getClientsBySsoid(entityManager, ssoid);
    }

    public List<WtGroupItem> getMapTypeFoods() {
        Query q = entityManager.createQuery("SELECT wtGroup from WtGroupItem wtGroup");
        return q.getResultList();
    }

    public List<WtAgeGroupItem> getAgeGroups() {
        Query q = entityManager.createQuery("SELECT wtAge FROM WtAgeGroupItem wtAge");
        return q.getResultList();
    }
}

