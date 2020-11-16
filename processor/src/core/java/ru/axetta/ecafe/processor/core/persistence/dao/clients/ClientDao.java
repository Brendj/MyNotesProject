/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.model.ClientCount;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.generateNewClientGuardianVersion;
import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.createClientGroup;
import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.findClientGroupByGroupNameAndIdOfOrg;

/**
 * User: shamil
 * Date: 21.11.14
 * Time: 17:31
 */
@Repository
public class ClientDao extends WritableJpaDao {
    public static final String UNKNOWN_PERSON_DATA = "Не заполнено";
    public static final String UNKNOWN_PERSON_SURNAME = "Представитель обучающегося:";
    public static final String COMMENT_AUTO_CREATE = "{Создано %s}";
    private static final Logger logger = LoggerFactory.getLogger(ClientDao.class);

    public static ClientDao getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientDao.class);
    }
    @Transactional
    public Client update( Client entity ){
        return entityManager.merge( entity );
    }

    @Transactional
    public List<Client> findAllByPassword(String password) {
        TypedQuery<Client> query = entityManager
                .createQuery("from Client c where c.cypheredPassword = :password", Client.class)
                .setParameter("password", password);
        return query.getResultList();    //To change body of overridden methods use File | Settings | File Templates.
    }


    @Transactional
    public List<Client> findAllByOrg(Set<Long> orgsIdList ) {
        TypedQuery<Client> query = entityManager
                .createQuery("from Client c left join fetch c.clientGroup left join fetch c.person "
                        + " where c.org.id  in :orgsIdList and c.idOfClientGroup <> 1100000070 and c.idOfClientGroup <> 1100000060", Client.class)
                .setParameter("orgsIdList", orgsIdList);
        return query.getResultList();
    }


    @Transactional
    public List<Client> findAllByOrgAndGroupNames(Set<Long> orgsIdList, List<String> groupNamesList) {
        TypedQuery<Client> query = entityManager.createQuery(
                "from Client c left join fetch c.clientGroup left join fetch c.person "
                        + " where c.org.id  in :orgsIdList and c.idOfClientGroup <> 1100000070 and c.idOfClientGroup <> 1100000060 and c.clientGroup.groupName in :groupNamesList",
                Client.class).setParameter("orgsIdList", orgsIdList).setParameter("groupNamesList", groupNamesList);
        return query.getResultList();
    }

    @Transactional
    public List<Client> findAllByOrgAndContractId(Set<Long> orgsIdList, List<Long> contractIdList) {
        TypedQuery<Client> query = entityManager.createQuery(
                "from Client c left join fetch c.clientGroup left join fetch c.person "
                        + " where c.org.id  in :orgsIdList and c.idOfClientGroup <> 1100000070 and c.idOfClientGroup <> 1100000060 and c.contractId in :contractIdList",
                Client.class).setParameter("orgsIdList", orgsIdList).setParameter("contractIdList", contractIdList);
        return query.getResultList();
    }

    @Transactional
    public List<Client> findAllByOrgAndСlientId(Set<Long> orgsIdList, List<Long> clientIdList) {
        TypedQuery<Client> query = entityManager.createQuery(
                "from Client c left join fetch c.clientGroup left join fetch c.person "
                        + " where c.org.id  in :orgsIdList and c.idOfClientGroup <> 1100000070 and c.idOfClientGroup <> 1100000060 and c.idOfClient in :clientIdList",
                Client.class).setParameter("orgsIdList", orgsIdList).setParameter("clientIdList", clientIdList);
        return query.getResultList();
    }

    @Transactional
    public List<Client> findAllByOrgAndContractIdAndGroupNames(Set<Long> orgsIdList, List<Long> contractIdList,
            List<String> groupNamesList) {
        TypedQuery<Client> query = entityManager.createQuery(
                "from Client c left join fetch c.clientGroup left join fetch c.person "
                        + " where c.org.id  in :orgsIdList and c.idOfClientGroup <> 1100000070 and c.idOfClientGroup <> 1100000060 and c.contractId in :contractIdList and c.clientGroup.groupName in :groupNamesList",
                Client.class).setParameter("orgsIdList", orgsIdList).setParameter("contractIdList", contractIdList)
                .setParameter("groupNamesList", groupNamesList);
        return query.getResultList();
    }

    @Transactional
    public List<Client> findAllByOrgAndClientIdAndGroupNames(Set<Long> orgsIdList, List<Long> clientIdList,
            List<String> groupNamesList) {
        TypedQuery<Client> query = entityManager.createQuery(
                "from Client c left join fetch c.clientGroup left join fetch c.person "
                        + " where c.org.id  in :orgsIdList and c.idOfClientGroup <> 1100000070 and c.idOfClientGroup <> 1100000060 and c.idOfClient in :clientIdList and c.clientGroup.groupName in :groupNamesList",
                Client.class).setParameter("orgsIdList", orgsIdList).setParameter("clientIdList", clientIdList)
                .setParameter("groupNamesList", groupNamesList);
        return query.getResultList();
    }

    @Transactional
    public List<ClientCount> findAllStudentsCount() {
        Query nativeQuery = entityManager.createNativeQuery(
                "select idoforg, count(*) from cf_clients where idofclientgroup <  1100000000 group by idoforg ");
        List<ClientCount> result = new ArrayList<ClientCount>();
        for (Object o : nativeQuery.getResultList()) {
            Object[] o1 = (Object[]) o;
            result.add(new ClientCount(((BigInteger)o1[0]).longValue(),((BigInteger)o1[1]).intValue()));
        }
        return result;
    }


    @Transactional
    public List<ClientCount> findAllBeneficiaryStudentsCount() {
        Query nativeQuery = entityManager.createNativeQuery(
                "select idoforg, count(*) from cf_clients where idofclientgroup <  :groupLimitation and DiscountMode > 0 group by idoforg ");
        nativeQuery.setParameter("groupLimitation", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES);
        List<ClientCount> result = new ArrayList<ClientCount>();
        for (Object o : nativeQuery.getResultList()) {
            Object[] o1 = (Object[]) o;
            result.add(new ClientCount(((BigInteger)o1[0]).longValue(),((BigInteger)o1[1]).intValue()));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public String extractSanFromClient(long idOfClient){
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Client.class);
        criteria.add(Restrictions.eq("idOfClient", idOfClient));
        criteria.setProjection(Projections.property("san"));
        return (String) criteria.uniqueResult();
    }

    public int runGenerateGuardians(List idOfOrgs, ClientGuardianHistory clientGuardianHistory) throws Exception {
        logger.info("Start generate guardians");
        List<ClientContactInfo> clientsGenerate = new ArrayList<ClientContactInfo>();
        int result = 0;
        Transaction transaction = null;
        Session session = RuntimeContext.getInstance().createPersistenceSession();
        session.setFlushMode(FlushMode.COMMIT);
        try {

            //Получаем клиентов, у которых указан телефон или мейл
            String squery = "select c.idOfClient, c.idOfOrg, c.idOfClientGroup, c.mobile, c.email, c.contractId, c.notifyViaSMS, c.notifyViaEmail, c.notifyViaPUSH, c.ssoid, "
                    + "(select count(idofclientguardian) from cf_client_guardian cg where cg.idofchildren = c.idofclient) as guardCount "
                    + "from cf_clients c "
                    + "where "
                    + (idOfOrgs == null ? "" : "c.idOfOrg in :orgs and "
                    + "c.idOfClientGroup < :clientGroup and ")
                    + "((c.mobile is not null and c.mobile <> '') or (c.email is not null and c.email <> ''))";
            SQLQuery query = session.createSQLQuery(squery);
            query.setParameter("clientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            if (idOfOrgs != null) {
                query.setParameterList("orgs", idOfOrgs);
            }
            query.addScalar("idOfClient", new LongType());
            query.addScalar("idOfOrg", new LongType());
            query.addScalar("idOfClientGroup", new LongType());
            query.addScalar("mobile", new StringType());
            query.addScalar("email", new StringType());
            query.addScalar("contractId", new LongType());
            query.addScalar("notifyViaSMS", new IntegerType());
            query.addScalar("notifyViaEmail", new IntegerType());
            query.addScalar("notifyViaPUSH", new IntegerType());
            query.addScalar("ssoid", new StringType());
            query.addScalar("guardCount", new LongType());
            query.setResultTransformer(Transformers.aliasToBean(ClientContactInfo.class));

            List<ClientContactInfo> clients = query.list();
            logger.info(String.format("Found %s clients", clients.size()));
            for (ClientContactInfo ccInfo : clients) {
                if (ccInfo.getMobile() == null) {
                    continue;
                }
                try {
                    transaction = session.beginTransaction();
                    boolean doGenerate = false;
                    if (ccInfo.getGuardCount() == 0) {
                        //опекунов нет, надо сгенерить
                        doGenerate = true;
                    } else {
                        List<ClientGuardian> guardians = getGuardians(session, ccInfo.getIdOfClient());
                        boolean mobileFound = false;
                        for (ClientGuardian cg : guardians) {
                            org.hibernate.Query query1 = session.createQuery("select c from Client c where c.idOfClient = :idOfClient");
                            query1.setParameter("idOfClient", cg.getIdOfGuardian());
                            Client guardian = (Client)query1.uniqueResult();
                            if (guardian.getMobile() != null &&
                                    PhoneNumberCanonicalizator.canonicalize(guardian.getMobile()).equals(PhoneNumberCanonicalizator.canonicalize(ccInfo.getMobile()))) {
                                //хотя бы у одного опекуна найден номер мобильного ребенка - очищаем контактные данные у ребенка
                                mobileFound = true;
                                Client child = (Client) session.load(Client.class, ccInfo.getIdOfClient());
                                long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
                                refreshClientGuadianData(child, cg, session, clientGuardianHistory);
                                clearClientContacts(child, session, clientRegistryVersion);
                                refreshGuardianData(guardian, session, clientRegistryVersion);
                                logger.info(String.format("Cleared contacts from client id=%s", child.getIdOfClient()));
                                session.save(child);
                                break;
                            }
                        }
                        if (!mobileFound) {
                            doGenerate = true;
                        }
                    }
                    if (doGenerate) {
                        logger.info(String.format("Client id=%s added to list for generating guardians", ccInfo.getIdOfClient()));
                        clientsGenerate.add(ccInfo);
                        result++;
                    }
                    transaction.commit();
                    transaction = null;
                } catch (Exception ignoreRecord) {
                    logger.error("", ignoreRecord);
                }
                finally {
                    HibernateUtils.rollback(transaction, logger);
                }
            }
        } finally {
            HibernateUtils.close(session, logger);
        }
        createParentAndGuardianship(clientsGenerate, clientGuardianHistory);
        logger.info("End generate guardians");
        return result;
    }

    public List<ClientGuardian> getGuardians(Session session, Long idOfClient) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfClient));
        criteria.addOrder(org.hibernate.criterion.Order.asc("deletedState"));
        return criteria.list();
    }

    private Map<Long, List<ClientContactInfo>> getClientContactInfoMap(List<ClientContactInfo> contactInfos) {
        Map<Long, List<ClientContactInfo>> map = new HashMap<Long, List<ClientContactInfo>>();
        for (ClientContactInfo contactInfo : contactInfos) {
            List<ClientContactInfo> list = map.get(contactInfo.getIdOfOrg());
            if (list == null) list = new ArrayList<ClientContactInfo>();
            list.add(contactInfo);
            map.put(contactInfo.getIdOfOrg(), list);
        }
        return map;
    }

    private void createParentAndGuardianship(List<ClientContactInfo> clientInfos,
            ClientGuardianHistory clientGuardianHistory) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            session.setFlushMode(FlushMode.MANUAL);
            Map<Long, List<ClientContactInfo>> clientContactInfoMap = getClientContactInfoMap(clientInfos);
            for (Map.Entry<Long, List<ClientContactInfo>> entry : clientContactInfoMap.entrySet()) {
                Long idOfOrg = entry.getKey();
                List<ClientContactInfo> orgClientInfos = entry.getValue();
                List<Long> contractIds = RuntimeContext.getInstance().getClientContractIdGenerator()
                        .generateTransactionFree(idOfOrg, orgClientInfos.size());
                Iterator<Long> iterator = contractIds.iterator();
                for (ClientContactInfo clientInfo : orgClientInfos) {
                    try {
                        transaction = session.beginTransaction();
                        FieldProcessor.Config createConfig = new ClientManager.ClientFieldConfig();
                        Client child = (Client) session.load(Client.class, clientInfo.getIdOfClient());
                        createConfig.setValue(ClientManager.FieldId.SURNAME, UNKNOWN_PERSON_SURNAME);
                        try {
                            createConfig.setValue(ClientManager.FieldId.NAME, child.getPerson().getSurnameAndFirstLetters());
                        } catch (Exception e) {
                            createConfig.setValue(ClientManager.FieldId.NAME, UNKNOWN_PERSON_DATA);
                        }
                        createConfig.setValue(ClientManager.FieldId.SECONDNAME, "");
                        createConfig.setValue(ClientManager.FieldId.GROUP, ClientGroup.Predefined.CLIENT_PARENTS.getNameOfGroup());
                        createConfig.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, clientInfo.getNotifyViaPUSH());
                        createConfig.setValue(ClientManager.FieldId.NOTIFY_BY_SMS, clientInfo.getNotifyViaSMS());
                        createConfig.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, clientInfo.getNotifyViaEmail());
                        if (clientInfo.getMobile() != null) {
                            createConfig.setValue(ClientManager.FieldId.MOBILE_PHONE, clientInfo.getMobile());
                        }
                        if (clientInfo.getEmail() != null) {
                            createConfig.setValue(ClientManager.FieldId.EMAIL, clientInfo.getEmail());
                        }
                        if (clientInfo.getSsoid() != null) {
                            createConfig.setValue(ClientManager.FieldId.SSOID, clientInfo.getSsoid());
                        }
                        Long contractId = iterator.next();
                        if (contractId != null) {
                            createConfig.setValue(ClientManager.FieldId.CONTRACT_ID, contractId);
                        }

                        String dateCreate = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));

                        Client clientId = ClientManager.registerClientTransactionFree(clientInfo.getIdOfOrg(), (ClientManager.ClientFieldConfig) createConfig, false, session,
                                String.format(COMMENT_AUTO_CREATE, dateCreate));

                        //Создаем опекунскую связь
                        Long version = generateNewClientGuardianVersion(session);
                        ClientManager.addGuardianByClient(session, clientInfo.getIdOfClient(), clientId.getIdOfClient(),
                                version, false, null, null, ClientCreatedFromType.DEFAULT, null,
                                clientGuardianHistory);
                        session.flush();

                        //Устанавливаем правила оповещения для опекуна
                        Client client = (Client) session.load(Client.class, clientInfo.getIdOfClient());
                        ClientGuardian currentClientGuardian = DAOUtils
                                .findClientGuardian(session, clientInfo.getIdOfClient(), clientId.getIdOfClient());

                        for (ClientNotificationSetting item : client.getNotificationSettings()) {
                            ClientGuardianNotificationSetting notificationSetting = new ClientGuardianNotificationSetting(
                                    currentClientGuardian, item.getNotifyType());
                            currentClientGuardian.getNotificationSettings().add(notificationSetting);
                        }

                        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATIONS_ON_BALANCES_AND_EE)) {
                            boolean containsNotificationOnBalances = false;
                            boolean containsNotificationOnEnterEvent = false;

                            // getNotificationSettings().contains() always false
                            for (ClientGuardianNotificationSetting item : currentClientGuardian.getNotificationSettings()) {
                                if (item.getNotifyType()
                                        .equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_REFILLS.getValue())) {
                                    containsNotificationOnBalances = true;
                                } else if (item.getNotifyType()
                                        .equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_EVENTS.getValue())) {
                                    containsNotificationOnEnterEvent = true;
                                }
                                if (containsNotificationOnBalances && containsNotificationOnEnterEvent) {
                                    break;
                                }
                            }
                            if (!containsNotificationOnBalances) {
                                ClientGuardianNotificationSetting notificationOnBalancesSetting = new ClientGuardianNotificationSetting(
                                        currentClientGuardian, ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_REFILLS.getValue());
                                currentClientGuardian.getNotificationSettings().add(notificationOnBalancesSetting);
                            }
                            if (!containsNotificationOnEnterEvent) {
                                ClientGuardianNotificationSetting notificationOnEnterEventSetting = new ClientGuardianNotificationSetting(
                                        currentClientGuardian, ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_EVENTS.getValue());
                                currentClientGuardian.getNotificationSettings().add(notificationOnEnterEventSetting);
                            }
                        }
                        session.update(currentClientGuardian);

                        //Очищаем данные клиента (ребенка)
                        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
                        clearClientContacts(client, session, clientRegistryVersion);
                        session.update(client);
                        session.flush();
                        transaction.commit();
                        transaction = null;
                    } catch (Exception e) {
                        logger.error(String.format("Error in generate guardian for client id=%s: ", clientInfo.getIdOfClient()), e);
                        HibernateUtils.rollback(transaction, logger);
                    }

                }
            }
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    private void clearClientContacts(Client client, Session session, long clientRegistryVersion) throws Exception {
        client.setMobile("");
        client.setEmail("");
        client.setNotifyViaSMS(false);
        client.setNotifyViaPUSH(false);
        client.setNotifyViaEmail(false);
        client.setSsoid("");
        client.setClientRegistryVersion(clientRegistryVersion);
    }

    private void refreshGuardianData(Client guardian, Session session, long clientRegistryVersion) throws Exception {
        if (guardian.isDeletedOrLeaving()) {
            ClientGroup clientGroup = findClientGroupByGroupNameAndIdOfOrg(session, guardian.getOrg().getIdOfOrg(),
                    ClientGroup.Predefined.CLIENT_PARENTS.getNameOfGroup());
            if (clientGroup == null) {
                clientGroup = createClientGroup(session, guardian.getOrg().getIdOfOrg(), ClientGroup.Predefined.CLIENT_PARENTS);
            }
            guardian.setClientGroup(clientGroup);
            guardian.setIdOfClientGroup(ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            guardian.setClientRegistryVersion(clientRegistryVersion);
            session.update(guardian);
        }
    }

    private void refreshClientGuadianData(Client client, ClientGuardian clientGuardian, Session session,
            ClientGuardianHistory clientGuardianHistory) {
        if (clientGuardian.getDeletedState() || clientGuardian.isDisabled()) {
            clientGuardian.setDeletedState(false);
            clientGuardian.setDisabled(false);
            clientGuardian.setVersion(ClientManager.generateNewClientGuardianVersion(session));
            Set<ClientNotificationSetting> settings = client.getNotificationSettings();
            List<NotificationSettingItem> notificationSettings = new ArrayList<NotificationSettingItem>();
            for (ClientNotificationSetting.Predefined predefined : ClientNotificationSetting.Predefined.values()) {
                if (predefined.getValue().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                    continue;
                }
                notificationSettings.add(new NotificationSettingItem(predefined, settings));
            }

            ClientManager.attachNotifications(clientGuardian, notificationSettings);
            session.update(clientGuardian);
        }
    }
}
