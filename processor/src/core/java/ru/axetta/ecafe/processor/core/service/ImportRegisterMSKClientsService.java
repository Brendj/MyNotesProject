/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.nsi.ClientMskNSIService;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 17.12.12
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
@Primary
@Component("importRegisterMSKClientsService")
@Scope("singleton")
public class ImportRegisterMSKClientsService implements ImportClientRegisterService {

    public static final int CREATE_OPERATION = 1;
    public static final int DELETE_OPERATION = 2;
    public static final int MODIFY_OPERATION = 3;
    public static final int MOVE_OPERATION = 4;

    public static final long TIME_DELTA_PER_REQUEST = 60*60*1000; //1 час

    private static final int MAX_CLIENTS_PER_TRANSACTION = 50;
    @PersistenceContext(unitName = "processorPU")
    protected EntityManager em;

    @Autowired
    ClientMskNSIService nsiService;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterMSKClientsService.class);
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final String ORG_SYNC_MARKER = "СИНХРОНИЗАЦИЯ_РЕЕСТРЫ";
    private static final long MILLISECONDS_IN_DAY = 86400000L;
    private static final int MAX_THREADS = 10;


    /*@PostConstruct
    private void clearOrgSyncsRegistryTable() {
        if (RuntimeContext.getInstance().isMainNode()) {
            DAOService.getInstance().clearOrgSyncsRegistryTable();
        }
    }*/

    protected RegistryChange getRegistryChangeClassInstance() {
        return new RegistryChange();
    }

    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_AUTOSYNC_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_MSK_NSI_AUTOSYNC_ON, "" + (on ? "1" : "0"));
    }


    private void setLastUpdateDate(Date date) {
        RuntimeContext.getInstance()
                .setOptionValueWithSave(Option.OPTION_MSK_NSI_AUTOSYNC_UPD_TIME, dateFormat.format(date));
    }


    private Date getLastUpdateDate() {
        try {
            String d = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSK_NSI_AUTOSYNC_UPD_TIME);
            if (d == null || d.length() < 1) {
                return new Date(0);
            }
            return dateFormat.parse(d);
        } catch (Exception e) {
            logError("Failed to parse date from options", e, null);
        }
        return new Date(0);
    }

    public StringBuffer runSyncForOrg(long idOfOrg, boolean performChanges) throws Exception {
        Org org = DAOService.getInstance().getOrg(idOfOrg);
        if (org.getTag() == null || !org.getTag().toUpperCase().contains(ORG_SYNC_MARKER)) {
            throw new Exception(
                    "У организации " + idOfOrg + " не установлен тэг синхронизации с Реестрами: " + ORG_SYNC_MARKER);
        }

        StringBuffer logBuffer = new StringBuffer();
        return RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class)
                .syncClientsWithRegistry(idOfOrg, performChanges, logBuffer, true);
    }


    public class WorkerThread implements Runnable {

        private String command;
        private List<Org> orgs;

        public WorkerThread(String s) {
            this.command = s;
        }

        @Override
        public void run() {
            processCommand();
        }

        public List<Org> getOrgs() {
            return orgs;
        }

        public WorkerThread setOrgs(List<Org> orgs) {
            this.orgs = orgs;
            return this;
        }

        private void processCommand() {
            try {
                int maxAttempts = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_MSK_NSI_MAX_ATTEMPTS);
                for (Org org : orgs) {
                    if (org.getTag() == null || !org.getTag().toUpperCase().contains(ORG_SYNC_MARKER)) {
                        continue;
                    }
                    int attempt = 0;
                    while (attempt < maxAttempts) {
                        try {
                            RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class)
                                    .syncClientsWithRegistry(org.getIdOfOrg(), true, null, false);
                        } catch (SocketTimeoutException ignored) {
                        } catch (Exception e) {
                            logError("Ошибка при синхронизации с Реестрами для организации: " + org.getIdOfOrg(), e,
                                    null);
                            break;
                        } finally {
                            attempt++;
                        }
                    }
                    if (attempt >= maxAttempts) {
                        logError("Неудалось подключиться к сервису, превышено максимальное количество попыток ("
                                + maxAttempts + ")", null, null);
                    }
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return this.command;
        }
    }

    protected List<List<Org>> buildOrgsPack(List<Org> orgs, int threadsCount) {
        List<List<Org>> result = new ArrayList<List<Org>>();

        int l = 0;
        int i = 0;
        Org o = orgs.get(i);

        while (o != null) {
            List<Org> list = null;
            if (result.size() > l) {
                list = result.get(l);
            } else {
                list = new ArrayList<Org>();
                result.add(list);
            }
            list.add(o);

            l++;
            if (l >= threadsCount) {
                l = 0;
            }
            i++;

            if (orgs.size() > i) {
                o = orgs.get(i);
            } else {
                o = null;
            }
        }

        return result;
    }

    public void run() throws IOException {
        if (!RuntimeContext.getInstance().isMainNode() || !RuntimeContext.RegistryType.isMsk()) {
            return;
        }

        RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class).checkRegistryChangesValidity();

        if (!isOn()) {
            return;
        }
        List<Org> orgs = DAOService.getInstance().getOrderedSynchOrgsList();
        List<List<Org>> orgsPack = buildOrgsPack(orgs, MAX_THREADS);

        log("Start import register with " + MAX_THREADS + " threads", null);
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        for (int i = 0; i < orgsPack.size(); i++) {
            List<Org> pack = orgsPack.get(i);
            log(String.format("Create thread %s of %s with %s orgs", i + 1, orgsPack.size(), pack.size()), null);
            Runnable worker = new WorkerThread("" + i).setOrgs(pack);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        setLastUpdateDate(new Date(System.currentTimeMillis()));
        log("Finished import register", null);
    }

    /*public void prevRun() throws IOException {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            return;
        }
        List<Org> orgs = DAOService.getInstance().getOrderedSynchOrgsList();
        RuntimeContext.getAppContext().getBean("importRegisterClientsService", ImportRegisterClientsService.class)
                .checkRegistryChangesValidity();

        int maxAttempts = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_MSK_NSI_MAX_ATTEMPTS);
        for (Org org : orgs) {
            if (org.getTag() == null || !org.getTag().toUpperCase().contains(ORG_SYNC_MARKER)) {
                continue;
            }
            int attempt = 0;
            while (attempt < maxAttempts) {
                try {
                    RuntimeContext.getAppContext().getBean("importRegisterClientsService", ImportRegisterClientsService.class)
                            .syncClientsWithRegistry(org.getIdOfOrg(), true, null, false);
                    break;
                } catch (SocketTimeoutException ste) {
                } catch (Exception e) {
                    logError("Ошибка при синхронизации с Реестрами для организации: " + org.getIdOfOrg(), e, null);
                    break;
                } finally {
                    attempt++;
                }
            }
            if (attempt >= maxAttempts) {
                logError("Неудалось подключиться к сервису, превышено максимальное количество попыток (" + maxAttempts
                        + ")", null, null);
            }
        }
        //  Если была хотя бы одна неудачная загрузка данных с сервиса, время последный синхронизации не обновляем!
        //if (allOperationsAreFinished) {
        setLastUpdateDate(new Date(System.currentTimeMillis()));
        //}
    }*/

    @Transactional
    public void checkRegistryChangesValidity() {
        long minCreateDate =
                RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_MSK_NSI_REGISTRY_CHANGE_DAYS_TIMEOUT)
                        * MILLISECONDS_IN_DAY;
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis() - minCreateDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Session session = (Session) em.getDelegate();
        Query q = session.createSQLQuery(
                "LOCK TABLE cf_registrychange_guardians, cf_registrychange, cf_registrychange_errors IN SHARE MODE; "
                + "CREATE TEMP TABLE cf_registrychange_guardians_backup ON COMMIT DROP AS "
                + "      SELECT * FROM cf_registrychange_guardians WHERE createddate >=:minCreateDate ; "
                + "CREATE TEMP TABLE cf_registrychange_backup ON COMMIT DROP AS "
                + "      SELECT * FROM cf_registrychange WHERE createdate >=:minCreateDate ; "
                + "CREATE TEMP TABLE cf_registrychange_errors_backup ON COMMIT DROP AS "
                + "      SELECT * FROM cf_registrychange_errors WHERE createdate >=:minCreateDate ; "
                + "TRUNCATE cf_registrychange_guardians, cf_registrychange, cf_registrychange_errors; "
                + "INSERT INTO cf_registrychange SELECT * FROM cf_registrychange_backup; "
                + "INSERT INTO cf_registrychange_guardians SELECT * FROM cf_registrychange_guardians_backup; "
                + "INSERT INTO cf_registrychange_errors SELECT * FROM cf_registrychange_errors_backup; ");
        q.setLong("minCreateDate", cal.getTimeInMillis());
        q.executeUpdate();
    }

    public List <Client> findClientsWithoutPredefinedForOrgAndFriendly (Org organization) throws Exception {
        List <Org> orgs = DAOUtils.findFriendlyOrgs (em, organization);
        //return findClientsForOrgAndFriendly (em, organization, orgs);

        String orgsClause = " where (client.org = :org0 ";
        for (int i=0; i < orgs.size(); i++) {
            if (orgsClause.length() > 0) {
                orgsClause += " or ";
            }
            orgsClause += "client.org = :org" + (i + 1);
        }
        orgsClause += ") " + " and (not (client.idOfClientGroup >= " +
                ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " and client.idOfClientGroup < " +
                ClientGroup.Predefined.CLIENT_LEAVING.getValue() + ") or client.idOfClientGroup is null)";

        javax.persistence.Query query = em.createQuery(
                "from Client client " + orgsClause);
        query.setParameter("org0", organization);
        for (int i=0; i < orgs.size(); i++) {
            query.setParameter("org" + (i + 1), orgs.get(i));
        }
        if (query.getResultList().isEmpty()) return Collections.emptyList();
        return query.getResultList();
    }

    public List<Client> findClientsByMeshGuids(List<String> guids) {
        if(CollectionUtils.isEmpty(guids)){
            return new ArrayList<Client>();
        }
        javax.persistence.Query q = em.createQuery("from Client where meshGUID in :guids");
        q.setParameter("guids", guids);
        List<Client> result = q.getResultList();
        return result != null ? result : new ArrayList<Client>();
    }

    public List<Client> findClientsByNSIGuids(List<String> guids) {
        if(CollectionUtils.isEmpty(guids)){
            return new ArrayList<Client>();
        }
        javax.persistence.Query q = em.createQuery("from Client where clientGUID in :guids");
        q.setParameter("guids", guids);
        List<Client> result = q.getResultList();
        return result != null ? result : new ArrayList<Client>();
    }

    protected String getPupilGuid(String guid) {
        return guid;
    }

    protected String getClientGuid(Client client) {
        return client.getClientGUID();
    }

    protected Boolean belongToProperGroup(Client cl) {
        return cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup() >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                && cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup() < ClientGroup.Predefined.CLIENT_LEAVING.getValue();
    }

    protected Boolean isProperGroup(ClientGroup currGroup) {
        return currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup() >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                && currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup() < ClientGroup.Predefined.CLIENT_LEAVING.getValue();
    }

    @Transactional
    public void saveClients(String synchDate, String date, long ts, Org org, List<ExpandedPupilInfo> pupils,
            StringBuffer logBuffer) throws Exception {
        saveClients(synchDate, date, ts, org, pupils, logBuffer, true);
    }

    @Transactional
    public void saveClients(String synchDate, String date, long ts, Org org, List<ExpandedPupilInfo> pupils,
            StringBuffer logBuffer, boolean deleteClientsIfNotFound) throws Exception {
        log(synchDate + "Начато сохранение списка клиентов для " + org.getOfficialName() + " в БД", logBuffer);

        //  Открываем сессию и загружаем клиентов, которые сейчас находятся в БД
        Session session = (Session) em.getDelegate();
        List<Org> orgsList = DAOUtils.findFriendlyOrgs(em, org);   //  Текущая организация и дружественные ей

        List<String> pupilsMeshGuidList = new LinkedList<>();
        for (ExpandedPupilInfo pupil : pupils) {
            if(StringUtils.isNotEmpty(pupil.getMeshGUID())) {
                pupilsMeshGuidList.add(getPupilGuid(pupil.getMeshGUID()));
            }
        }

        List<String> pupilsNSIGuidList = new LinkedList<>();
        for (ExpandedPupilInfo pupil : pupils) {
            if(StringUtils.isNotEmpty(pupil.getMeshGUID())) {
                pupilsNSIGuidList.add(getPupilGuid(pupil.getGuid()));
            }
        }

        List<Client> findByGuidsList = findClientsByMeshGuids(pupilsMeshGuidList);
        Map<String, Client> meshGuidMap = new HashMap<>();
        for(Client client : findByGuidsList){
            meshGuidMap.put(client.getMeshGUID(), client);
        }

        findByGuidsList = findClientsByNSIGuids(pupilsNSIGuidList);
        Map<String, Client> nsiGuidMap = new HashMap<>();
        for(Client client : findByGuidsList){
            nsiGuidMap.put(client.getClientGUID(), client);
        }

        //  Если используется старый метод полной загрузки контенгента школы, то проверяем каждого ученика в отдельности на его
        //  наличие в школе. Иначе - смотрим флаг удалено/не удалено и в зависимости от этого помещаем ученика в удаленные
        /*
        List<Client> currentClients = findClientsWithoutPredefinedForOrgAndFriendly(org);
        if (deleteClientsIfNotFound) { // Заглушено в рамках логики функционала сверки с МЭШ.Контингент
            //  Находим только удаления и подсчитываем их, если их количество больще чем ограничение, то прекращаем обновление школы и
            //  отправляем уведомление на email
            List<Client> clientsToRemove = new ArrayList<Client>();
            for (Client dbClient : currentClients) {
                boolean found = false;
                for (ExpandedPupilInfo pupil : pupils) {
                    if (!StringUtils.isEmpty(getPupilGuid(emptyIfNull(pupil.getGuid()))) && getClientGuid(dbClient) != null && getPupilGuid(pupil.getGuid())
                            .equals(getClientGuid(dbClient))) {
                        found = true;
                        break;
                    }
                }
                try {
                    ClientGroup currGroup = dbClient.getClientGroup();
                    if (currGroup != null && isProperGroup(currGroup)) {
                        break;
                    }
                    //  Если клиент из Реестров не найден используя GUID из ИС ПП и группа у него еще не "Отчисленные", "Удаленные"
                    //  увеличиваем количество клиентов, подлежащих удалению
                    Long currGroupId =
                            currGroup == null ? null : currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
                    if(currGroupId != null &&
                       (currGroupId.equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue()) ||
                        currGroupId.equals(ClientGroup.Predefined.CLIENT_DELETED.getValue()))) {
                        continue;
                    }
                    if (emptyIfNull(getClientGuid(dbClient)).equals("") || !found) {
                        log(synchDate + "Удаление " +
                                emptyIfNull(getClientGuid(dbClient)) + ", " + emptyIfNull(
                                dbClient.getPerson().getSurname()) + " " +
                                emptyIfNull(dbClient.getPerson().getFirstName()) + " " + emptyIfNull(
                                dbClient.getPerson().getSecondName()) + ", " +
                                emptyIfNull(dbClient.getClientGroup() == null ? ""
                                        : dbClient.getClientGroup().getGroupName()), logBuffer);
                        addClientChange(ts, org.getIdOfOrg(), dbClient, DELETE_OPERATION,
                                RegistryChange.FULL_COMPARISON, false);
                    }
                } catch (Exception e) {
                    logError("Failed to delete client " + dbClient, e, logBuffer);
                }
            }
        }*/

        //  Проходим по ответу от Реестров и анализируем надо ли обновлять его или нет
        for (ExpandedPupilInfo pupil : pupils) {
            if (pupil.deleted) {
                Client dbClient = meshGuidMap.get(emptyIfNull(pupil.getMeshGUID()));
                if (dbClient == null ) {
                    dbClient = nsiGuidMap.get(emptyIfNull(pupil.getGuid()));
                    if(dbClient != null && StringUtils.isNotEmpty(dbClient.getMeshGUID())){
                        continue;
                    }
                }
                if (dbClient == null || dbClient.isDeletedOrLeaving()) {
                    continue;
                }
                log(synchDate + "Удаление " + emptyIfNull(dbClient.getClientGUID()) + ", " + emptyIfNull(
                        dbClient.getPerson().getSurname()) + " " + emptyIfNull(dbClient.getPerson().getFirstName())
                        + " " + emptyIfNull(dbClient.getPerson().getSecondName()) + ", " + emptyIfNull(
                        dbClient.getClientGroup() == null ? "" : dbClient.getClientGroup().getGroupName()), logBuffer);
                addClientChange(ts, org.getIdOfOrg(), dbClient, DELETE_OPERATION, RegistryChange.FULL_COMPARISON,
                        false);
            } else {
                FieldProcessor.Config fieldConfig;
                boolean updateClient = false;
                Client cl = meshGuidMap.get(getPupilGuid(emptyIfNull(pupil.getMeshGUID())));
                if (cl == null) {
                    cl = nsiGuidMap.get(emptyIfNull(pupil.getGuid()));
                }
                if (cl == null) {
                    fieldConfig = new ClientManager.ClientFieldConfig();
                } else {
                    if (cl.getClientGroup() != null && belongToProperGroup(cl)) {
                        continue;
                    }
                    fieldConfig = new ClientManager.ClientFieldConfigForUpdate();
                }
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.MESH_GUID,
                        getPupilGuid(pupil.getMeshGUID()), cl == null ? null : cl.getMeshGUID(), updateClient);
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.CLIENT_GUID,
                            getPupilGuid(pupil.getGuid()), cl == null ? null : getClientGuid(cl), updateClient);
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.SURNAME, pupil.getFamilyName(),
                        cl == null ? null : cl.getPerson().getSurname(), updateClient);
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.NAME, pupil.getFirstName(),
                        cl == null ? null : cl.getPerson().getFirstName(), updateClient);
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.SECONDNAME, pupil.getSecondName(),
                        cl == null ? null : cl.getPerson().getSecondName(), updateClient);
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.GENDER,
                        pupil.getGender() == null ? "Мужской" : pupil.getGender(),
                        cl == null || cl.getGender() == null ? "Мужской" : cl.getGender() == 0 ? "Женский" : "Мужской",
                        updateClient);

                DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.BIRTH_DATE, pupil.getBirthDate(),
                        cl == null ? null : cl.getBirthDate() == null ? null : timeFormat.format(cl.getBirthDate()),
                        updateClient);

                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.AGE_TYPE_GROUP,
                        pupil.getAgeTypeGroup(), cl == null ? null : cl.getAgeTypeGroup(), updateClient);

                if (pupil.getGroup() != null) {
                    updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.GROUP, pupil.getGroup(),
                            cl == null || cl.getClientGroup() == null ? null : cl.getClientGroup().getGroupName(),
                            updateClient);
                } else {
                    //  Если группа у клиента не указана, то перемещаем его в Другие
                    updateClient = doUpdateClientGroup(fieldConfig, cl, updateClient);
                }
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.PARALLEL, pupil.getParallel(),
                        cl == null ? null : cl.getParallel(), updateClient);
                //  Проверяем организацию и дружественные ей - если клиент был переведен из другого ОУ, то перемещаем его
                boolean crossFound = false;
                if (cl != null) {
                    if (org.getIdOfOrg().equals(cl.getOrg().getIdOfOrg())) {
                        crossFound = true;
                    } else {
                        for (Org o : orgsList) {
                            if (o.getIdOfOrg().equals(cl.getOrg().getIdOfOrg())) {
                                crossFound = true;
                                break;
                            }
                        }
                    }
                }
                try {
                    if (cl != null && !cl.getOrg().getOrgIdFromNsi().equals(pupil.orgId) && !crossFound) {
                        log(synchDate + "Перевод " + emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(
                                cl.getPerson() == null ? "" : cl.getPerson().getSurname()) + " " + emptyIfNull(
                                cl.getPerson() == null ? "" : cl.getPerson().getFirstName()) + " " + emptyIfNull(
                                cl.getPerson() == null ? "" : cl.getPerson().getSecondName()) + ", " + emptyIfNull(
                                cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName()) + " из школы " + cl.getOrg().getIdOfOrg() + " в школу " + org.getIdOfOrg(), logBuffer);
                        addClientChange(ts, org.getIdOfOrg(), org.getIdOfOrg(), fieldConfig, cl, MOVE_OPERATION,
                                RegistryChange.FULL_COMPARISON, false);
                        continue;
                    }
                } catch (Exception e) {
                    logError(String.format(
                            "Ошибка при определении признака перевода клиента из ОО в ОО. ID OO клиента %d, ID OO в НСИ-3 в записи разногласий: %d",
                            cl.getOrg().getIdOfOrg(), pupil.orgId
                    ), e, logBuffer);
                }
                if (!updateClient) {
                    continue;
                }

                doClientUpdate(fieldConfig, ClientManager.FieldId.GUARDIANS_COUNT, pupil.getGuardiansCount(),
                        cl == null ? null : cl.getGuardiansCount(), updateClient);

                if (!pupil.getGuardianInfoList().isEmpty()) {
                    doClientUpdate(fieldConfig, ClientManager.FieldId.GUARDIANS_COUNT_LIST,
                            pupil.getGuardianInfoList());
                }

                try {
                    //  Если клиента по GUID найти не удалось, это значит что он новый - добавляем его
                    if (cl == null) {
                        try {
                            log(synchDate + "Добавление " + pupil.getGuid() + ", " + pupil.getFamilyName() + " " + pupil
                                    .getFirstName() + " " + pupil.getSecondName() + ", " + pupil.getGroup(), logBuffer);
                            addClientChange(ts, org.getIdOfOrg(), fieldConfig, CREATE_OPERATION,
                                    RegistryChange.FULL_COMPARISON, false);
                        } catch (Exception e) {
                            // Не раскомментировать, очень много исключений будет из-за дублирования клиентов
                            logError("Ошибка добавления клиента", e, logBuffer);
                        }
                        //  Иначе - обновляем клиента в БД
                    } else {
                        log(synchDate + "Изменение " + emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(
                                cl.getPerson().getSurname()) + " " + emptyIfNull(cl.getPerson().getFirstName()) + " "
                                + emptyIfNull(cl.getPerson().getSecondName()) + ", " + emptyIfNull(
                                cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName()) + " на "
                                + emptyIfNull(pupil.getGuid()) + ", " + emptyIfNull(pupil.getFamilyName()) + " "
                                + emptyIfNull(pupil.getFirstName()) + " " + emptyIfNull(pupil.getSecondName()) + ", "
                                + emptyIfNull(pupil.getGroup()), logBuffer);
                        addClientChange(ts, org.getIdOfOrg(), fieldConfig, cl, MODIFY_OPERATION,
                                RegistryChange.FULL_COMPARISON, false);
                    }
                } catch (Exception e) {
                    logError("Failed to add client for " + org.getIdOfOrg() + " org", e, logBuffer);
                }
            }
        }
        log(synchDate + "Синхронизация завершена для " + org.getOfficialName(), logBuffer);
    }

    protected boolean doUpdateClientGroup(FieldProcessor.Config fieldConfig, Client cl, boolean updateClient) throws Exception {
        return doClientUpdate(fieldConfig, ClientManager.FieldId.GROUP,
                ClientGroup.Predefined.CLIENT_OTHERS.getNameOfGroup(),
                cl == null || cl.getClientGroup() == null ? null : cl.getClientGroup().getGroupName(),
                updateClient);
    }

    public String getCategoriesString(String categoriesDSZN, String clientCategories,
            Map<Long, CategoryDiscount> categoryMap, Map<Integer, CategoryDiscountDSZN> categoryDSZNMap) {
        List<Long> categoriesList = new ArrayList<Long>();
        for(String c : clientCategories.split(",")) {
            if(StringUtils.isNotEmpty(c)) {
                categoriesList.add(Long.valueOf(c));
            }
        }
        List<Integer> categoriesDSZNList = new ArrayList<Integer>();
        for(String c : categoriesDSZN.split(",")) {
            if(StringUtils.isNotEmpty(c)) {
                categoriesDSZNList.add(Integer.valueOf(c));
            }
        }

        Set<Long> resultCategories = new TreeSet<Long>();
        for(Long c : categoriesList) {
            if(categoryMap.get(c) != null && !(categoryMap.get(c).getCategoriesDiscountDSZN().size() > 0)) {
                resultCategories.add(c);
            }
        }

        for(Integer c : categoriesDSZNList) {
            if(categoryDSZNMap.get(c) != null && categoryDSZNMap.get(c).getCategoryDiscount() != null) {
                resultCategories.add(categoryDSZNMap.get(c).getCategoryDiscount().getIdOfCategoryDiscount());
            }
        }

        return StringUtils.join(resultCategories, ",");
    }

    public static boolean doCategoriesUpdate(String newCategories, String oldCategories) {
        if(StringUtils.isEmpty(newCategories) && StringUtils.isEmpty(oldCategories)) {
            return false;
        }
        Set<String> newCategoriesSet = new HashSet<String>(Arrays.asList(newCategories.split(",")));
        Set<String> oldCategoriesSet = new HashSet<String>(Arrays.asList(oldCategories.split(",")));
        return !newCategoriesSet.equals(oldCategoriesSet);
    }

    @SuppressWarnings("unchecked")
    public static Map<Long, CategoryDiscount> getCategoriesMap(Session session) {
        Map<Long, CategoryDiscount> result = new HashMap<Long, CategoryDiscount>();
        Criteria criteria = session.createCriteria(CategoryDiscount.class);
        List<CategoryDiscount> list = criteria.list();
        for(CategoryDiscount categoryDiscount : list) {
            result.put(categoryDiscount.getIdOfCategoryDiscount(), categoryDiscount);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, CategoryDiscountDSZN> getCategoriesDSZNMap(Session session) {
        Map<Integer, CategoryDiscountDSZN> result = new HashMap<Integer, CategoryDiscountDSZN>();
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        List<CategoryDiscountDSZN> list = criteria.list();
        for(CategoryDiscountDSZN categoryDiscountDSZN : list) {
            result.put(categoryDiscountDSZN.getCode(), categoryDiscountDSZN);
        }
        return result;
    }

    public static long getLastUncommitedChange(EntityManager em) {
        long maxTs = 0L;
        long lastTs = 0L;
        Session session = (Session) em.getDelegate();
        Query q = session.createSQLQuery("select max(rc1.createdate), 'last' " + "from CF_RegistryChange rc1 "
                + "where rc1.applied=true and rc1.type=:type " + "union all " + "select max(rc1.createdate), 'max' "
                + "from CF_RegistryChange rc1 " + "where rc1.type=:type");
        q.setParameter("type", RegistryChange.CHANGES_UPDATE);
        List resultList = q.list();
        for (Object obj : resultList) {
            Object[] dat = (Object[]) obj;
            if (dat[0] == null) {
                continue;
            }
            Long value = ((BigInteger) dat[0]).longValue();
            String type = (String) dat[1];
            if (type.equals("max")) {
                maxTs = value;
            } else if (type.equals("last")) {
                lastTs = value;
            }
        }
        if (maxTs > lastTs) {
            Calendar target = new GregorianCalendar();
            target.setTimeInMillis(maxTs);
            target.set(Calendar.HOUR, 0);
            target.set(Calendar.MINUTE, 0);
            target.set(Calendar.SECOND, 0);
            target.set(Calendar.MILLISECOND, 0);

            Calendar now = new GregorianCalendar();
            now.setTimeInMillis(System.currentTimeMillis());
            now.set(Calendar.HOUR, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            if (target.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
                return maxTs;
            }
        }
        return System.currentTimeMillis();
    }


    private void addClientChange(long ts, long idOfOrg, FieldProcessor.Config fieldConfig, int operation, int type, Boolean checkBenefits)
            throws Exception {
        //  ДОБАВИТЬ ЗАПИСЬ ОБ ИЗМЕНЕНИИ ПОЛЬЗОВАТЕЛЯ И УКАЗАТЬ СООТВЕТСТВУЮЩУЮ ОПЕРАЦИЮ
        addClientChange(ts, idOfOrg, fieldConfig, null, operation, type, checkBenefits);
    }


    private void addClientChange(long ts, long idOfOrg, FieldProcessor.Config fieldConfig, Client currentClient,
            int operation, int type, Boolean checkBenefits) throws Exception {
        addClientChange(ts, idOfOrg, null, fieldConfig, currentClient, operation, type, checkBenefits);
    }

    public void addClientChange(long ts, long idOfOrg, Long idOfMigrateOrg,
            FieldProcessor.Config fieldConfig, Client currentClient, int operation, int type, Boolean checkBenefits) throws Exception {
        addClientChange(ts, idOfOrg, idOfMigrateOrg, fieldConfig, currentClient, operation, type, null, checkBenefits);
    }

    public boolean isRegistryChangeExist(String notificationId, Client client, int operation, Session session) {
        Query q = session.createSQLQuery("SELECT 1 " + "FROM cf_registrychange "
                + "where notificationId=:notificationId and operation=:operation");
        q.setParameter("notificationId", notificationId);
        q.setParameter("operation", operation);
        List res = q.list();
        return CollectionUtils.isNotEmpty(res);
    }

    public void addClientChange(long ts, long idOfOrg, Long idOfMigrateOrg,
            FieldProcessor.Config fieldConfig, Client currentClient, int operation, int type, String notificationId, Boolean checkBenefits)
            throws Exception {
        //  ДОБАВИТЬ ЗАПИСЬ ОБ ИЗМЕНЕНИИ ПОЛЬЗОВАТЕЛЯ И УКАЗАТЬ СООТВЕТСТВУЮЩУЮ ОПЕРАЦИЮ
        Session sess = (Session) em.getDelegate();
        if (currentClient != null) {
            currentClient = em.merge(currentClient);
        }
        if (type == RegistryChange.CHANGES_UPDATE && !StringUtils.isBlank(notificationId) &&
                isRegistryChangeExist(notificationId, currentClient, operation, sess)) {
            return;
        }

        String meshGuid = fieldConfig.getValue(ClientManager.FieldId.MESH_GUID);
        String clientGuid = emptyIfNull(fieldConfig.getValue(ClientManager.FieldId.CLIENT_GUID));
        String name = trim(fieldConfig.getValue(ClientManager.FieldId.NAME), 64, clientGuid, "Имя ученика");
        String secondname = trim(fieldConfig.getValue(ClientManager.FieldId.SECONDNAME), 128, clientGuid, "Отчество ученика");
        String surname = trim(fieldConfig.getValue(ClientManager.FieldId.SURNAME), 128, clientGuid, "Фамилия ученика");
        String registryGroupName = trim(fieldConfig.getValue(ClientManager.FieldId.GROUP), 256, clientGuid, "Наименование группы");
        String clientGender = trim(fieldConfig.getValue(ClientManager.FieldId.GENDER), 64, clientGuid, "Пол");
        String clientBirthDate = trim(fieldConfig.getValue(ClientManager.FieldId.BIRTH_DATE), 64, clientGuid, "Дата рождения");
        String guardiansCount = trim(fieldConfig.getValue(ClientManager.FieldId.GUARDIANS_COUNT), 64, clientGuid, "Количество представителей");
        List<GuardianInfo> guardianInfoList = fieldConfig.getValueList(ClientManager.FieldId.GUARDIANS_COUNT_LIST);
        String ageTypeGroup = trim(fieldConfig.getValue(ClientManager.FieldId.AGE_TYPE_GROUP), 128, clientGuid, "Тип возрастной группы");
        String parallel = trim(fieldConfig.getValue(ClientManager.FieldId.PARALLEL), 255, clientGuid, "Параллель");

        RegistryChange ch = getRegistryChangeClassInstance();
        ch.setMeshGUID(meshGuid);
        ch.setClientGUID(clientGuid);
        ch.setFirstName(name);
        ch.setSecondName(secondname);
        ch.setSurname(surname);
        ch.setGroupName(registryGroupName);
        ch.setParallel(parallel);
        ch.setIdOfClient(currentClient == null ? null : currentClient.getIdOfClient());
        ch.setIdOfOrg(idOfOrg);
        ch.setOperation(operation);
        ch.setCreateDate(ts);
        ch.setApplied(false);
        ch.setType(type);
        ch.setNotificationId(notificationId);
        ch.setAgeTypeGroup(ageTypeGroup);

        if (clientGender != null) {
            if (clientGender.equals("Женский")) {
                ch.setGender(0);
            }
            if (clientGender.equals("Мужской")) {
                ch.setGender(1);
            }
        }

        ch.setCheckBenefits(checkBenefits);

        if (operation == CREATE_OPERATION) {
            if (!StringUtils.isEmpty(guardiansCount)) {
                ch.setGuardiansCount(Integer.valueOf(guardiansCount));
            }

            if (guardianInfoList != null && !guardianInfoList.isEmpty()) {
                Set<RegistryChangeGuardians> registryChangeGuardiansSet = new HashSet<RegistryChangeGuardians>();

                for (GuardianInfo guardianInfo : guardianInfoList) {
                    RegistryChangeGuardians guardians = new RegistryChangeGuardians(guardianInfo.getFamilyName(),
                            guardianInfo.getFirstName(), guardianInfo.getSecondName(), guardianInfo.getRelationship(),
                            guardianInfo.getPhoneNumber(), guardianInfo.getEmailAddress(), new Date(), ch, false,
                            guardianInfo.getLegalRepresentative(), guardianInfo.getSsoid(), guardianInfo.getGuid());
                    registryChangeGuardiansSet.add(guardians);
                }
                ch.setRegistryChangeGuardiansSet(registryChangeGuardiansSet);
            }
        }

        if (clientBirthDate != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            Date date = format.parse(clientBirthDate);
            ch.setBirthDate(date.getTime());
    }

        if (operation == MOVE_OPERATION) {
            ch.setIdOfMigrateOrgFrom(currentClient.getOrg().getIdOfOrg());
            ch.setIdOfMigrateOrgTo(idOfMigrateOrg);
            ClientGroup currentGroup = currentClient.getClientGroup();
            if (currentGroup != null) {
                currentGroup = em.merge(currentGroup);
                ch.setGroupNameFrom(currentGroup.getGroupName());
            } else {
                ch.setGroupNameFrom("");
            }
            ch.setParallelFrom(currentClient.getParallel());
            ch.setGenderFrom(currentClient.getGender());
            if (currentClient.getBirthDate() != null) {
                ch.setBirthDateFrom(currentClient.getBirthDate().getTime());
            }
            ch.setAgeTypeGroupFrom(currentClient.getAgeTypeGroup());
        }
        if (operation == MODIFY_OPERATION) {
            ClientGroup currentGroup = currentClient.getClientGroup();
            if (currentGroup != null) {
                currentGroup = em.merge(currentGroup);
                ch.setGroupNameFrom(currentGroup.getGroupName());
            } else {
                ch.setGroupNameFrom("");
            }
            ch.setParallelFrom(currentClient.getParallel());
            ch.setFirstNameFrom(currentClient.getPerson().getFirstName());
            ch.setSecondNameFrom(currentClient.getPerson().getSecondName());
            ch.setSurnameFrom(currentClient.getPerson().getSurname());
            ch.setGenderFrom(currentClient.getGender());
            if (currentClient.getBirthDate() != null) {
                ch.setBirthDateFrom(currentClient.getBirthDate().getTime());
            }
            ch.setOldDiscounts(DiscountManager.getClientDiscountsAsString(currentClient));
            ch.setAgeTypeGroupFrom(currentClient.getAgeTypeGroup());
        }
        sess.save(ch);
    }

    private static Set<Long> getCategoriesByDSZNCodes(Session session, String clientBenefitDSZN, String oldDiscounts) {
        Set<Long> newDiscountsIds = new TreeSet<Long>();
        List<Long> oldDiscountsIds = new ArrayList<Long>();
        for(String o : oldDiscounts.split(",")) {
            if(StringUtils.isNotEmpty(o)) {
                oldDiscountsIds.add(Long.parseLong(o));
            }
        }
        if(oldDiscountsIds.size() > 0) {
            Collections.sort(oldDiscountsIds);
            Criteria criteria = session.createCriteria(CategoryDiscount.class);
            criteria.add(Restrictions.in("idOfCategoryDiscount", oldDiscountsIds));
            List<CategoryDiscount> list = criteria.list();
            for (CategoryDiscount categoryDiscount : list) {
                if (!(categoryDiscount.getCategoriesDiscountDSZN().size() > 0)) {
                    newDiscountsIds.add(categoryDiscount.getIdOfCategoryDiscount());
                }
            }
        }

        if(StringUtils.isNotEmpty(clientBenefitDSZN)) {
            List<Integer> benefitsList = new ArrayList<Integer>();
            for(String s : clientBenefitDSZN.split(",")) {
                if(StringUtils.isNotEmpty(s)) {
                    benefitsList.add(Integer.valueOf(s));
                }
            }
            Criteria criteria1 = session.createCriteria(CategoryDiscountDSZN.class);
            criteria1.add(Restrictions.in("code", benefitsList));
            criteria1.add(Restrictions.eq("deleted", false));
            List<CategoryDiscountDSZN> cdDSZN = criteria1.list();
            for(CategoryDiscountDSZN discountDSZN : cdDSZN) {
                if(discountDSZN.getCategoryDiscount() != null) {
                    newDiscountsIds.add(discountDSZN.getCategoryDiscount().getIdOfCategoryDiscount());
                }
            }
        }
        return newDiscountsIds;
    }

    protected static String trim(String source, int maxLen, String clientGuid, String fieldName) {
        if(source != null && !StringUtils.isBlank(source) && source.length() > maxLen) {
            String replace = source.substring(0, maxLen - 1);
            logger.error(String.format("ВНИМАНИЕ! %s (%s) ученика (%s) в Реестрах "
                    + "слишком длинное, будет применено ограничение на длинну поля "
                    + "%s (%s)", fieldName, source, clientGuid, fieldName, replace));
            return replace;
        }
        return source;
    }

    public void addClientChange(long ts, long idOfOrg, Client currentClient, int operation,
            int type, Boolean checkBenefits) throws Exception {
        addClientChange(ts, idOfOrg, currentClient, operation, type, null, checkBenefits);
    }

    public void addClientChange(long ts, long idOfOrg, Client currentClient, int operation,
            int type, String notificationId, Boolean checkBenefits) throws Exception {
        //  ДОБАВИТЬ ЗАПИСЬ ОБ УДАЛЕНИИ В БД
        Session sess = (Session) em.getDelegate();
        if (currentClient != null) {
            currentClient = em.merge(currentClient);
        }
        if (type == RegistryChange.CHANGES_UPDATE && !StringUtils.isBlank(notificationId) &&
                isRegistryChangeExist(notificationId, currentClient, operation, sess)) {
            return;
        }

        RegistryChange ch = getRegistryChangeClassInstance();
        ch.setClientGUID(emptyIfNull(currentClient.getClientGUID()));
        ch.setMeshGUID(currentClient.getMeshGUID());
        ch.setFirstName(currentClient.getPerson().getFirstName());
        ch.setSecondName(currentClient.getPerson().getSecondName());
        ch.setSurname(currentClient.getPerson().getSurname());
        ch.setGroupName(currentClient.getClientGroup() == null ? "" : currentClient.getClientGroup().getGroupName());
        ch.setParallel(currentClient.getParallel());
        ch.setIdOfClient(currentClient.getIdOfClient());
        ch.setIdOfOrg(idOfOrg);
        ch.setOperation(operation);
        ch.setType(type);
        ch.setCreateDate(ts);
        ch.setApplied(false);
        ch.setNotificationId(notificationId);
        ch.setGender(currentClient.getGender());
        if (currentClient.getBirthDate() != null) {
            ch.setBirthDate(currentClient.getBirthDate().getTime());
        }

        ch.setCheckBenefits(checkBenefits);
        ch.setAgeTypeGroup(currentClient.getAgeTypeGroup());
        sess.save(ch);
    }


    /*@Transactional
    public void parseClients(String synchDate, String date, Org org, List<ExpandedPupilInfo> pupils,
            boolean performChanges, StringBuffer logBuffer, boolean manualCheckout) throws Exception {
        log(synchDate + "Синхронизация списков начата для " + org.getOfficialName() + (performChanges ? ""
                : " РЕЖИМ БЕЗ ПРИМЕНЕНИЯ ИЗМЕНЕНИЙ"), logBuffer);


        //  Открываем сессию и загружаем клиентов, которые сейчас находятся в БД
        Session session = (Session) em.getDelegate();
        List<Client> currentClients = DAOUtils.findClientsForOrgAndFriendly(em, org);
        List<Org> orgsList = DAOUtils.findFriendlyOrgs(em, org);   //  Текущая организация и дружественные ей
        //orgsList.add(org);

        List<String> pupilsGuidList = new ArrayList<String>();
        for (ExpandedPupilInfo pupil : pupils) {
            if(pupil.getGuid() != null && !pupil.getGuid().isEmpty()) {
                pupilsGuidList.add(pupil.getGuid());
            }
        }

        List<Client> findByGuidsList = DAOUtils.findClientsByGuids(em, pupilsGuidList);
        Map<String, Client> guidMap = new HashMap<String, Client>();
        for(Client client : findByGuidsList){
            guidMap.put(client.getClientGUID(), client);
        }

        //  Находим только удаления и подсчитываем их, если их количество больще чем ограничение, то прекращаем обновление школы и
        //  отправляем уведомление на email
        List<Client> clientsToRemove = new ArrayList<Client>();
        for (Client dbClient : currentClients) {
            boolean found = false;
            for (ExpandedPupilInfo pupil : pupils) {
                if (pupil.getGuid() != null && dbClient.getClientGUID() != null && pupil.getGuid()
                        .equals(dbClient.getClientGUID())) {
                    found = true;
                    break;
                }
            }
            ClientGroup currGroup = dbClient.getClientGroup();
            //  Если клиент из Реестров не найден используя GUID из ИС ПП и группа у него еще не "Отчисленные", "Удаленные"
            //  увеличиваем количество клиентов, подлежих удалению
            Long currGroupId = currGroup == null ? null : currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
            if (!found && !emptyIfNull(dbClient.getClientGUID()).equals("") && currGroupId != null &&
                    !currGroupId.equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue()) &&
                    !currGroupId.equals(ClientGroup.Predefined.CLIENT_DELETED.getValue())) {
                clientsToRemove.add(dbClient);
            }
        }
        //log(synchDate + "Найдено " + (removedClientsCount) + " клиентов, подлженщих удалению");
        if (clientsToRemove.size() > MAX_CLIENTS_PER_TRANSACTION && !manualCheckout) {
            String text = "Внимание! Из Реестров поступило обновление " + pupils.size() + " клиентов для " + org
                    .getOfficialName() + " {" + org.getIdOfOrg()
                    + "}. В целях безопасности автоматическое обновление прекращено.";
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            if (runtimeContext != null) {
                try {
                    String address = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_SUPPORT_EMAIL);
                    String subject = "Синхронизация с Реестрами";
                    List<File> files = new ArrayList<File>();
                    runtimeContext.getPostman().postSupportEmail(address, subject, text, files);
                } catch (Exception e) {
                    logError("Ошибка при отправке уведомления", e, logBuffer);
                }
            }
            logError(text, null, logBuffer);
            return;
        }


        //  Удаляем найденных клиентов
        for (Client dbClient : clientsToRemove) {
            ClientGroup clientGroup = DAOUtils
                    .findClientGroupByGroupNameAndIdOfOrg(session, dbClient.getOrg().getIdOfOrg(),
                            ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
            if (clientGroup == null) {
                clientGroup = DAOUtils.createClientGroup(session, dbClient.getOrg().getIdOfOrg(),
                        ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
            }
            log(synchDate + "Удаление " +
                    emptyIfNull(dbClient.getClientGUID()) + ", " + emptyIfNull(dbClient.getPerson().getSurname()) + " "
                    +
                    emptyIfNull(dbClient.getPerson().getFirstName()) + " " + emptyIfNull(
                    dbClient.getPerson().getSecondName()) + ", " +
                    emptyIfNull(dbClient.getClientGroup() == null ? "" : dbClient.getClientGroup().getGroupName()),
                    logBuffer);
            if (performChanges) {
                dbClient.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                session.save(dbClient);
            }
        }

        //  Начинаем работать с поступившем списком клиентов - для начала, УДАЛЯЕМ
        /*for (Client dbClient : currentClients) {
            boolean found = false;
            for (ExpandedPupilInfo pupil : pupils) {
                if (pupil.getGuid() != null && dbClient.getClientGUID() != null && pupil.getGuid()
                        .equals(dbClient.getClientGUID())) {
                    found = true;
                    break;
                }
            }
            ClientGroup currGroup = dbClient.getClientGroup();
            //  Если клиент из Реестров не найден используя GUID из ИС ПП и группа у него еще не "Отчисленные", то заносим его в эту группу
            if (!found && !emptyIfNull(dbClient.getClientGUID()).equals("") && currGroup != null &&
                    !currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup()
                            .equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue())) {
                ClientGroup clientGroup = DAOUtils
                        .findClientGroupByGroupNameAndIdOfOrg(session, dbClient.getOrg().getIdOfOrg(),
                                ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                if (clientGroup == null) {
                    clientGroup = DAOUtils.createNewClientGroup(session, dbClient.getOrg().getIdOfOrg(),
                            ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                }
                log(synchDate + "Удаление " +
                        emptyIfNull(dbClient.getClientGUID()) + ", " + emptyIfNull(dbClient.getPerson().getSurname())
                        + " " +
                        emptyIfNull(dbClient.getPerson().getFirstName()) + " " + emptyIfNull(
                        dbClient.getPerson().getSecondName()) + ", " +
                        emptyIfNull(dbClient.getClientGroup().getGroupName()), logBuffer);
                if (performChanges) {
                    dbClient.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    session.save(dbClient);
                }
            }
        }

        //  Проходим по ответу от Реестров и анализируем надо ли обновлять его или нет
        for (ExpandedPupilInfo pupil : pupils) {
            FieldProcessor.Config fieldConfig;
            boolean updateClient = false;
            Client cl = guidMap.get(emptyIfNull(pupil.getGuid()));
            if (cl == null) {
                fieldConfig = new ClientManager.ClientFieldConfig();
            } else {
                fieldConfig = new ClientManager.ClientFieldConfigForUpdate();
            }

            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.CLIENT_GUID, pupil.getGuid(),
                    cl == null ? null : cl.getClientGUID(), updateClient);
            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.SURNAME, pupil.getFamilyName(),
                    cl == null ? null : cl.getPerson().getSurname(), updateClient);
            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.NAME, pupil.getFirstName(),
                    cl == null ? null : cl.getPerson().getFirstName(), updateClient);
            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.SECONDNAME, pupil.getSecondName(),
                    cl == null ? null : cl.getPerson().getSecondName(), updateClient);
            if (pupil.getGroup() != null) {
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.GROUP, pupil.getGroup(),
                        cl == null || cl.getClientGroup() == null ? null : cl.getClientGroup().getGroupName(),
                        updateClient);
            }
            //  Проверяем организацию и дружественные ей - если клиент был переведен из другого ОУ, то перемещаем его
            boolean guidFound = false;
            for (Org o : orgsList) {
                if (o.getGuid().equals(pupil.get)) {
                    guidFound = true;
                    break;
                }
            }
            if (cl != null && !cl.getOrg().getGuid().equals(pupil.getGuidOfOrg()) && !guidFound) {
                Org newOrg = DAOService.getInstance().getOrgByGuid(pupil.getGuidOfOrg());
                log(synchDate + "Перевод " + emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(
                        cl.getPerson().getSurname()) + " " +
                        emptyIfNull(cl.getPerson().getFirstName()) + " " + emptyIfNull(cl.getPerson().getSecondName())
                        + ", " +
                        emptyIfNull(cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName())
                        + " из школы " + cl.getOrg().getIdOfOrg() + " в школу " + newOrg.getIdOfOrg(), logBuffer);
                if (performChanges) {
                    cl.setOrg(newOrg);
                }
                updateClient = true;
            }


            if (!updateClient) {
                continue;
            }
            try {
                //  Если клиента по GUID найти не удалось, это значит что он новый - добавляем его
                if (cl == null) {
                    try {
                        log(synchDate + "Добавление " + pupil.getGuid() + ", " +
                                pupil.getFamilyName() + " " + pupil.getFirstName() + " " +
                                pupil.getSecondName() + ", " + pupil.getGroup(), logBuffer);
                        fieldConfig.setValue(ClientManager.FieldId.COMMENTS,
                                String.format(MskNSIService.COMMENT_AUTO_IMPORT, date));
                        if (performChanges) {
                            String dateCreate = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
                            ClientManager.registerClientTransactionFree(org.getIdOfOrg(),
                                    (ClientManager.ClientFieldConfig) fieldConfig, true, session, String.format(MskNSIService.COMMENT_AUTO_CREATE, dateCreate));
                        }
                    } catch (Exception e) {
                        // Не раскомментировать, очень много исключений будет из-за дублирования клиентов
                        logError("Ошибка добавления клиента", e, logBuffer);
                    }
                    //  Иначе - обновляем клиента в БД
                } else {
                    log(synchDate + "Изменение " +
                            emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(cl.getPerson().getSurname()) + " " +
                            emptyIfNull(cl.getPerson().getFirstName()) + " " + emptyIfNull(
                            cl.getPerson().getSecondName()) + ", " +
                            emptyIfNull(cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName()) + " на "
                            +
                            emptyIfNull(pupil.getGuid()) + ", " + emptyIfNull(pupil.getFamilyName()) + " "
                            + emptyIfNull(pupil.getFirstName()) + " " +
                            emptyIfNull(pupil.getSecondName()) + ", " + emptyIfNull(pupil.getGroup()), logBuffer);
                    if (performChanges) {
                        ClientManager
                                .modifyClientTransactionFree((ClientManager.ClientFieldConfigForUpdate) fieldConfig,
                                        org, String.format(MskNSIService.COMMENT_AUTO_MODIFY, date), cl, session, true);
                    }
                }
            } catch (Exception e) {
                logError("Failed to add client for " + org.getIdOfOrg() + " org", e, logBuffer);
            }
        }
        log(synchDate + "Синхронизация завершена для " + org.getOfficialName(), logBuffer);
    }*/

    public static class OrgRegistryGUIDInfo {
        Set<String> orgGuids;
        String guidInfo;
        private Set<String> orgEkisIds;
        private Set<Long> orgEkisIdsLong; // TODO: пересмотреть необходимость держать 2 одинаковых по сути поля, но с разными типами данных
        private String ekisInfo;

        public OrgRegistryGUIDInfo(Org org) {
            Set<Org> orgs = DAOService.getInstance().getFriendlyOrgs(org.getIdOfOrg());
            orgGuids = new HashSet<String>();
            guidInfo = "";
            orgEkisIds = new HashSet<>();
            orgEkisIdsLong = new HashSet<>();
            ekisInfo = "";
            for (Org o : orgs) {
                if (StringUtils.isEmpty(o.getGuid())) {
                    continue;
                }
                if (guidInfo.length() > 0) {
                    guidInfo += ", ";
                }
                guidInfo += o.getOrgNumberInName() + ": " + o.getGuid();
                orgGuids.add(o.getGuid());
            }
            for (Org o : orgs) {
                if (o.getEkisId() == null) continue;
                if (ekisInfo.length() > 0) ekisInfo += ", ";
                ekisInfo += o.getOrgNumberInName() + ": " + o.getEkisId().toString();
                orgEkisIds.add(o.getEkisId().toString());
                orgEkisIdsLong.add(o.getEkisId());
            }
        }

        public Set<Long> getOrgEkisIdsLong() {
            return orgEkisIdsLong;
        }

        public void setOrgEkisIdsLong(Set<Long> orgEkisIdsLong) {
            this.orgEkisIdsLong = orgEkisIdsLong;
        }

        public Set<String> getOrgGuids() {
            return orgGuids;
        }

        public String getGuidInfo() {
            return guidInfo;
        }

        public Set<String> getOrgEkisIds() {
            return orgEkisIds;
        }

        public String getEkisInfo() {
            return ekisInfo;
        }
    }

    public RegistryChange getRegistryChange(Long idOfRegistryChange) {
        if (idOfRegistryChange == null) {
            return null;
        }
        return em.find(RegistryChange.class, idOfRegistryChange);
    }

    @Override
    public RegistryChangeError getRegistryChangeError(Long idOfRegistryChangeError) {
        if (idOfRegistryChangeError == null) {
            return null;
        }
        return em.find(RegistryChangeError.class, idOfRegistryChangeError);
    }

    @Override
    public List<RegistryChangeCallback> applyRegistryChangeBatch(List<Long> changesList,
            boolean fullNameValidation, String groupName, ClientsMobileHistory clientsMobileHistory) throws Exception {
        Session session = null;
        Transaction transaction = null;
        List<RegistryChangeCallback> result = new ArrayList<RegistryChangeCallback>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            List<RegistryChange> registryChangeList = getRegistryChangeList(session, changesList); //получаем список объектов разногласий
            session.close();

            Integer newOperationsCount = 0; //количество созданий нового клиента на весь пакет (Дети + представители)
            Long idOfOrg = null;
            for (RegistryChange change : registryChangeList) {
                if (change.getOperation().equals(CREATE_OPERATION)) {
                    newOperationsCount++; //+1 л/с на каждое создание клиента
                    if (change.getGuardiansCount() != null) newOperationsCount += change.getGuardiansCount();  //+ количество представителей у создаваемого клиента
                }

                if (idOfOrg == null) idOfOrg = change.getIdOfOrg(); //idOfOrg у всего пакета совпадает
            }
            List<Long> contractIds = null;
            Iterator<Long> iterator = null;
            if (newOperationsCount > 0) {
                contractIds = RuntimeContext.getInstance().getClientContractIdGenerator()
                        .generateTransactionFree(idOfOrg, newOperationsCount);
                iterator = contractIds.iterator();
            }


            for (RegistryChange change : registryChangeList) {
                try {
                    session = RuntimeContext.getInstance().createPersistenceSession();
                    transaction = session.beginTransaction();
                    applyRegistryChange(session, change, fullNameValidation, iterator, groupName, clientsMobileHistory);
                    transaction.commit();
                    transaction = null;
                    session.close();
                    /*if (change.getIdOfClient() != null) {
                        try {
                            saveClientGuardians(change, iterator);
                        } catch (Exception e) {
                            logger.error("Error creating guardian: ", e);
                        }
                    }*/
                    result.add(new RegistryChangeCallback(change.getIdOfRegistryChange(), ""));
                } catch (Exception e) {
                    logger.error("Error ClientRegistryChange: ", e);
                    setChangeError(change.getIdOfRegistryChange(), e);
                    result.add(new RegistryChangeCallback(change.getIdOfRegistryChange(), e.getMessage()));
                } finally {
                    HibernateUtils.rollback(transaction, logger);
                    HibernateUtils.close(session, logger);
                }
            }
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    protected List<RegistryChange> getRegistryChangeList(Session session, List<Long> changesList) {
        Criteria criteria = session.createCriteria(RegistryChange.class);
        criteria.add(Restrictions.in("idOfRegistryChange", changesList));
        return criteria.list();
    }

    private Long getNextContractIdFromList(Iterator<Long> iterator) {
        try {
            return iterator.next();
        } catch (Exception e) {
            return null;
        }
    }

    protected void setGuidFromChange(FieldProcessor.Config fieldConfig, RegistryChange change) throws Exception {
        fieldConfig.setValue(ClientManager.FieldId.CLIENT_GUID, change.getClientGUID());
    }

    public void applyRegistryChange(Session session, RegistryChange change, boolean fullNameValidation,
            Iterator<Long> iterator, String groupName, ClientsMobileHistory clientsMobileHistory) throws Exception {
        Client afterSaveClient = null;

            Client dbClient = null;
            if (change.getIdOfClient() != null) {
                dbClient = (Client)session.load(Client.class, change.getIdOfClient());
            }
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

            Boolean migration = false;

            String group = groupName == null ? change.getGroupName() : groupName;
            ClientGroup beforeMigrationGroup = null;

            switch (change.getOperation()) {
                case CREATE_OPERATION:
                    //  добавление нового клиента
                    String dateCreate = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));

                    checkGroupNamesToOrgs(session, group, change.getIdOfOrg());

                    String notifyByPush = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS) ? "1" : "0";
                    String notifyByEmail = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS) ? "1" : "0";
                    FieldProcessor.Config createConfig = new ClientManager.ClientFieldConfig();
                    Long contractId = getNextContractIdFromList(iterator);
                    if (contractId != null) {
                        createConfig.setValue(ClientManager.FieldId.CONTRACT_ID, contractId);
                    }
                    setGuidFromChange(createConfig, change);
                    createConfig.setValue(ClientManager.FieldId.MESH_GUID, change.getMeshGUID());
                    createConfig.setValue(ClientManager.FieldId.SURNAME, change.getSurname());
                    createConfig.setValue(ClientManager.FieldId.NAME, change.getFirstName());
                    createConfig.setValue(ClientManager.FieldId.SECONDNAME, change.getSecondName());
                    createConfig.setValue(ClientManager.FieldId.GROUP, groupName == null ? change.getGroupName() : groupName);
                    createConfig.setValue(ClientManager.FieldId.PARALLEL, change.getParallel());
                    createConfig.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, notifyByPush);
                    createConfig.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, notifyByEmail);
                    if (change.getGender() != null) {
                        if (change.getGender().equals(0))
                            createConfig.setValue(ClientManager.FieldId.GENDER, "f");
                        if (change.getGender().equals(1))
                            createConfig.setValue(ClientManager.FieldId.GENDER, "m");
                    }
                    Date createDateBirth = new Date(change.getBirthDate());
                    createConfig.setValue(ClientManager.FieldId.BIRTH_DATE, format.format(createDateBirth));
                    createConfig.setValue(ClientManager.FieldId.CHECKBENEFITS, false);
                    createConfig.setValue(ClientManager.FieldId.GUARDIANS_COUNT, change.getGuardiansCount());
                    createConfig.setValueSet(ClientManager.FieldId.GUARDIANS_COUNT_LIST, change.getRegistryChangeGuardiansSet());
                    createConfig.setValue(ClientManager.FieldId.AGE_TYPE_GROUP, change.getAgeTypeGroup());
                    createConfig.setValue(ClientManager.FieldId.CREATED_FROM, Integer.toString(ClientCreatedFromType.REGISTRY.getValue()));
                    afterSaveClient = ClientManager.registerClientTransactionFree(change.getIdOfOrg(),
                            (ClientManager.ClientFieldConfig) createConfig, fullNameValidation,
                            session, String.format(MskNSIService.COMMENT_AUTO_CREATE, dateCreate), clientsMobileHistory);
                    change.setIdOfClient(afterSaveClient.getIdOfClient());
                    change.setIdOfOrg(afterSaveClient.getOrg().getIdOfOrg());

                    break;
                case DELETE_OPERATION:
                    ClientGroup deletedClientGroup = DAOUtils
                            .findClientGroupByGroupNameAndIdOfOrg(session, change.getIdOfOrg(),
                                    ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                    if (deletedClientGroup == null) {
                        deletedClientGroup = DAOUtils.createClientGroup(session, change.getIdOfOrg(),
                                ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                    }
                    addClientMigrationLeaving(session, dbClient, change);

                    dbClient.setIdOfClientGroup(deletedClientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    if(dbClient.getMeshGUID() == null && StringUtils.isNotEmpty(change.getMeshGUID())){
                        dbClient.setMeshGUID(change.getMeshGUID());
                    }

                    String dateDelete = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
                    String deleteCommentsAdds = String.format(MskNSIService.COMMENT_AUTO_DELETED, dateDelete);
                    commentsAddsDelete(dbClient, deleteCommentsAdds);
                    dbClient.setUpdateTime(new Date());
                    session.save(dbClient);
                    break;
                case MOVE_OPERATION:
                    migration = true;
                    Org newOrg = (Org) session.load(Org.class, change.getIdOfMigrateOrgTo());

                    Org beforeMigrateOrg = dbClient.getOrg();
                    beforeMigrationGroup = dbClient.getClientGroup();

                    checkGroupNamesToOrgs(session, change.getGroupName(), newOrg.getIdOfOrg());

                    GroupNamesToOrgs groupNamesToOrgs = DAOUtils
                            .getAllGroupnamesToOrgsByIdOfMainOrgAndGroupName(session, newOrg.getIdOfOrg(),
                                    change.getGroupName());

                    if (groupNamesToOrgs != null && groupNamesToOrgs.getIdOfOrg() != null) {
                        clientGroupProcess(session, dbClient, groupNamesToOrgs);
                    } else {
                        ClientGroup clientGroup = DAOUtils
                                .findClientGroupByGroupNameAndIdOfOrgNotIgnoreCase(session, newOrg.getIdOfOrg(),
                                        change.getGroupName());
                        if (clientGroup == null) {
                            clientGroup = DAOUtils
                                    .createClientGroup(session, newOrg.getIdOfOrg(), change.getGroupName());
                        }
                        dbClient.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                        dbClient.setParallel(change.getParallel());
                        dbClient.setOrg(newOrg);
                        if(dbClient.getMeshGUID() == null && StringUtils.isNotEmpty(change.getMeshGUID())){
                            dbClient.setMeshGUID(change.getMeshGUID());
                        }
                    }
                    if(!newOrg.multiCardModeIsEnabled() && dbClient.activeMultiCardMode()){
                        dbClient.setMultiCardMode(false);
                        ClientManager.blockExtraCardOfClient(dbClient, session);
                    }

                    addClientMigrationEntry(session, beforeMigrateOrg, beforeMigrationGroup, dbClient.getOrg(), dbClient, change);
                    change.setIdOfOrg(dbClient.getOrg().getIdOfOrg());
                    dbClient.setUpdateTime(new Date());
                    session.save(dbClient);
                case MODIFY_OPERATION:
                    Org newOrg1 = (Org)session.load(Org.class, change.getIdOfOrg());
                    Org beforeModifyOrg = dbClient.getOrg();
                    if (beforeMigrationGroup == null)
                    {
                        beforeMigrationGroup = dbClient.getClientGroup();
                    }

                    checkGroupNamesToOrgs(session, group, change.getIdOfOrg());

                    String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
                    FieldProcessor.Config modifyConfig = new ClientManager.ClientFieldConfigForUpdate();
                    setGuidFromChange(modifyConfig, change);
                    modifyConfig.setValue(ClientManager.FieldId.MESH_GUID, change.getMeshGUID());
                    modifyConfig.setValue(ClientManager.FieldId.SURNAME, change.getSurname());
                    modifyConfig.setValue(ClientManager.FieldId.NAME, change.getFirstName());
                    modifyConfig.setValue(ClientManager.FieldId.SECONDNAME, change.getSecondName());
                    modifyConfig.setValue(ClientManager.FieldId.GROUP, change.getGroupName());
                    modifyConfig.setValue(ClientManager.FieldId.PARALLEL, change.getParallel());
                    modifyConfig.setValue(ClientManager.FieldId.GENDER, change.getGender());
                    Date modifyDateBirth = new Date(change.getBirthDate());
                    modifyConfig.setValue(ClientManager.FieldId.BIRTH_DATE, format.format(modifyDateBirth));
                    modifyConfig.setValue(ClientManager.FieldId.CHECKBENEFITS, false);
                    modifyConfig.setValue(ClientManager.FieldId.AGE_TYPE_GROUP, change.getAgeTypeGroup());

                    ClientManager.modifyClientTransactionFree((ClientManager.ClientFieldConfigForUpdate) modifyConfig,
                            newOrg1, String.format(MskNSIService.COMMENT_AUTO_MODIFY, date),
                            dbClient, session, true, clientsMobileHistory);

                    if (!migration) {
                        if (!dbClient.getOrg().getIdOfOrg().equals(beforeModifyOrg.getIdOfOrg())) {
                            addClientMigrationEntry(session, beforeModifyOrg, beforeMigrationGroup, dbClient.getOrg(), dbClient,
                                    change); //орг. меняется - история миграции между ОО
                        } else {
                            if((change.getGroupName() == null && change.getGroupNameFrom() == null) ||
                                    (change.getGroupName() != null && change.getGroupNameFrom() != null &&
                                    !change.getGroupName().equals(change.getGroupNameFrom()))) {
                                addClientGroupMigrationEntry(session, dbClient.getOrg(), dbClient, change);
                                //если орг. не меняется, добавляем историю миграции внутри ОО
                            }
                        }
                    }
                    change.setIdOfOrg(dbClient.getOrg().getIdOfOrg());
                    dbClient.setUpdateTime(new Date());
                    session.save(dbClient);
                    break;
                default:
                    logger.error("Unknown update registry change operation " + change.getOperation());
            }
            change.setApplied(true);
            session.update(change);

    }

    private void checkGroupNamesToOrgs(Session session, String groupName, Long idofOrg) {
        try {
            Org org = (Org) session.get(Org.class, idofOrg);
            GroupNamesToOrgs groupNamesToOrgs = DAOUtils
                    .getAllGroupnamesToOrgsByIdOfMainOrgAndGroupName(session, org.getIdOfOrg(), groupName);
            if (groupNamesToOrgs == null) {
                Long version = DAOUtils.nextVersionByGroupNameToOrg(session);
                DAOUtils.createGroupNamesToOrg(session, org, version, groupName);
            } else {
                if(!groupNamesToOrgs.getGroupName().equals(groupName)){
                    groupNamesToOrgs.setGroupName(groupName);
                    session.update(groupNamesToOrgs);
                }
            }
        } catch (Exception e){
            logger.error("Can't check GroupNamesToOrgs: " + e.getMessage());
        }
    }

    public static void clientGroupProcess(Session session, Client dbClient, GroupNamesToOrgs groupNamesToOrgs)
            throws Exception {
        ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrgNotIgnoreCase(session,
                groupNamesToOrgs.getIdOfOrg(), groupNamesToOrgs.getGroupName());
        if (clientGroup == null) {
            clientGroup = DAOUtils.createClientGroup(session, groupNamesToOrgs.getIdOfOrg(),
                    groupNamesToOrgs.getGroupName());
        }
        dbClient.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        Org org = (Org) session.load(Org.class, groupNamesToOrgs.getIdOfOrg());
        dbClient.setOrg(org);
    }

    public static void commentsAddsDelete(Client dbClient, String deleteCommentsAdds) {
        if (deleteCommentsAdds != null && deleteCommentsAdds.length() > 0) {
            String comments = dbClient.getRemarks();
            if (comments==null) comments="";
            if (comments.indexOf("{%") > -1) {
                comments = comments.substring(0, comments.indexOf("{%")) + comments
                        .substring(comments.indexOf("%}") + 1);
            }
            comments += deleteCommentsAdds;
            if (comments.length() >= 1024) {
                comments = comments.replaceAll(MskNSIService.REPLACEMENT_REGEXP, "");
            }

            dbClient.setRemarks(comments);
        }
    }

    private static void saveClientGuardians(RegistryChange registryChange, Iterator<Long> iterator,
            ClientsMobileHistory clientsMobileHistory) throws Exception {

            Set<RegistryChangeGuardians> registryChangeGuardiansSet = registryChange.getRegistryChangeGuardiansSet();

            if (registryChangeGuardiansSet == null) return;
            for (RegistryChangeGuardians registryChangeGuardians : registryChangeGuardiansSet) {

                Session session = null;
                Transaction transaction = null;
                try {
                    session = RuntimeContext.getInstance().createPersistenceSession();
                    transaction = session.beginTransaction();
                    Long clientId = registryChange.getIdOfClient();
                    Long idOfOrg = registryChange.getIdOfOrg();
                    ClientManager.applyClientGuardians(registryChangeGuardians, session, idOfOrg, clientId, iterator,
                            clientsMobileHistory);
                    transaction.commit();
                    transaction = null;
                } finally {
                    HibernateUtils.rollback(transaction, logger);
                    HibernateUtils.close(session, logger);
                }
            }
    }

    //@Transactional
    private void addClientMigrationEntry(Session session,Org oldOrg, ClientGroup beforeMigrationGroup, Org newOrg, Client client, RegistryChange change){
        ClientManager.addClientMigrationEntry(session, oldOrg, beforeMigrationGroup, newOrg, client,
                ClientMigration.MODIFY_IN_REGISTRY.concat(String.format(" (ид. ОО=%s)", change.getIdOfOrg())), change.getGroupName());
    }

    //@Transactional
    private void addClientGroupMigrationEntry(Session session,Org org, Client client, RegistryChange change){
        ClientManager.createClientGroupMigrationHistory(session, client, org, client.getIdOfClientGroup(),
                change.getGroupName(), ClientGroupMigrationHistory.MODIFY_IN_REGISTRY.concat(String.format(" (ид. ОО=%s)", change.getIdOfOrg())));
    }

    private void addClientMigrationLeaving(Session session, Client client, RegistryChange change) throws Exception {
        Org org = (Org)session.get(Org.class, change.getIdOfOrg());
        ClientManager.createClientGroupMigrationHistory(session, client, org, ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup(), ClientGroupMigrationHistory.MODIFY_IN_REGISTRY
                        .concat(String.format(" (ид. ОО=%s)", change.getIdOfOrg())));
    }

    public void setChangeError(long idOfRegistryChange, Exception e) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            RegistryChange change = (RegistryChange)session.load(RegistryChange.class, idOfRegistryChange);
            String err = e.getMessage();
            if (err != null && err.length() > 255) {
                err = err.substring(0, 255).trim();
            }
            change.setError(err);
            session.update(change);
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public ClientMskNSIService getNSIService() {
        switch (RuntimeContext.getInstance().getOptionValueString(Option.OPTION_NSI_VERSION)) {
            case Option.NSI3 :
                //Не смотрим на настройку из MODE_PROPERTY. Pабота с файлом НСИ-3 по екис ид
                return RuntimeContext.getAppContext().getBean("ImportRegisterNSI3ServiceKafkaWrapper", ImportRegisterNSI3ServiceKafkaWrapper.class);
        }
        String mode = RuntimeContext.getInstance().getPropertiesValue(ImportRegisterFileService.MODE_PROPERTY, null);
        if (mode.equals(ImportRegisterFileService.MODE_SYMMETRIC)) {
            //забор клиентов из таблиц симметрика
            return RuntimeContext.getAppContext().getBean("ImportRegisterSymmetricService", ImportRegisterSymmetricService.class);
        } else if (mode.equals(ImportRegisterFileService.MODE_FILE)) {
            //клиенты из файла НСИ-2
            return RuntimeContext.getAppContext().getBean("ImportRegisterFileService", ImportRegisterFileService.class);
        } else {
            //запросы в апи НСИ-1
            return RuntimeContext.getAppContext().getBean("ClientMskNSIService", ClientMskNSIService.class);
        }
    }

    @Override
    @Transactional
    public StringBuffer syncClientsWithRegistry(long idOfOrg, boolean performChanges, StringBuffer logBuffer,
            boolean manualCheckout) throws Exception {
        if (!DAOService.getInstance().isSverkaEnabled()) {
            throw new ServiceTemporaryUnavailableException("Service temporary unavailable");
        } else if(!RuntimeContext.getInstance().getOptionValueString(Option.OPTION_NSI_VERSION).equals(Option.NSI3)
                && !ImportRegisterNSI3ServiceKafkaWrapper.workWithKafka()){
            throw new ServiceTemporaryUnavailableException("Set wrong data source");
        }
        /*Убираем ограничение на выполнение сверки не чаще, чем раз в час
        if (!DAOService.getInstance().isSverkaEnabledByOrg(idOfOrg)) {
            throw new RegistryTimeDeltaException("Запрос не разрешен. Повторите попытку не ранее, чем через час");
        }*/
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        Org org = em.find(Org.class, idOfOrg);
        String synchDate = "[Синхронизация с Реестрами от " + date + " для " + org.getIdOfOrg() + "]: ";
        OrgRegistryGUIDInfo orgGuids = new OrgRegistryGUIDInfo(org);
        log(synchDate + "Производится синхронизация для " + org.getOfficialName() + " GUID [" + orgGuids.getGuidInfo()
                + "] + EKIS Id [" + orgGuids.getEkisInfo() + "]", logBuffer);

        SecurityJournalProcess process = SecurityJournalProcess.createJournalRecordStart(
                SecurityJournalProcess.EventType.NSI_CLIENTS, new Date());
        process.saveWithSuccess(true);
        boolean isSuccessEnd = true;

        try {
            //DAOService.getInstance().updateOrgRegistrySync(idOfOrg, 1);
            //Проверка на устаревшие гуиды организаций
            ClientMskNSIService service = getNSIService();
            String badGuids = service.getBadGuids(orgGuids);
            if (!StringUtils.isEmpty(badGuids)) {
                isSuccessEnd = false;
                throw new BadOrgGuidsException(badGuids);
            }

            //  Итеративно загружаем клиентов, используя ограничения
            List<ExpandedPupilInfo> pupils = service.getPupilsByOrgGUID(orgGuids, null, null, null);
            if(CollectionUtils.isEmpty(pupils)){
                log("Нет данных для сверки", logBuffer);
                return logBuffer;
            }
            log(synchDate + "Получено " + pupils.size() + " записей", logBuffer);
            //  !!!!!!!!!!
            //  !!!!!!!!!!
            //  parseClients(synchDate, date, org, pupils, performChanges, logBuffer, manualCheckout);
            //  !!!!!!!!!!
            //  !!!!!!!!!!
            saveClients(synchDate, date, System.currentTimeMillis(), org, pupils, logBuffer, false);
            return logBuffer;
        } catch (Exception e) {
            logError(null, e, logBuffer);
            isSuccessEnd = false;
            throw e;
        } finally {
            SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(
                    SecurityJournalProcess.EventType.NSI_CLIENTS, new Date());
            processEnd.saveWithSuccess(isSuccessEnd);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setOrgSyncErrorCode(String orgGuid, Integer code) throws Exception {
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("from OrgSync os where os.org.guid = :guid");
        query.setParameter("guid", orgGuid);
        List<OrgSync> list = query.list();
        for (OrgSync os : list) {
            os.setErrorState(code);
            session.save(os);
        }
    }

    public static boolean doClientUpdate(FieldProcessor.Config fieldConfig, Object fieldID, String reesterValue,
            String currentValue, boolean doClientUpdate) throws Exception {
        reesterValue = emptyIfNull(reesterValue);
        currentValue = emptyIfNull(currentValue);
        fieldConfig.setValue(fieldID, reesterValue);
        return doClientUpdate || !currentValue.trim().equals(reesterValue.trim());
    }

    public void doClientUpdate(FieldProcessor.Config fieldConfig, Object fieldID, List<GuardianInfo> reesterValue)
            throws Exception {
        reesterValue = emptyIfNull(reesterValue);
        fieldConfig.setValueList(fieldID, reesterValue);
    }

    public static String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    private static List<GuardianInfo> emptyIfNull(List<GuardianInfo> list) {
        return list.isEmpty() ? new ArrayList<GuardianInfo>() : list;
    }

    public static void log(String str, StringBuffer logBuffer) {
        if (logBuffer != null) {
            logBuffer.append(str).append('\n');
        }
        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_LOG)) {
            logger.info(str);
        }
    }

    public static void logError(String str, Exception e, StringBuffer logBuffer) {
        if (logBuffer != null) {
            logBuffer.append(str).append(": ").append(e.getMessage());
        }
        if (e != null) {
            logger.error(str, e);
        } else {
            logger.error(str);
        }
    }

    public static boolean isPupilIgnoredFromImport(String guid, String group) {
        /*if (group != null && group.toLowerCase().startsWith("дошкол")) {
            return true;
        }*/
        if (guid == null || guid.length() == 0) {
            return true;
        }
        return false;
    }

    public static class ExpandedPupilInfo {

        public String familyName, firstName, secondName, guid, group, enterGroup, enterDate, leaveDate;
        public String groupDeprecated; //сюда группу из секции "Текущий класс или группа", как резервный случай, если в секции "Класс" будет пусто
        public String groupNewWay;     //сюда группу из секции "Класс"
        public String birthDate;
        public String benefitDSZN;

        public boolean deleted;
        public boolean created;
        public String guidOfOrg;
        public String recordState;
        public String gender;
        public String guardiansCount;
        public String ageTypeGroup;
        public String parallel;
        public String meshGUID;
        public Long orgId;

        public List<GuardianInfo> guardianInfoList = new ArrayList<GuardianInfo>();

        public boolean isDeleted() {
            return deleted;
        }

        public boolean isCreated() {
            return created;
        }

        public String getGuidOfOrg() {
            return guidOfOrg;
        }

        public void setGuidOfOrg(String guidOfOrg) {
            this.guidOfOrg = guidOfOrg;
        }

        public String getFamilyName() {
            return familyName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getGuid() {
            return guid;
        }

        public String getGroup() {
            return group;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getBenefitDSZN() {
            return benefitDSZN;
        }

        public void setBenefitDSZN(String benefitDSZN) {
            this.benefitDSZN = benefitDSZN;
        }

        public String getRecordState() {
            return recordState;
        }

        public void setRecordState(String recordState) {
            this.recordState = recordState;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getGender() {
            return gender;
        }

        public String getEnterGroup() {
            return enterGroup;
        }

        public void setEnterGroup(String enterGroup) {
            this.enterGroup = enterGroup;
        }

        public String getEnterDate() {
            return enterDate;
        }

        public void setEnterDate(String enterDate) {
            this.enterDate = enterDate;
        }

        public String getLeaveDate() {
            return leaveDate;
        }

        public void setLeaveDate(String leaveDate) {
            this.leaveDate = leaveDate;
        }

        public List<GuardianInfo> getGuardianInfoList() {
            return guardianInfoList;
        }

        public void setGuardianInfoList(List<GuardianInfo> guardianInfoList) {
            this.guardianInfoList = guardianInfoList;
        }

        public String getGuardiansCount() {
            return guardiansCount;
        }

        public void setGuardiansCount(String guardiansCount) {
            this.guardiansCount = guardiansCount;
        }

        public String getAgeTypeGroup() {
            return ageTypeGroup;
        }

        public void setAgeTypeGroup(String ageTypeGroup) {
            this.ageTypeGroup = ageTypeGroup;
        }

        public String getMeshGUID() {
            return meshGUID;
        }

        public void setMeshGUID(String meshGUID) {
            this.meshGUID = meshGUID;
        }

        public void copyFrom(ExpandedPupilInfo pi) {
            this.familyName = pi.familyName;
            this.firstName = pi.firstName;
            this.secondName = pi.secondName;
            this.guid = pi.guid;
            this.group = pi.group;
            this.birthDate = pi.birthDate;
            this.benefitDSZN = pi.benefitDSZN;
            this.deleted = pi.deleted;
            this.created = pi.created;
            this.guidOfOrg = pi.guidOfOrg;
            this.recordState = pi.recordState;
            this.gender = pi.gender;
            this.enterGroup = pi.enterGroup;
            this.enterDate = pi.enterDate;
            this.leaveDate = pi.leaveDate;
            this.ageTypeGroup = pi.ageTypeGroup;
            this.parallel = pi.parallel;
            this.orgId = pi.orgId;
        }

        public Long getOrgId() {
            return orgId;
        }

        public void setOrgId(Long organizationId) {
            this.orgId = organizationId;
        }

        public String getGroupDeprecated() {
            return groupDeprecated;
        }

        public String getGroupNewWay() {
            return groupNewWay;
        }

        public String getParallel() {
            return parallel;
        }
    }

    public static class GuardianInfo {

        private String familyName;
        private String firstName;
        private String secondName;
        private String relationship;
        private String phoneNumber;
        private String emailAddress;
        private Boolean legalRepresentative;
        private String ssoid;
        private String guid;

        public GuardianInfo() {
        }

        public GuardianInfo(String familyName, String firstName, String secondName, String relationship,
                String phoneNumber, String emailAddress) {
            this.familyName = familyName;
            this.firstName = firstName;
            this.secondName = secondName;
            this.relationship = relationship;
            this.phoneNumber = phoneNumber;
            this.emailAddress = emailAddress;
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public String getRelationship() {
            return relationship;
        }

        public void setRelationship(String relationship) {
            this.relationship = relationship;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public Boolean getLegalRepresentative() {
            return legalRepresentative;
        }

        public void setLegalRepresentative(Boolean legalRepresentative) {
            this.legalRepresentative = legalRepresentative;
        }

        public String getSsoid() {
            return ssoid;
        }

        public void setSsoid(String ssoid) {
            this.ssoid = ssoid;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }
    }
}