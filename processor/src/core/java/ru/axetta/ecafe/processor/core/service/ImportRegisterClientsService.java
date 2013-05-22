/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
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
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    @PersistenceContext
    EntityManager em;

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
            logger.error("Failed to parse date from options", e);
        }
        return new Date(0);
    }


    public void run() throws IOException {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            return;
        }
        Date lastUpd = getLastUpdateDate();
        List<Org> orgs = DAOService.getInstance().getOrderedSynchOrgsList();


        int maxAttempts = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_MSK_NSI_MAX_ATTEMPTS);
        boolean allOperationsAreFinished = true;
        for (Org org : orgs) {
            if (org.getTag() == null || !org.getTag().toUpperCase().contains(ORG_SYNC_MARKER)) {
                continue;
            }
            int attempt = 0;
            while (attempt < maxAttempts) {
                try {
                    RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).loadClients(lastUpd, org);
                    break;
                } catch (SocketTimeoutException ste) {
                } catch (Exception e) {
                    logger.error("Ошибка при синхронизации с Реестрами для организации: " + org.getIdOfOrg(), e);
                    break;
                } finally {
                    attempt++;
                }
            }
            if (attempt >= maxAttempts) {
                allOperationsAreFinished = false;
                logger.error("Неудалось подключиться к сервису, превышено максимальное количество попыток (" + maxAttempts +")");
            }
        }
        //  Если была хотя бы одна неудачная загрузка данных с сервиса, время последный синхронизации не обновляем!
        //if (allOperationsAreFinished) {
            setLastUpdateDate(new Date(System.currentTimeMillis()));
        //}
    }


    @Transactional
    public void loadClients(java.util.Date lastUpd, Org org) throws Exception {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Синхронизация с Реестрами от " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())) + " для " + org.getIdOfOrg() + "]: ";
        log(synchDate + "Производится синхронизация для " + org.getOfficialName());

        Session session = (Session) em.getDelegate();
        org = em.merge(org);
        List<MskNSIService.ExpandedPupilInfo> pupils = null;
        try {
            pupils = nsiService.getChangedClients(lastUpd, org);
        } catch (Exception e) {
            logger.error("Ошибка получения данных от Реестров для "+org.getOfficialName(), e);
            return;
        }

        try {

            //  Проходим по всем существующим клиентам ОУ
            List<Client> currentClients = DAOUtils.findClientsForOrgAndFriendly (em, org);
            List<Org> orgsList = DAOUtils.findFriendlyOrgs (em, org);   //  Текущая организация и дружественные ей
            orgsList.add(org);
            for (Client dbClient : currentClients) {
                boolean found = false;
                for (MskNSIService.ExpandedPupilInfo pupil : pupils) {
                    if (pupil.getGuid().equals(dbClient.getClientGUID())) {
                        found = true;
                        break;
                    }
                }
                ClientGroup currGroup = dbClient.getClientGroup();
                //  Если клиент из Реестров не найден используя GUID из ИС ПП и группа у него еще не "Отчисленные", то заносим его в эту группу
                if (!found && !emptyIfNull(dbClient.getClientGUID()).equals("") && currGroup != null &&
                    !currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup().equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue())) {
                    ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(session, dbClient.getOrg().getIdOfOrg(),
                                                                                            ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                    if (clientGroup == null) {
                        clientGroup = DAOUtils.createNewClientGroup(session, dbClient.getOrg().getIdOfOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                    }
                    log(synchDate + "Требует произвести удаление клиента " +
                        emptyIfNull(dbClient.getClientGUID()) + ", " + emptyIfNull(dbClient.getPerson().getSurname()) + " " +
                        emptyIfNull(dbClient.getPerson().getFirstName()) + " " + emptyIfNull(dbClient.getPerson().getSecondName()) + ", " +
                        emptyIfNull(dbClient.getClientGroup().getGroupName()));
                    dbClient.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    session.save(dbClient);
                }
            }

            //  Проходим по ответу от Реестров и анализируем надо ли обновлять его или нет
            for (MskNSIService.ExpandedPupilInfo pupil : pupils) {
                FieldProcessor.Config fieldConfig;
                boolean updateClient = false;
                Client cl = DAOUtils.findClientByGuid(em, emptyIfNull(pupil.getGuid()));
                if (cl == null) {
                    fieldConfig = new ClientManager.ClientFieldConfig();
                } else {
                    fieldConfig = new ClientManager.ClientFieldConfigForUpdate();
                }
                updateClient = doClientUpdate (fieldConfig, ClientManager.FieldId.CLIENT_GUID,
                                               pupil.getGuid(), cl == null ? null : cl.getClientGUID(), updateClient);// fieldConfig.setValue(ClientManager.FieldId.CLIENT_GUID, pupil.getGuid());
                updateClient = doClientUpdate (fieldConfig, ClientManager.FieldId.SURNAME,
                                               pupil.getFamilyName(), cl == null ? null : cl.getPerson().getSurname(), updateClient);
                updateClient = doClientUpdate (fieldConfig, ClientManager.FieldId.NAME,
                                               pupil.getFirstName(), cl == null ? null : cl.getPerson().getFirstName(), updateClient);
                updateClient = doClientUpdate (fieldConfig, ClientManager.FieldId.SECONDNAME,
                                               pupil.getSecondName(), cl == null ? null : cl.getPerson().getSecondName(), updateClient);
                if (pupil.getGroup() != null) {
                    updateClient = doClientUpdate (fieldConfig, ClientManager.FieldId.GROUP,
                                                   pupil.getGroup(), cl == null ? null : cl.getClientGroup().getGroupName(), updateClient);
                }
                //  Проверяем организацию и дружественные ей - если клиент был переведен из другого ОУ, то перемещаем его
                boolean guidFound = false;
                for (Org o : orgsList) {
                    if (o.getGuid().equals(pupil.getGuidOfOrg())) {
                        guidFound = true;
                        break;
                    }
                }
                if (cl != null && !guidFound) {
                    Org newOrg = DAOService.getInstance().getOrgByGuid (pupil.getGuidOfOrg());
                    log(synchDate + "Клиент " + emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(cl.getPerson().getSurname()) + " " +
                            emptyIfNull(cl.getPerson().getFirstName()) + " " + emptyIfNull(cl.getPerson().getSecondName()) + ", " +
                            emptyIfNull(cl.getClientGroup().getGroupName()) + " был переведен из школы " + cl.getOrg().getIdOfOrg() + " в школу " + newOrg.getIdOfOrg());
                    cl.setOrg(newOrg);
                    updateClient = true;
                }



                if (!updateClient) {
                    continue;
                }
                try {
                    //  Если клиента по GUID найти не удалось, это значит что он новый - добавляем его
                    if (cl == null) {
                        try {
                            log(synchDate + "Требуется добавление нового клинета " + pupil.getGuid() + ", " +
                                pupil.getFamilyName() + " " + pupil.getFirstName() + " " +
                                pupil.getSecondName() + ", " + pupil.getGroup());
                            fieldConfig.setValue(ClientManager.FieldId.COMMENTS,
                                    String.format(MskNSIService.COMMENT_AUTO_IMPORT, date));
                            ClientManager
                                    .registerClientTransactionFree(org.getIdOfOrg(), (ClientManager.ClientFieldConfig) fieldConfig,
                                            true, session);
                        } catch (Exception e) {
                            // Не раскомментировать, очень много исключений будет из-за дублирования клиентов
                        }
                    //  Иначе - обновляем клиента в БД
                    } else {
                        log(synchDate + "Требуется внести изменения в учетную запись существующего пользователя " +
                                emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(cl.getPerson().getSurname()) + " " +
                                emptyIfNull(cl.getPerson().getFirstName()) + " " + emptyIfNull(cl.getPerson().getSecondName()) + ", " +
                                emptyIfNull(cl.getClientGroup().getGroupName()) + " на " +
                                emptyIfNull(pupil.getGuid()) + ", " + emptyIfNull(pupil.getFamilyName()) + " " + emptyIfNull(pupil.getFirstName()) + " " +
                                emptyIfNull(pupil.getSecondName()) + ", " + emptyIfNull(pupil.getGroup()));
                        ClientManager.modifyClientTransactionFree((ClientManager.ClientFieldConfigForUpdate) fieldConfig, org,
                                String.format(MskNSIService.COMMENT_AUTO_MODIFY, date), cl, session);
                    }
                } catch (Exception e) {
                    logger.error("Failed to add client for " + org.getIdOfOrg() + " org", e);
                }
            }
        } finally {
        }
        log(synchDate + "Синхронизация завершена для " + org.getOfficialName());
    }

    public boolean doClientUpdate (FieldProcessor.Config fieldConfig, Object fieldID,
                                  String reesterValue, String currentValue, boolean doClientUpdate) throws Exception {
        reesterValue = emptyIfNull(reesterValue);
        currentValue = emptyIfNull(currentValue);
        fieldConfig.setValue(fieldID, reesterValue);
        return doClientUpdate || !currentValue.trim().equals(reesterValue.trim());
    }

    private String emptyIfNull(String str) {
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


    private void log (String str) {
        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_LOG)) {
            logger.info(str);
        }
    }
}