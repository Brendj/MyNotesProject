/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.IPreorderDAOOperations;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ExternalSystemStats;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
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

    public List<TransactionJournal> fetchTransactionJournal(int nRecs) {
        return DAOUtils.fetchTransactionJournalRecs(entityManager, nRecs);
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
        contractIds = contractIds.substring(0, contractIds.length() - 1);
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

    public long getDistributedObjectVersion(String name) {
        return getDistributedObjectVersionFromSequence(name);
    }

    public String getDistributedObjectSequenceName(String name) {
        return "DO_VERSION_" + name.toUpperCase() + "_SEQ";
    }

    private long getDistributedObjectVersionFromSequence(String name) {
        long version = 0L;
        Query query = entityManager.createNativeQuery(String.format("select nextval('%s')", getDistributedObjectSequenceName(name)));
        Object o = query.getSingleResult();
        if (o != null) {
            version = HibernateUtils.getDbLong(o);
        }
        return version;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long updateVersionByDistributedObjects(String name) {
        return getDistributedObjectVersion(name);
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

    public boolean setClientExpenditureLimit(Long contractId, long limit, long version) {
        Query q = entityManager
                .createQuery("update Client set expenditureLimit=:expenditureLimit, clientRegistryVersion = :version where contractId=:contractId");
        q.setParameter("expenditureLimit", limit);
        q.setParameter("version", version);
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

    public void addIntegraPartnerAccessPermissionToClient(Long idOfClient, String idOfIntegraPartner) throws Exception {
        Client cl = entityManager.find(Client.class, idOfClient);
        if (cl == null) {
            throw new Exception("Client not found: " + idOfClient);
        }
        cl.addIntegraPartnerAccessPermission(idOfIntegraPartner);
        entityManager.persist(cl);
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

    public Contragent getClientOrgDefaultSupplier(Client client) {
        client = entityManager.merge(client);
        Contragent ca = client.getOrg().getDefaultSupplier();
        ca.getContragentName(); //lazy load
        return ca;
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

    public boolean setCardStatus(long idOfCard, int state, String reason) {
        Query q = entityManager
                .createNativeQuery("UPDATE cf_cards SET state=:state, lockreason=:reason WHERE idofCard=:idOfCard");
        q.setParameter("state", state);
        q.setParameter("reason", reason);
        q.setParameter("idOfCard", idOfCard);
        return q.executeUpdate() > 0;
    }

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

    public TechnologicalMapGroup findTechnologicalMapGroupByTechnologicalMap(TechnologicalMap technologicalMap) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findTechnologicalMapGroupByTechnologicalMap(session, technologicalMap);
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

    public void applyUsePlanOrdersOperationByOrgList(List<Long> idOfOrgList) throws Exception {
        Query query = entityManager.createQuery("update Org set usePlanOrders=1 where idOfOrg in :idOfOrgList");
        query.setParameter("idOfOrgList", idOfOrgList);
        query.executeUpdate();
        for (Long idOfOrg : idOfOrgList) {
            Org.sendInvalidateCache(idOfOrg);
        }
    }

    public void applyHaveNewLPForOrg(Long idOfOrg, boolean value) throws Exception {
        Query query = entityManager.createQuery("update Org set haveNewLP=:valueB where idOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("valueB", value);
        query.executeUpdate();
        Org.sendInvalidateCache(idOfOrg);
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

    public long getSynchErrorsCount(Org org) {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session
                .createSQLQuery("SELECT count(idoforg) FROM cf_synchistory_exceptions WHERE idoforg=:idoforg");
        q.setLong("idoforg", org.getIdOfOrg());
        return ((BigInteger) q.uniqueResult()).longValue();
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

    @Transactional
    public void updateInfoCurrentUser(List<Long> orgIds, List<Long> orgIdsCancel, User user) {
        Session session = entityManager.unwrap(Session.class);
        session.refresh(user);
        session.flush();
        Query query = entityManager.createNativeQuery("DELETE FROM cf_userorgs WHERE idofuser = :idOfUser");
        query.setParameter("idOfUser", user.getIdOfUser());
        query.executeUpdate();

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

            List<Accessory> res = q.getResultList();
            if (CollectionUtils.isEmpty(res)) {
                createAccessory(idoforg, accessoryType, accessoryNumber);
                return idoforg;
            }
            Accessory acc = res.get(0);
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

    @Transactional
    public void saveSyncRequestInDb(String query_str) {
        Query query = entityManager.createNativeQuery(query_str);
        query.executeUpdate();
    }

    public void saveCancelPayment(ClientPayment payment) {
        entityManager.persist(payment);
        entityManager.flush();
    }

    public boolean isContractFromHistory(Long idOfOrg, Long idOfContract) {
        Session session = (Session) entityManager.unwrap(Session.class);
        Org org = (Org) session.load(Org.class, idOfOrg);
        if (org.getContract() == null) {
            return true;
        }
        return (org.getContract().getIdOfContract() != idOfContract);
    }

    public void saveSecurityJournalBalance(SecurityJournalBalance object) {
        entityManager.persist(object);
        entityManager.flush();
    }

    public void changeOrgSecurityLevel(Long idOfOrg, OrganizationSecurityLevel securityLevel) {
        Org org = DAOReadonlyService.getInstance().findOrg(idOfOrg);
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
            Org.sendInvalidateCache(idOfOrg);
        } catch (Exception e) {
            logger.error("e", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
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
        Org.sendInvalidateCache(idOfOrg);
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
        Org.sendInvalidateCache(idOfOrg);
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

    public void saveLogServiceMessage(String message, String response, LogServiceType type) {
        LogService logService = new LogService(type, message, response);
        entityManager.merge(logService);
    }

    public void saveNotificationOrder(NotificationOrders object) {
        entityManager.persist(object);
        entityManager.flush();
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

    public void updateExemptionVisiting() {
        Query query = entityManager.createQuery(
                "update EMIAS set archive=true, version=:version where endDateLiberate<:currentDate and archive=false");
        query.setParameter("currentDate", CalendarUtils.startOfDay(new Date()));
        query.setParameter("version", DAOUtils.getMaxVersionEMIAS((Session) entityManager.getDelegate(), true) + 1);
        query.executeUpdate();
    }

    public void saveQRinfo(ClientEnterQR clientEnterQR) {
        entityManager.persist(clientEnterQR);
        entityManager.flush();
    }

    @Transactional
    public void setCardSignID(CardSign cardSign, int newID) {
        String str_query = "update cf_card_signs set idofcardsign=:newID where idofcardsign=:oldID";
        Query q = entityManager.createNativeQuery(str_query);
        q = entityManager.createNativeQuery(str_query);
        q.setParameter("newID", newID);
        q.setParameter("oldID", cardSign.getIdOfCardSign());
        q.executeUpdate();
    }

    public void deleteOldFoodBoxAvailable(Org org) {
        Query query = entityManager.createQuery("delete from FoodBoxPreorderAvailable where org=:org");
        query.setParameter("org", org);
        query.executeUpdate();
    }
}

