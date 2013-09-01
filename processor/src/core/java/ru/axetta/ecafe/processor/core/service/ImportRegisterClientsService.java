/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.mail.File;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static final int MAX_CLIENTS_PER_TRANSACTION = 50;
    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Autowired
    MskNSIService nsiService;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterClientsService.class);
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final String ORG_SYNC_MARKER = "СИНХРОНИЗАЦИЯ_РЕЕСТРЫ";


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
                .syncClientsWithRegistry(org, performChanges, logBuffer, true);
    }


    public void run() throws IOException {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            return;
        }
        List<Org> orgs = DAOService.getInstance().getOrderedSynchOrgsList();

        int maxAttempts = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_MSK_NSI_MAX_ATTEMPTS);
        for (Org org : orgs) {
            if (org.getTag() == null || !org.getTag().toUpperCase().contains(ORG_SYNC_MARKER)) {
                continue;
            }
            int attempt = 0;
            while (attempt < maxAttempts) {
                try {
                    RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class)
                            .syncClientsWithRegistry(org, true, null, false);
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
            Long currGroupId = currGroup==null?null:currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
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
        for (Client dbClient: clientsToRemove) {
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
                        cl == null ? null : cl.getClientGroup().getGroupName(), updateClient);
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
                        emptyIfNull(cl.getClientGroup().getGroupName()) + " из школы " + cl.getOrg().getIdOfOrg()
                        + " в школу " + newOrg.getIdOfOrg(), logBuffer);
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
                            emptyIfNull(cl.getClientGroup().getGroupName()) + " на " +
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
            guidInfo="";
            for (Org o : orgs) {
                if (o.getGuid()==null) continue;
                if (guidInfo.length()>0) guidInfo+=", ";
                guidInfo+=o.getOrgNumberInName()+": "+o.getGuid();
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
    
    @Transactional
    public StringBuffer syncClientsWithRegistry(Org org, boolean performChanges, StringBuffer logBuffer, boolean manualCheckout) throws Exception {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Синхронизация с Реестрами от " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(System.currentTimeMillis())) + " для " + org.getIdOfOrg() + "]: ";
        org = em.find(Org.class, org.getIdOfOrg());
        OrgRegistryGUIDInfo orgGuids = new OrgRegistryGUIDInfo(org);
        log(synchDate + "Производится синхронизация для " + org.getOfficialName()+" GUID ["+orgGuids.getGuidInfo()+"]", logBuffer);

        //  Итеративно загружаем клиентов, используя ограничения
        List<ExpandedPupilInfo> pupils = nsiService.getPupilsByOrgGUID(orgGuids.orgGuids, null, null, null);
        log(synchDate + "Получено " + pupils.size() +" записей", logBuffer);
        parseClients(synchDate, date, org, pupils, performChanges, logBuffer, manualCheckout);
        return logBuffer;
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


    private static void log(String str, StringBuffer logBuffer) {
        if (logBuffer != null) {
            logBuffer.append(str).append('\n');
        }
        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_LOG)) {
            logger.info(str);
        }
    }

    private static void logError(String str, Exception e, StringBuffer logBuffer) {
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
        if (group!=null && group.toLowerCase().startsWith("дошкол")) {
            return true;
        }
        if (guid==null || guid.length()==0) {
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