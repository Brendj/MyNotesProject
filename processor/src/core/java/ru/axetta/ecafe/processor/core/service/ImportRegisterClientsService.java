/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.mail.File;
import ru.axetta.ecafe.processor.core.partner.nsi.ClientMskNSIService;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.partner.nsi.OrgMskNSIService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@Component
@Scope("singleton")
public class ImportRegisterClientsService {

    public static final int CREATE_OPERATION = 1;
    public static final int DELETE_OPERATION = 2;
    public static final int MODIFY_OPERATION = 3;
    public static final int MOVE_OPERATION = 4;

    private static final int MAX_CLIENTS_PER_TRANSACTION = 50;
    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Autowired
    ClientMskNSIService nsiService;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterClientsService.class);
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final String ORG_SYNC_MARKER = "СИНХРОНИЗАЦИЯ_РЕЕСТРЫ";
    private static final long MILLISECONDS_IN_DAY = 86400000L;
    private static final int MAX_THREADS = 10;


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
        return RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class)
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
            //System.out.println(Thread.currentThread().getName()+" Start. Command = "+command);
            processCommand();
            //System.out.println(Thread.currentThread().getName()+" End.");
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
                            RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class)
                                    .syncClientsWithRegistry(org.getIdOfOrg(), true, null, false);
                        } catch (SocketTimeoutException ste) {
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

        int count = (int) Math.ceil((double) orgs.size() / (double) threadsCount);
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
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            return;
        }
        List<Org> orgs = DAOService.getInstance().getOrderedSynchOrgsList();
        RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).checkRegistryChangesValidity();
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
        RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class)
                .checkRegistryChangesValidity();

        int maxAttempts = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_MSK_NSI_MAX_ATTEMPTS);
        for (Org org : orgs) {
            if (org.getTag() == null || !org.getTag().toUpperCase().contains(ORG_SYNC_MARKER)) {
                continue;
            }
            int attempt = 0;
            while (attempt < maxAttempts) {
                try {
                    RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class)
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
        Query q = session.createSQLQuery("delete from cf_registrychange where createDate<:minCreateDate");
        q.setLong("minCreateDate", cal.getTimeInMillis());
        q.executeUpdate();
        q = session.createSQLQuery("delete from cf_registrychange_errors where createDate<:minCreateDate");
        q.setLong("minCreateDate", cal.getTimeInMillis());
        q.executeUpdate();
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
        List<Client> currentClients = DAOUtils.findClientsWithoutPredefinedForOrgAndFriendly(em, org);
        List<Org> orgsList = DAOUtils.findFriendlyOrgs(em, org);   //  Текущая организация и дружественные ей


        //  Если используется старый метод полной загрузки контенгента школы, то проверяем каждого ученика в отдельности на его
        //  наличие в школе. Иначе - смотрим флаг удалено/не удалено и в зависимости от этого помещаем ученика в удаленные
        if (deleteClientsIfNotFound) {
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
                try {
                    ClientGroup currGroup = dbClient.getClientGroup();
                    if (currGroup != null &&
                            currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup().longValue() >= ClientGroup
                                    .Predefined.CLIENT_EMPLOYEES.getValue().longValue() &&
                            currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup().longValue() < ClientGroup
                                    .Predefined.CLIENT_LEAVING.getValue().longValue()) {
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
                    if (emptyIfNull(dbClient.getClientGUID()).equals("") || !found) {
                        log(synchDate + "Удаление " +
                                emptyIfNull(dbClient.getClientGUID()) + ", " + emptyIfNull(
                                dbClient.getPerson().getSurname()) + " " +
                                emptyIfNull(dbClient.getPerson().getFirstName()) + " " + emptyIfNull(
                                dbClient.getPerson().getSecondName()) + ", " +
                                emptyIfNull(dbClient.getClientGroup() == null ? ""
                                        : dbClient.getClientGroup().getGroupName()), logBuffer);
                        addClientChange(em, ts, org.getIdOfOrg(), dbClient, DELETE_OPERATION,
                                RegistryChange.FULL_COMPARISON);
                    }
                } catch (Exception e) {
                    logError("Failed to delete client " + dbClient, e, logBuffer);
                }
            }
        } else {
            for (ExpandedPupilInfo epi : pupils) {
                if (epi.deleted) {
                    Client dbClient = DAOUtils.findClientByGuid(em, epi.guid);
                    if (dbClient == null) {
                        continue;
                    }
                    log(synchDate + "Удаление " +
                            emptyIfNull(dbClient.getClientGUID()) + ", " + emptyIfNull(
                            dbClient.getPerson().getSurname()) + " " +
                            emptyIfNull(dbClient.getPerson().getFirstName()) + " " + emptyIfNull(
                            dbClient.getPerson().getSecondName()) + ", " +
                            emptyIfNull(
                                    dbClient.getClientGroup() == null ? "" : dbClient.getClientGroup().getGroupName()),
                            logBuffer);
                    addClientChange(em, ts, org.getIdOfOrg(), dbClient, DELETE_OPERATION,
                            RegistryChange.FULL_COMPARISON);
                }
            }
        }


        //  Проходим по ответу от Реестров и анализируем надо ли обновлять его или нет
        for (ExpandedPupilInfo pupil : pupils) {
            FieldProcessor.Config fieldConfig;
            boolean updateClient = false;
            Client cl = DAOUtils.findClientByGuid(em, emptyIfNull(pupil.getGuid()));
            if (cl == null) {
                fieldConfig = new ClientManager.ClientFieldConfig();
            } else {
                if (cl.getClientGroup() != null &&
                    cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().longValue() >= ClientGroup
                        .Predefined.CLIENT_EMPLOYEES.getValue().longValue()
                        && cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().longValue()
                        < ClientGroup.Predefined.CLIENT_LEAVING.getValue().longValue()) {
                    continue;
                }
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
            } else {
                //  Если группа у клиента не указана, то перемещаем его в Другие
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.GROUP,
                        ClientGroup.Predefined.CLIENT_OTHERS.getNameOfGroup(),
                        cl == null || cl.getClientGroup() == null ? null : cl.getClientGroup().getGroupName(),
                        updateClient);
            }
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
            if (cl != null && !cl.getOrg().getGuid().equals(pupil.getGuidOfOrg()) && !crossFound) {
                log(synchDate + "Перевод " + emptyIfNull(cl.getClientGUID()) + ", " +
                        emptyIfNull(cl.getPerson() == null ? "" : cl.getPerson().getSurname()) + " " +
                        emptyIfNull(cl.getPerson() == null ? "" : cl.getPerson().getFirstName()) + " " +
                        emptyIfNull(cl.getPerson() == null ? "" : cl.getPerson().getSecondName()) + ", " +
                        emptyIfNull(cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName())
                        + " из школы " + cl.getOrg().getIdOfOrg() + " в школу " + org.getIdOfOrg(), logBuffer);
                addClientChange(em, ts, org.getIdOfOrg(), org.getIdOfOrg(), fieldConfig, cl, MOVE_OPERATION,
                        RegistryChange.FULL_COMPARISON);
                continue;
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
                        addClientChange(ts, org.getIdOfOrg(), fieldConfig, CREATE_OPERATION,
                                RegistryChange.FULL_COMPARISON);
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
                    addClientChange(ts, org.getIdOfOrg(), fieldConfig, cl, MODIFY_OPERATION,
                            RegistryChange.FULL_COMPARISON);
                }
            } catch (Exception e) {
                logError("Failed to add client for " + org.getIdOfOrg() + " org", e, logBuffer);
            }
        }
        log(synchDate + "Синхронизация завершена для " + org.getOfficialName(), logBuffer);
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


    private void addClientChange(long ts, long idOfOrg, FieldProcessor.Config fieldConfig, int operation, int type)
            throws Exception {
        //  ДОБАВИТЬ ЗАПИСЬ ОБ ИЗМЕНЕНИИ ПОЛЬЗОВАТЕЛЯ И УКАЗАТЬ СООТВЕТСТВУЮЩУЮ ОПЕРАЦИЮ
        addClientChange(ts, idOfOrg, fieldConfig, null, operation, type);
    }


    private void addClientChange(long ts, long idOfOrg, FieldProcessor.Config fieldConfig, Client currentClient,
            int operation, int type) throws Exception {
        addClientChange(em, ts, idOfOrg, null, fieldConfig, currentClient, operation, type);
    }

    public static void addClientChange(EntityManager em, long ts, long idOfOrg, Long idOfMigrateOrg,
            FieldProcessor.Config fieldConfig, Client currentClient, int operation, int type) throws Exception {
        addClientChange(em, ts, idOfOrg, idOfMigrateOrg, fieldConfig, currentClient, operation, type, null);
    }

    public static boolean isRegistryChangeExist(String notificationId, Client client, int operation, Session session) {
        Query q = session.createSQLQuery("SELECT 1 " + "FROM cf_registrychange "
                + "where notificationId=:notificationId and operation=:operation");
        q.setParameter("notificationId", notificationId);
        q.setParameter("operation", operation);
        List res = q.list();
        if (res == null || res.size() < 1) {
            return false;
        } else {
            return true;
        }
    }

    public static void addClientChange(EntityManager em, long ts, long idOfOrg, Long idOfMigrateOrg,
            FieldProcessor.Config fieldConfig, Client currentClient, int operation, int type, String notificationId)
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

        String clientGuid = emptyIfNull(fieldConfig.getValue(ClientManager.FieldId.CLIENT_GUID));
        String name = trim(fieldConfig.getValue(ClientManager.FieldId.NAME), 64, clientGuid, "Имя ученика");
        String secondname = trim(fieldConfig.getValue(ClientManager.FieldId.SECONDNAME), 128, clientGuid, "Отчество ученика");
        String surname = trim(fieldConfig.getValue(ClientManager.FieldId.SURNAME), 128, clientGuid, "Фамилия ученика");
        String registryGroupName = trim(fieldConfig.getValue(ClientManager.FieldId.GROUP), 64, clientGuid, "Наименование группы");
        RegistryChange ch = new RegistryChange();
        ch.setClientGUID(clientGuid);
        ch.setFirstName(name);
        ch.setSecondName(secondname);
        ch.setSurname(surname);
        ch.setGroupName(registryGroupName);
        ch.setIdOfClient(currentClient == null ? null : currentClient.getIdOfClient());
        ch.setIdOfOrg(idOfOrg);
        ch.setOperation(operation);
        ch.setCreateDate(ts);
        ch.setApplied(false);
        ch.setType(type);
        ch.setNotificationId(notificationId);
        ch.setGender(currentClient.getGender());
        ch.setBenefitOnAdmission(currentClient.getBenefitOnAdmission());
        ch.setBirthDate(currentClient.getBirthDate().getTime());
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
        }
        if (operation == MODIFY_OPERATION) {
            ClientGroup currentGroup = currentClient.getClientGroup();
            if (currentGroup != null) {
                currentGroup = em.merge(currentGroup);
                ch.setGroupNameFrom(currentGroup.getGroupName());
            } else {
                ch.setGroupNameFrom("");
            }
            ch.setFirstNameFrom(currentClient.getPerson().getFirstName());
            ch.setSecondNameFrom(currentClient.getPerson().getSecondName());
            ch.setSurnameFrom(currentClient.getPerson().getSurname());
        }
        sess.save(ch);
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

    public static void addClientChange(EntityManager em, long ts, long idOfOrg, Client currentClient, int operation,
            int type) throws Exception {
        addClientChange(em, ts, idOfOrg, currentClient, operation, type, null);
    }

    public static void addClientChange(EntityManager em, long ts, long idOfOrg, Client currentClient, int operation,
            int type, String notificationId) throws Exception {
        //  ДОБАВИТЬ ЗАПИСЬ ОБ УДАЛЕНИИ В БД
        Session sess = (Session) em.getDelegate();
        if (currentClient != null) {
            currentClient = em.merge(currentClient);
        }
        if (type == RegistryChange.CHANGES_UPDATE && !StringUtils.isBlank(notificationId) &&
                isRegistryChangeExist(notificationId, currentClient, operation, sess)) {
            return;
        }

        RegistryChange ch = new RegistryChange();
        ch.setClientGUID(emptyIfNull(currentClient.getClientGUID()));
        ch.setFirstName(currentClient.getPerson().getFirstName());
        ch.setSecondName(currentClient.getPerson().getSecondName());
        ch.setSurname(currentClient.getPerson().getSurname());
        ch.setGroupName(currentClient.getClientGroup() == null ? "" : currentClient.getClientGroup().getGroupName());
        ch.setIdOfClient(currentClient.getIdOfClient());
        ch.setIdOfOrg(idOfOrg);
        ch.setOperation(operation);
        ch.setType(type);
        ch.setCreateDate(ts);
        ch.setApplied(false);
        ch.setNotificationId(notificationId);
        ch.setGender(currentClient.getGender());
        ch.setBirthDate(currentClient.getBirthDate().getTime());
        ch.setBenefitOnAdmission(currentClient.getBenefitOnAdmission());
        sess.save(ch);
    }


    @Transactional
    public void parseClients(String synchDate, String date, Org org, List<ExpandedPupilInfo> pupils,
            boolean performChanges, StringBuffer logBuffer, boolean manualCheckout) throws Exception {
        log(synchDate + "Синхронизация списков начата для " + org.getOfficialName() + (performChanges ? ""
                : " РЕЖИМ БЕЗ ПРИМЕНЕНИЯ ИЗМЕНЕНИЙ"), logBuffer);


        //  Открываем сессию и загружаем клиентов, которые сейчас находятся в БД
        Session session = (Session) em.getDelegate();
        List<Client> currentClients = DAOUtils.findClientsForOrgAndFriendly(em, org);
        List<Org> orgsList = DAOUtils.findFriendlyOrgs(em, org);   //  Текущая организация и дружественные ей
        //orgsList.add(org);


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
        }*/

        //  Проходим по ответу от Реестров и анализируем надо ли обновлять его или нет
        for (ExpandedPupilInfo pupil : pupils) {
            FieldProcessor.Config fieldConfig;
            boolean updateClient = false;
            Client cl = DAOUtils.findClientByGuid(em, emptyIfNull(pupil.getGuid()));
            if (cl == null) {
                fieldConfig = new ClientManager.ClientFieldConfig();
            } else {
                fieldConfig = new ClientManager.ClientFieldConfigForUpdate();
            }
            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.CLIENT_GUID, pupil.getGuid(),
                    cl == null ? null : cl.getClientGUID(),
                    updateClient);// fieldConfig.setValue(ClientManager.FieldId.CLIENT_GUID, pupil.getGuid());
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
                if (o.getGuid().equals(pupil.getGuidOfOrg())) {
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
                            ClientManager.registerClientTransactionFree(org.getIdOfOrg(),
                                    (ClientManager.ClientFieldConfig) fieldConfig, true, session);
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
    }

    public static class OrgRegistryGUIDInfo {

        Set<String> orgGuids;
        String guidInfo;

        public OrgRegistryGUIDInfo(Org org) {
            Set<Org> orgs = DAOService.getInstance().getFriendlyOrgs(org.getIdOfOrg());
            orgGuids = new HashSet<String>();
            guidInfo = "";
            for (Org o : orgs) {
                if (o.getGuid() == null) {
                    continue;
                }
                if (guidInfo.length() > 0) {
                    guidInfo += ", ";
                }
                guidInfo += o.getOrgNumberInName() + ": " + o.getGuid();
                orgGuids.add(o.getGuid());
            }
        }

        public Set<String> getOrgGuids() {
            return orgGuids;
        }

        public String getGuidInfo() {
            return guidInfo;
        }
    }

    public RegistryChange getRegistryChange(Long idOfRegistryChange) {
        if (idOfRegistryChange == null) {
            return null;
        }
        RegistryChange change = em.find(RegistryChange.class, idOfRegistryChange);
        return change;
    }

    public RegistryChangeError getRegistryChangeError(Long idOfRegistryChangeError) {
        if (idOfRegistryChangeError == null) {
            return null;
        }
        RegistryChangeError e = em.find(RegistryChangeError.class, idOfRegistryChangeError);
        return e;
    }

    @Transactional
    public void applyRegistryChange(long idOfRegistryChange, boolean fullNameValidation) throws Exception {
        RegistryChange change = em.find(RegistryChange.class, idOfRegistryChange);
        Session session = null;
        try {
            session = (Session) em.getDelegate();
        } catch (Exception e) {
            logger.error("Failed to craete session", e);
            throw e;
        }


        Client dbClient = null;
        if (change.getIdOfClient() != null) {
            dbClient = em.find(Client.class, change.getIdOfClient());
        }

        switch (change.getOperation()) {
            case CREATE_OPERATION:
                //  добавление нового клиента
                String notifyByPush = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS) ? "1" : "0";
                FieldProcessor.Config createConfig = new ClientManager.ClientFieldConfig();
                createConfig.setValue(ClientManager.FieldId.CLIENT_GUID, change.getClientGUID());
                createConfig.setValue(ClientManager.FieldId.SURNAME, change.getSurname());
                createConfig.setValue(ClientManager.FieldId.NAME, change.getFirstName());
                createConfig.setValue(ClientManager.FieldId.SECONDNAME, change.getSecondName());
                createConfig.setValue(ClientManager.FieldId.GROUP, change.getGroupName());
                createConfig.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, notifyByPush);
                ClientManager.registerClientTransactionFree(change.getIdOfOrg(),
                        (ClientManager.ClientFieldConfig) createConfig, fullNameValidation, session);
                break;
            case DELETE_OPERATION:
                ClientGroup deletedClientGroup = DAOUtils
                        .findClientGroupByGroupNameAndIdOfOrg(session, change.getIdOfOrg(),
                                ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                if (deletedClientGroup == null) {
                    deletedClientGroup = DAOUtils.createClientGroup(session, change.getIdOfOrg(),
                            ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                }
                dbClient.setIdOfClientGroup(deletedClientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                session.save(dbClient);
                break;
            case MOVE_OPERATION:
                Org newOrg = em.find(Org.class, change.getIdOfMigrateOrgTo());
                addClientMigrationEntry(session, dbClient.getOrg(), newOrg, dbClient, change);
                dbClient.setOrg(newOrg);
            case MODIFY_OPERATION:
                Org newOrg1 = em.find(Org.class, change.getIdOfOrg());
                if (dbClient.getOrg().getIdOfOrg() == change.getIdOfOrg()) {
                    addClientGroupMigrationEntry(session, dbClient.getOrg(), dbClient, change); //если орг. не меняется, добавляем историю миграции внутри ОО
                }
                String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
                FieldProcessor.Config modifyConfig = new ClientManager.ClientFieldConfigForUpdate();
                modifyConfig.setValue(ClientManager.FieldId.CLIENT_GUID, change.getClientGUID());
                modifyConfig.setValue(ClientManager.FieldId.SURNAME, change.getSurname());
                modifyConfig.setValue(ClientManager.FieldId.NAME, change.getFirstName());
                modifyConfig.setValue(ClientManager.FieldId.SECONDNAME, change.getSecondName());
                modifyConfig.setValue(ClientManager.FieldId.GROUP, change.getGroupName());
                if (dbClient.getOrg().getIdOfOrg() != change.getIdOfOrg()) {
                    addClientMigrationEntry(session, dbClient.getOrg(), newOrg1, dbClient, change); //орг. меняется - история миграции между ОО
                    dbClient.setOrg(newOrg1);
                }
                ClientManager.modifyClientTransactionFree((ClientManager.ClientFieldConfigForUpdate) modifyConfig,
                        newOrg1, String.format(MskNSIService.COMMENT_AUTO_MODIFY, date),
                        dbClient, session, true);

                break;
            default:
                logger.error("Unknown update registry change operation " + change.getOperation());
        }
        change.setApplied(true);
        session.update(change);
    }

    @Transactional
    private void addClientMigrationEntry(Session session,Org oldOrg, Org newOrg, Client client, RegistryChange change){
        ClientMigration migration = new ClientMigration(client, newOrg, oldOrg);
        migration.setComment(ClientMigration.MODIFY_IN_REGISTRY);
        migration.setOldGroupName(client.getClientGroup().getGroupName());
        migration.setNewGroupName(change.getGroupName());
        session.save(migration);
    }

    @Transactional
    private void addClientGroupMigrationEntry(Session session,Org org, Client client, RegistryChange change){
        ClientGroupMigrationHistory migration = new ClientGroupMigrationHistory(org,client);
        migration.setComment(ClientGroupMigrationHistory.MODIFY_IN_REGISTRY);
        migration.setOldGroupId(client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup());
        migration.setOldGroupName(client.getClientGroup().getGroupName());
        migration.setNewGroupName(change.getGroupName());
        session.save(migration);
    }

    @Transactional
    public void setChangeError(long idOfRegistryChange, Exception e) throws Exception {
        RegistryChange change = em.find(RegistryChange.class, idOfRegistryChange);
        Session session = null;
        try {
            session = (Session) em.getDelegate();
        } catch (Exception ex) {
            logger.error("Failed to craete session", e);
            throw ex;
        }
        String err = e.getMessage();
        if (err.length() > 255) {
            err = err.substring(0, 255).trim();
        }
        change.setError(err);
        session.update(change);
    }

    @Transactional
    public StringBuffer syncClientsWithRegistry(long idOfOrg, boolean performChanges, StringBuffer logBuffer,
            boolean manualCheckout) throws Exception {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        Org org = em.find(Org.class, idOfOrg);
        String synchDate = "[Синхронизация с Реестрами от " + date + " для " + org.getIdOfOrg() + "]: ";
        OrgRegistryGUIDInfo orgGuids = new OrgRegistryGUIDInfo(org);
        log(synchDate + "Производится синхронизация для " + org.getOfficialName() + " GUID [" + orgGuids.getGuidInfo()
                + "]", logBuffer);

        //Проверка на устаревшие гуиды организаций
        OrgMskNSIService service = RuntimeContext.getAppContext().getBean(OrgMskNSIService.class);
        List<String> list = service.getBadGuids(orgGuids.orgGuids);
        if (list != null && !list.isEmpty()) {
            String badGuids = "Найдены следующие неактуальные GUIDы организаций:\n";
            for (String g : list) {
                badGuids += g;
            }
            throw new BadOrgGuidsException(badGuids); // UnsupportedOperationException(badGuids);
        }

        //  Итеративно загружаем клиентов, используя ограничения
        List<ExpandedPupilInfo> pupils = nsiService.getPupilsByOrgGUID(orgGuids.orgGuids, null, null, null);//test();
        log(synchDate + "Получено " + pupils.size() + " записей", logBuffer);
        //  !!!!!!!!!!
        //  !!!!!!!!!!
        //  parseClients(synchDate, date, org, pupils, performChanges, logBuffer, manualCheckout);
        //  !!!!!!!!!!
        //  !!!!!!!!!!
        saveClients(synchDate, date, System.currentTimeMillis(), org, pupils, logBuffer);
        return logBuffer;
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

    private static String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    /*
    // Скедулинг идет через Spring
    // возможно если нужно будет настраиваемое расписание взять этот код

    public static class SyncJob implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(NSISyncService.class).doSync();
        }
    }

    final static String JOB_NAME="sync";


    public void scheduleSync() throws Exception {
        mskNSIService.init();

        String syncSchedule = RuntimeContext.getInstance().getNsiServiceConfig().syncSchedule;
        logger.info("Scheduling NSI sync job: "+syncSchedule);
        JobDetail jobDetail = new JobDetail(JOB_NAME,Scheduler.DEFAULT_GROUP, SyncJob.class);

        CronTrigger trigger = new CronTrigger(JOB_NAME, Scheduler.DEFAULT_GROUP);
        //trigger.setStartTime(new Date());
        //trigger.setEndTime(new Date(new Date().getTime() + 10 * 60 * 1000));
        trigger.setCronExpression(syncSchedule);

        SchedulerFactory sfb = new StdSchedulerFactory();
        Scheduler scheduler = sfb.getScheduler();
        if (scheduler.getTrigger(JOB_NAME, Scheduler.DEFAULT_GROUP)!=null) {
            scheduler.deleteJob(JOB_NAME, Scheduler.DEFAULT_GROUP);
        }
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    } */


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
        public String birthDate;

        public boolean deleted;
        public boolean created;
        public String guidOfOrg;
        public String recordState;

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

        public String getRecordState() {
            return recordState;
        }

        public void setRecordState(String recordState) {
            this.recordState = recordState;
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

        public void copyFrom(ExpandedPupilInfo pi) {
            this.familyName = pi.familyName;
            this.firstName = pi.firstName;
            this.secondName = pi.secondName;
            this.guid = pi.guid;
            this.group = pi.group;
            this.birthDate = pi.birthDate;
            this.deleted = pi.deleted;
            this.created = pi.created;
            this.guidOfOrg = pi.guidOfOrg;
            this.recordState = pi.recordState;
            this.enterGroup = pi.enterGroup;
            this.enterDate = pi.enterDate;
            this.leaveDate = pi.leaveDate;
        }
    }
}