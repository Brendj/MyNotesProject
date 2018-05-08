/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientCreatedFromType;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.model.ClientCount;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.generateNewClientGuardianVersion;

/**
 * User: shamil
 * Date: 21.11.14
 * Time: 17:31
 */
@Repository
public class ClientDao extends WritableJpaDao {
    public static final String UNKNOWN_PERSON_DATA = "Не заполнено";
    public static final String UNKNOWN_PERSON_SURNAME = "Представитель: %s";
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

    public int runGenerateGuardians(List idOfOrgs) throws Exception {
        int result = 0;
        Transaction transaction = null;
        Session session = RuntimeContext.getInstance().createPersistenceSession();
        try {

            //Получаем клиентов, у которых указан телефон или мейл
            String squery = "select c.idOfClient, c.idOfOrg, c.idOfClientGroup, c.mobile, c.email, c.contractId, c.notifyViaSMS, c.notifyViaEmail, c.notifyViaPUSH, c.ssoid, "
                    + "(select count(idofclientguardian) from cf_client_guardian cg where cg.idofchildren = c.idofclient) as guardCount "
                    + "from cf_clients c "
                    + "where "
                    + (idOfOrgs == null ? "" : "c.idOfOrg in :orgs and "
                    + "c.idOfClientGroup not between 1100000000 and 1100000080 and ")
                    + "((c.mobile is not null and c.mobile <> '') or (c.email is not null and c.email <> ''))";
            SQLQuery query = session.createSQLQuery(squery);
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
                            Client guardian = (Client)session.load(Client.class, cg.getIdOfGuardian());
                            if (guardian.getMobile() != null &&
                                    PhoneNumberCanonicalizator.canonicalize(guardian.getMobile()).equals(PhoneNumberCanonicalizator.canonicalize(ccInfo.getMobile()))) {
                                //хотя бы у одного опекуна найден номер мобильного ребенка - очищаем контактные данные у ребенка
                                mobileFound = true;
                                Client child = (Client) session.load(Client.class, ccInfo.getIdOfClient());
                                clearClientContacts(child, session);
                                session.save(child);
                                break;
                            }
                        }
                        if (!mobileFound) {
                            doGenerate = true;
                        }
                    }
                    if (doGenerate) {
                        createParentAndGuardianship(session, ccInfo);
                        result++;
                    }
                    session.flush();
                    transaction.commit();
                    transaction = null;
                } catch (Exception ignoreRecord) { }
                finally {
                    HibernateUtils.rollback(transaction, logger);
                }
            }
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    public List<ClientGuardian> getGuardians(Session session, Long idOfClient) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfClient));
        //criteria.add(Restrictions.eq("disabled", false));
        return criteria.list();
    }

    private void createParentAndGuardianship(Session session, ClientContactInfo clientInfo) throws Exception {
        //Создаем нового клиента-родителя
        FieldProcessor.Config createConfig = new ClientManager.ClientFieldConfig();
        Client child = (Client)session.load(Client.class, clientInfo.getIdOfClient());
        String surname = String.format(UNKNOWN_PERSON_SURNAME, child.getPerson().getSurnameAndFirstLetters());
        createConfig.setValue(ClientManager.FieldId.SURNAME, surname);
        createConfig.setValue(ClientManager.FieldId.NAME, UNKNOWN_PERSON_DATA);
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

        String dateCreate = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));

        Client clientId = ClientManager.registerClientTransactionFree(clientInfo.getIdOfOrg(),
                (ClientManager.ClientFieldConfig) createConfig, false, session, String.format(COMMENT_AUTO_CREATE, dateCreate));

        //Создаем опекунскую связь
        Long version = generateNewClientGuardianVersion(session);
        ClientManager.addGuardianByClient(session, clientInfo.getIdOfClient(), clientId.getIdOfClient(), version, false, null, null,
                ClientCreatedFromType.DEFAULT);

        //Очищаем данные клиента (ребенка)
        Client client = (Client)session.load(Client.class, clientInfo.getIdOfClient());
        clearClientContacts(client, session);
        session.update(client);
    }

    private void clearClientContacts(Client client, Session session) throws Exception {
        client.setMobile("");
        client.setEmail("");
        client.setNotifyViaSMS(false);
        client.setNotifyViaPUSH(false);
        client.setNotifyViaEmail(false);
        client.setSsoid("");
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
        client.setClientRegistryVersion(clientRegistryVersion);
    }
}
