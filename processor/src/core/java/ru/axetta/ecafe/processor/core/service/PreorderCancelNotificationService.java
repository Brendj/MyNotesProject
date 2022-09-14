/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: a.voinov
 * Date: 04.03.21
 */

@Component
@Scope("singleton")
public class PreorderCancelNotificationService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PreorderCancelNotificationService.class);
    final static String CANCEL_PREORDER_NOTIFICATION = "CancelPreorderNotification";
    final EventNotificationService notificationService = RuntimeContext.getAppContext()
            .getBean(EventNotificationService.class);
    public static class sendNotification implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                logger.info("Start PreorderCancelNotificationService");
                RuntimeContext.getAppContext().getBean(PreorderCancelNotificationService.class)
                        .start();
                logger.info("End PreorderCancelNotificationService");
            } catch (Exception e) {
                logger.error("Error in PreorderCancelNotificationService.manualStart: ", e);
            }
        }

        public static void manualStart() throws JobExecutionException {
            try {
                logger.info("Start PreorderCancelNotificationService");
                RuntimeContext.getAppContext().getBean(PreorderCancelNotificationService.class)
                        .start();
                logger.info("End PreorderCancelNotificationService");
            } catch (Exception e) {
                logger.error("Error in PreorderCancelNotificationService.manualStart: ", e);
            }
        }
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().
                getProperty("ecafe.processor.preorder.cancel.notification.node", "1");
        String[] nodes = reqInstance.split(",");
        for (String node : nodes) {
            if (!StringUtils.isBlank(instance) && !StringUtils.isBlank(reqInstance)
                    && instance.trim().equals(node.trim())) {
                return true;
            }
        }
        return false;
    }

    public void scheduleSync() throws Exception {
        if (!isOn())
            return;
        String syncScheduleSync = RuntimeContext.getInstance().getConfigProperties().
                getProperty("ecafe.processor.preorder.cancel.notification.time", "0 0 8 ? * * *");
        try {
            JobDetail jobDetailSync = new JobDetail(CANCEL_PREORDER_NOTIFICATION, Scheduler.DEFAULT_GROUP, sendNotification.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();

            CronTrigger triggerSync = new CronTrigger(CANCEL_PREORDER_NOTIFICATION, Scheduler.DEFAULT_GROUP);
            triggerSync.setCronExpression(syncScheduleSync);
            if (scheduler.getTrigger(CANCEL_PREORDER_NOTIFICATION, Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob(CANCEL_PREORDER_NOTIFICATION, Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(jobDetailSync, triggerSync);
            scheduler.start();
        } catch (Exception e) {
        }
    }


    public void start() throws Exception {
        try {
            logger.info("Start service for 23 type notifications");
            Map<Client, Object> newMessage = new HashMap<>();
            Session persistenceSession1 = null;
            Transaction persistenceTransaction1 = null;
            try {
                logger.info("Start new Session for get new data");
                persistenceSession1 = RuntimeContext.getInstance().createPersistenceSession();
                persistenceTransaction1 = persistenceSession1.beginTransaction();
                //Получаем новые данные для отправления
                newMessage = getNewNotification(persistenceSession1);
                persistenceTransaction1.commit();
                persistenceTransaction1 = null;
                logger.info("End new Session for get new data");
            } catch (Exception e) {
                logger.error("Error in new Session for get new data: ", e);
            } finally {
                HibernateUtils.rollback(persistenceTransaction1, logger);
                HibernateUtils.close(persistenceSession1, logger);
            }

            //
            String goodtime = RuntimeContext.getInstance().getConfigProperties().
                    getProperty("ecafe.processor.preorder.cancel.notification.goodtime", "08:00-22:00");
            String goodH = goodtime.substring(0, 5);
            String goodM = goodtime.substring(6, 11);
            DateFormat format = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
            Date dateH = format.parse(goodH);
            Date dateM = format.parse(goodM);
            //


            Long currentTime = new Date().getTime() - CalendarUtils.startOfDay(new Date()).getTime();
            // if (currentTime >= 28800000 && currentTime < 79200000) //Отправка только в рабочее время (с 8:00 до 22:00)
            if (currentTime > (dateH.getTime() + 10800000) &&
                    currentTime < (dateM.getTime() + 10800000)) {
                Map<Client, Object> oldMessage = new HashMap<>();
                logger.info("Start sending a message because the time is right");
                Session persistenceSession2 = null;
                Transaction persistenceTransaction2 = null;
                try {
                    logger.info("Start new Session for get data from db");
                    persistenceSession2 = RuntimeContext.getInstance().createPersistenceSession();
                    persistenceTransaction2 = persistenceSession2.beginTransaction();
                    //Получаем данные из БД, которые не отправлены при предидущим вызовом сервиса
                    oldMessage = convertResultFromDB(persistenceSession2);
                    persistenceTransaction2.commit();
                    persistenceTransaction2= null;
                    logger.info("End new Session for get data from db");
                } catch (Exception e) {
                    logger.error("Error in new Session for get data from db: ", e);
                } finally {
                    HibernateUtils.rollback(persistenceTransaction2, logger);
                    HibernateUtils.close(persistenceSession2, logger);
                }

                //Отправляем сообщение
                logger.info("Start send oldMessage");
                Session persistenceSession3 = null;
                Transaction persistenceTransaction3 = null;
                try {
                    logger.info("Start new Session for really send old Message");
                    persistenceSession3 = RuntimeContext.getInstance().createPersistenceSession();
                    persistenceTransaction3 = persistenceSession3.beginTransaction();
                    reallySendNotification(persistenceSession3, oldMessage);
                    persistenceTransaction3.commit();
                    persistenceTransaction3 = null;
                    logger.info("End new Session for really send old Message");
                } catch (Exception e) {
                    logger.error("Error in  new Session for really send old Message: ", e);
                } finally {
                    HibernateUtils.rollback(persistenceTransaction3, logger);
                    HibernateUtils.close(persistenceSession3, logger);
                }

                logger.info("End send oldMessage");
                logger.info("Start send newMessage");
                Session persistenceSession4 = null;
                Transaction persistenceTransaction4 = null;
                try {
                    logger.info("Start new Session for really send new Message");
                    persistenceSession4 = RuntimeContext.getInstance().createPersistenceSession();
                    persistenceTransaction4 = persistenceSession4.beginTransaction();
                    reallySendNotification(persistenceSession4, newMessage);
                    persistenceTransaction4.commit();
                    persistenceTransaction4 = null;
                    logger.info("End new Session for really send new Message");
                } catch (Exception e) {
                    logger.error("Error in new Session for really send new Message: ", e);
                } finally {
                    HibernateUtils.rollback(persistenceTransaction4, logger);
                    HibernateUtils.close(persistenceSession4, logger);
                }
                logger.info("End send newMessage");

                Session persistenceSession5 = null;
                Transaction persistenceTransaction5 = null;
                try {
                    logger.info("Start new Session for clear table");
                    persistenceSession5 = RuntimeContext.getInstance().createPersistenceSession();
                    persistenceTransaction5 = persistenceSession5.beginTransaction();
                    //Очищаем таблицу сообщений
                    logger.info("Start clear table notification");
                    DAOUtils.clearCancelNotificationTable(persistenceSession5);
                    logger.info("End clear table notification");
                    persistenceTransaction5.commit();
                    persistenceTransaction5 = null;
                    logger.info("End new Session for for clear table");
                } catch (Exception e) {
                    logger.error("Error in new Session for really send new Message: ", e);
                } finally {
                    HibernateUtils.rollback(persistenceTransaction5, logger);
                    HibernateUtils.close(persistenceSession5, logger);
                }

                logger.info("End sending a message because the time is right");
            } else {

                RuntimeContext runtimeContext = RuntimeContext.getInstance();
                Session persistenceSession = null;
                Transaction persistenceTransaction = null;
                try {
                    persistenceSession = runtimeContext.createPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    logger.info("Saving the message to the database because the time is not right");
                    //Сохраняем новые данные в БД
                    convertResultToDB(persistenceSession, newMessage);
                    logger.info("End saving the message to the database because the time is not right");
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                } catch (Exception e) {
                    logger.error("Error in PreorderCancelNotificationService.manualStart: ", e);
                } finally {
                    HibernateUtils.rollback(persistenceTransaction, logger);
                    HibernateUtils.close(persistenceSession, logger);
                }
            }
            logger.info("End service for 23 type notifications");
        } catch (Exception e)
        {
            logger.error("Error in service for 23 type notifications", e);
        }
    }

    private void reallySendNotification(Session session, Map<Client, Object> messages) throws Exception {
        Iterator<Map.Entry<Client, Object>> clients = messages.entrySet().iterator();
        int counttype = 1;
        String value = "<br>";
        while (clients.hasNext()) {
            Map.Entry<Client, Object> client = clients.next();
            try {
                String[] values = new String[]{
                        "balance", String.valueOf(client.getKey().getBalance())};
                List<Client> guardians = ClientManager
                        .findGuardiansByClientNew(session, client.getKey().getIdOfClient());
                Iterator<Map.Entry<String, Object>> types =
                        ((Map<String, Object>) client.getValue()).entrySet().iterator();
                while (types.hasNext()) {
                    Map.Entry<String, Object> type = types.next();
                    counttype = 1;
                    Iterator<Map.Entry<Date, String>> datemessages =
                            ((Map<Date, String>) type.getValue()).entrySet().iterator();
                    while (datemessages.hasNext()) {
                        Map.Entry<Date, String> datemessage = datemessages.next();
                        value = "<br>";
                        value += new SimpleDateFormat("dd-MM-yyyy").format(datemessage.getKey());
                        value += datemessage.getValue();
                        values = EventNotificationService.attachToValues
                                (type.getKey() + "_den_" + counttype, value, values);
                        counttype++;
                    }
                }

                try {
                    logger.info("Start send to guard for client: " + client.getKey().getIdOfClient());
                    //отправка представителям
                    if (!(guardians == null || guardians.isEmpty())) {
                        Session persistenceSession1 = null;
                        Transaction persistenceTransaction1 = null;
                        try {
                            logger.info("Start new Session for send message for guards");
                            persistenceSession1 = RuntimeContext.getInstance().createPersistenceSession();
                            persistenceTransaction1 = persistenceSession1.beginTransaction();
                            for (Client destGuardian : guardians) {
                                logger.info("Start send message for guard: " + destGuardian.getIdOfClient());
                                if (ClientManager.allowedGuardianshipNotificationNew(persistenceSession1, destGuardian.getIdOfClient(),
                                        client.getKey().getIdOfClient(),
                                        ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SPECIAL.getValue())) {
                                    logger.info("Send message async for guard: " + destGuardian.getIdOfClient());
                                    notificationService
                                            .sendNotificationAsync(destGuardian, client.getKey(), EventNotificationService.NOTIFICATION_CANCEL_PREORDER, values, new Date());
                                }
                                logger.info("End send message for guard: " + destGuardian.getIdOfClient());
                            }
                            persistenceTransaction1.commit();
                            persistenceTransaction1 = null;
                            logger.info("End new Session for send message for guards");
                        } catch (Exception e) {
                            logger.error("Error in  new Session for send message for guards ", e);
                        } finally {
                            HibernateUtils.rollback(persistenceTransaction1, logger);
                            HibernateUtils.close(persistenceSession1, logger);
                        }
                    }
                    logger.info("End send to guard for client: " + client.getKey().getIdOfClient());
                } catch (Exception e) {
                    logger.error("Error send to guard for client: " + client.getKey().getIdOfClient());
                }

                logger.info("Send message async for client: " + client.getKey().getIdOfClient());
                //отправка клиенту
                notificationService.sendNotificationAsync(client.getKey(), null, EventNotificationService.NOTIFICATION_CANCEL_PREORDER, values, new Date());
            } catch (Exception e)
            {
                logger.error("Error in really send message for client: " + client.getKey().getIdOfClient());
            }
        }
    }


    public Map<Client, Object> getNewNotification(Session session) {

        Map<Client, Object> result =
                new HashMap<Client, Object>();
        List<PreorderComplex> preorderComplexs;
        try {
            logger.info("Start get contentDeletedPreorderDishOtherOO");
            preorderComplexs = DAOUtils.getContentDeletedPreorderDishOtherOO(session, new Date());
            setFlagSended(session, preorderComplexs);
            preorderComplexs = getUniquleResult(preorderComplexs);
            createData(preorderComplexs, "contentDeletedPreorderDishOtherOO", 1, result);
            logger.info("End get contentDeletedPreorderDishOtherOO: " + preorderComplexs.size());
        } catch (Exception e)
        {
            logger.error("Error in get contentDeletedPreorderDishOtherOO: ", e);
        }

        try {
            logger.info("Start get contentDeletedPreorderDishOtherCash");
            preorderComplexs = DAOUtils.getcontentDeletedPreorderDishOtherCash(session, new Date());
            setFlagSended(session, preorderComplexs);
            preorderComplexs = getUniquleResult(preorderComplexs);
            createData(preorderComplexs, "contentDeletedPreorderDishOtherCash", 1, result);
            logger.info("End get contentDeletedPreorderDishOtherCash: " + preorderComplexs.size());
        } catch (Exception e)
        {
            logger.error("Error in get contentDeletedPreorderDishOtherCash: ", e);
        }

        try {
            logger.info("Start get contentDeletedPreorderDishOtherPP");
            preorderComplexs = DAOUtils.getContentDeletedPreorderDishOtherPP(session, new Date());
            setFlagSended(session, preorderComplexs);
            preorderComplexs = getUniquleResult(preorderComplexs);
            createData(preorderComplexs, "contentDeletedPreorderDishOtherPP", 2, result);
            logger.info("End get contentDeletedPreorderDishOtherPP: " + preorderComplexs.size());
        } catch (Exception e)
        {
            logger.error("Error in get contentDeletedPreorderDishOtherPP: ", e);
        }

        try {
            logger.info("Start get contentDeletedPreorderOtherOO");
            preorderComplexs = DAOUtils.getContentDeletedPreorderOtherOO(session, new Date());
            setFlagSended(session, preorderComplexs);
            preorderComplexs = getUniquleResult(preorderComplexs);
            createData(preorderComplexs, "contentDeletedPreorderOtherOO", 3, result);
            logger.info("End get contentDeletedPreorderOtherOO: " + preorderComplexs.size());
        } catch (Exception e)
        {
            logger.error("Error in get contentDeletedPreorderOtherOO: ", e);
        }

        try {
            logger.info("Start get contentDeletedPreorderOtherCash");
            preorderComplexs = DAOUtils.getContentDeletedPreorderOtherCash(session, new Date());
            setFlagSended(session, preorderComplexs);
            preorderComplexs = getUniquleResult(preorderComplexs);
            createData(preorderComplexs, "contentDeletedPreorderOtherCash", 3, result);
            logger.info("End get contentDeletedPreorderOtherCash: " + preorderComplexs.size());
        } catch (Exception e)
        {
            logger.error("Error in get contentDeletedPreorderOtherCash: ", e);
        }

        try {
            logger.info("Start get contentDeletedPreorderOtherPP");
            preorderComplexs = DAOUtils.getContentDeletedPreorderOtherPP(session, new Date());
            setFlagSended(session, preorderComplexs);
            preorderComplexs = getUniquleResult(preorderComplexs);
            createData(preorderComplexs, "contentDeletedPreorderOtherPP", 4, result);
            logger.info("End get contentDeletedPreorderOtherPP: " + preorderComplexs.size());
        } catch (Exception e)
        {
            logger.error("Error in get contentDeletedPreorderOtherPP: ", e);
        }

        List<RegularPreorder> regularPreorders;
        try {
            logger.info("Start get contentDeletedPreorderOtherRegularOO");
            regularPreorders = DAOUtils.getContentDeletedPreorderOtherRegularOO(session, new Date());
            setFlagSendedRegular(session, regularPreorders);
            regularPreorders = getUniquleResultRegular(regularPreorders);
            createDataRegularPreorder(session, regularPreorders, "contentDeletedPreorderOtherRegularOO", 1, result);
            logger.info("End get contentDeletedPreorderOtherRegularOO: " + regularPreorders.size());
        } catch (Exception e)
        {
            logger.error("Error in get contentDeletedPreorderOtherRegularOO: ", e);
        }

        try {
            logger.info("Start get contentDeletedPreorderDishOtherRegularOO");
            regularPreorders = DAOUtils.getContentDeletedPreorderDishOtherRegularOO(session, new Date());
            setFlagSendedRegular(session, regularPreorders);
            regularPreorders = getUniquleResultRegular(regularPreorders);
            createDataRegularPreorder(session, regularPreorders, "contentDeletedPreorderDishOtherRegularOO", 2, result);
            logger.info("End get contentDeletedPreorderDishOtherRegularOO: " + regularPreorders.size());
        } catch (Exception e)
        {
            logger.error("Error in get contentDeletedPreorderDishOtherRegularOO: ", e);
        }

        return result;
    }

    private void setFlagSended (Session session, List<PreorderComplex> preorderComplexs)
    {
        for (PreorderComplex preorderComplex: preorderComplexs)
        {
            preorderComplex.setCancelnotification(true);
            session.update(preorderComplex);
        }
    }

    private void setFlagSendedRegular (Session session, List<RegularPreorder> regularPreorders)
    {
        for (RegularPreorder regularPreorder: regularPreorders)
        {
            regularPreorder.setCancelnotification(true);
            session.update(regularPreorder);
        }
    }

    private Map<Client, Object> convertResultFromDB (Session session) throws Exception {
        Map<Client, Object> result = new HashMap<>();
        logger.info("Start get data from db");
        List<CancelPreorderNotification> cancelPreorderNotifications = DAOUtils.getCancelPreorderNotification(session);
        logger.info("End get data from db: " + cancelPreorderNotifications.size());
        Map<Date, String> dateMessage = new HashMap<Date, String>();
        Map<String, Object> infoType = new HashMap<String, Object>();
        for (CancelPreorderNotification cancelPreorderNotification: cancelPreorderNotifications)
        {
            dateMessage = new HashMap<Date, String>();
            infoType = new HashMap<String, Object>();
            Client client = cancelPreorderNotification.getClient();
            if (client != null) {
                //Если такого клиента пока не было
                if (result.get(client) == null)
                {
                    dateMessage.put(cancelPreorderNotification.getPreorderdate(),
                            cancelPreorderNotification.getTextmessage());
                    infoType.put(cancelPreorderNotification.getTypename(), dateMessage);
                    result.put(client,infoType);
                }
                else
                {
                    infoType = (Map<String, Object>) result.get(client);
                    if (infoType.get(cancelPreorderNotification.getTypename()) == null)
                    {
                        dateMessage.put(cancelPreorderNotification.getPreorderdate(),
                                cancelPreorderNotification.getTextmessage());
                        infoType.put(cancelPreorderNotification.getTypename(), dateMessage);
                    }
                    else
                    {
                        dateMessage = (Map<Date, String>) infoType.get
                                (cancelPreorderNotification.getTypename());
                        dateMessage.put(cancelPreorderNotification.getPreorderdate(),
                                cancelPreorderNotification.getTextmessage());
                    }
                }
            }
        }
        return result;
    }

    private void convertResultToDB (Session session, Map<Client, Object> result)
    {
        Iterator<Map.Entry<Client, Object>> clients = result.entrySet().iterator();
        while (clients.hasNext()) {
            Map.Entry<Client, Object> client = clients.next();
            Iterator<Map.Entry<String, Object>> types =
                    ((Map<String, Object>)client.getValue()).entrySet().iterator();
            while (types.hasNext()) {
                Map.Entry<String, Object> type = types.next();

                Iterator<Map.Entry<Date, String>> datemessages =
                        ((Map<Date, String>)type.getValue()).entrySet().iterator();
                while (datemessages.hasNext()) {
                    Map.Entry<Date, String> datemessage = datemessages.next();

                    CancelPreorderNotification cancelPreorderNotification = new CancelPreorderNotification();
                    cancelPreorderNotification.setClient(client.getKey());
                    cancelPreorderNotification.setTypename(type.getKey());
                    cancelPreorderNotification.setPreorderdate(datemessage.getKey());
                    cancelPreorderNotification.setTextmessage(datemessage.getValue());
                    session.save(cancelPreorderNotification);
                }
            }
        }
    }

    private void createDataRegularPreorder(Session session, List<RegularPreorder> regularPreorders, String type,
            Integer mode, Map<Client, Object> result )
    {
        Map<Date, String> dateMessage = new HashMap<Date, String>();
        Map<String, Object> infoType = new HashMap<String, Object>();

        Map<Long, String> frations = new HashMap<Long, String>();
        List res = DAOReadonlyService.getInstance().getWtComplexsByRegular(regularPreorders);
        for (Object o : res) {
            Object[] row = (Object[]) o;
            frations.put(((BigInteger)row[0]).longValue(), (String)row[1]);
        }

        for (RegularPreorder regularPreorder: regularPreorders)
        {
            dateMessage = new HashMap<Date, String>();
            infoType = new HashMap<String, Object>();
            Client client = regularPreorder.getClient();
            if (client != null) {
                //Если такого клиента пока не было
                if (result.get(client) == null)
                {
                    String mess = "";
                    createmassageregular(session, mess, regularPreorder, frations.get(regularPreorder.getIdOfRegularPreorder()), mode, dateMessage);
                    infoType.put(type, dateMessage);
                    result.put(client,infoType);
                }
                else
                {
                    infoType = (Map<String, Object>) result.get(client);
                    if (infoType.get(type) == null)
                    {
                        String mess = "";
                        createmassageregular(session, mess, regularPreorder, frations.get(regularPreorder.getIdOfRegularPreorder()), mode, dateMessage);
                        infoType.put(type, dateMessage);
                    }
                    else
                    {
                        dateMessage = (Map<Date, String>) infoType.get(type);
                        if (dateMessage.get(regularPreorder.getEndDate()) == null)
                        {
                            String mess = "";
                            createmassageregular(session, mess, regularPreorder, frations.get(regularPreorder.getIdOfRegularPreorder()), mode, dateMessage);
                        }
                        else
                        {
                            String mess = dateMessage.get(regularPreorder.getEndDate());
                            createmassageregular(session, mess, regularPreorder, frations.get(regularPreorder.getIdOfRegularPreorder()), mode, dateMessage);
                        }
                    }
                }
            }
        }
    }

    private void createData(List<PreorderComplex> preorderComplexs, String type, Integer mode, Map<Client, Object> result)
    {
        Map<Date, String> dateMessage = new HashMap<Date, String>();
        Map<String, Object> infoType = new HashMap<String, Object>();

        Map<Long, String> frations = new HashMap<Long, String>();
        List res = DAOReadonlyService.getInstance().getWtComplexsByComplexes(preorderComplexs);
        for (Object o : res) {
            Object[] row = (Object[]) o;
            frations.put(((BigInteger)row[0]).longValue(), (String)row[1]);
        }

        for (PreorderComplex preorderComplex: preorderComplexs)
        {
            dateMessage = new HashMap<Date, String>();
            infoType = new HashMap<String, Object>();
            Client client = preorderComplex.getClient();
            if (client != null) {
                //Если такого клиента пока не было
                if (result.get(client) == null)
                {
                    String mess = "";
                    createmassage(mess, preorderComplex, frations.get(preorderComplex.getIdOfPreorderComplex()), mode, dateMessage);
                    infoType.put(type, dateMessage);
                    result.put(client,infoType);
                }
                else
                {
                    infoType = (Map<String, Object>) result.get(client);
                    if (infoType.get(type) == null)
                    {
                        String mess = "";
                        createmassage(mess, preorderComplex, frations.get(preorderComplex.getIdOfPreorderComplex()), mode, dateMessage);
                        infoType.put(type, dateMessage);
                    }
                    else
                    {
                        dateMessage = (Map<Date, String>) infoType.get(type);
                        if (dateMessage.get(preorderComplex.getPreorderDate()) == null)
                        {
                            String mess = "";
                            createmassage(mess, preorderComplex, frations.get(preorderComplex.getIdOfPreorderComplex()), mode, dateMessage);
                        }
                        else
                        {
                            String mess = dateMessage.get(preorderComplex.getPreorderDate());
                            createmassage(mess, preorderComplex, frations.get(preorderComplex.getIdOfPreorderComplex()), mode, dateMessage);
                        }
                    }
                }
            }
        }
    }

    private void createmassage (String mess, PreorderComplex preorderComplex, String fration, Integer mode, Map<Date, String> dateMessage)
    {
        if (fration == null)
            fration = "";
        if (mess.isEmpty())
            mess += "<br>";
        if (mode == 3 || mode == 4)
        {
            //mess += "Комплексный рацион «" + preorderComplex.getComplexName().trim() + "»";
            mess += "Комплексный рацион «" + fration.trim() + "»";
        }
        if (mode == 1 || mode == 2) {
            //mess += preorderComplex.getComplexName().trim() + ":";
            mess += fration.trim() + ":";
            for (PreorderMenuDetail preorderMenuDetail : preorderComplex.getPreorderMenuDetails()) {
                if (preorderMenuDetail.getShortName() != null)
                    mess += "«" + preorderMenuDetail.getShortName().trim() + "»,";
            }
            if (mess.length() > 4)
                mess = mess.substring(0, mess.length() - 1);
        }
        mess += "<br>";
        dateMessage.put(preorderComplex.getPreorderDate(), mess);
    }

    private void createmassageregular (Session session, String mess, RegularPreorder regularPreorder, String fration, Integer mode,
            Map<Date, String> dateMessage)
    {
        if (fration == null)
            fration = "";
        if (mess.isEmpty())
            mess += "<br>";
        if (mode == 1) {
            //mess += "Комплексный рацион «" + regularPreorder.getItemName().trim() + "»";
            mess += "Комплексный рацион «" + fration.trim() + "»";
        }
        if (mode == 2) {
            ComplexInfo complexInfo = DAOUtils.getComplexInfoForRegular(session, regularPreorder);
            if (complexInfo != null) {
                mess += complexInfo.getIdOfComplex();
            }
            //mess += ": «" + regularPreorder.getItemName().trim() + "» ";
            mess += ": «" + fration.trim() + "» ";
        }
        mess += " (стоимость " + regularPreorder.getPrice() / 100 + " руб., ";
        mess += regularPreorder.getAmount() + " шт)";
        mess += "<br>";
        dateMessage.put(regularPreorder.getEndDate(), mess);
    }

    private List<PreorderComplex> getUniquleResult (List<PreorderComplex> preorderComplexs)
    {
        List<PreorderComplex> preorderComplexs1 = new ArrayList<>();
        for (PreorderComplex preorderComplex: preorderComplexs)
        {
            boolean good = true;
            for (PreorderComplex preorderComplex1: preorderComplexs1)
            {
                if (preorderComplex1.getClient() == preorderComplex.getClient() &&
                        preorderComplex1.getComplexName().trim().equals(preorderComplex.getComplexName().trim()) &&
                        preorderComplex1.getPreorderDate().equals(preorderComplex.getPreorderDate()) &&
                        preorderComplex1.getPreorderMenuDetails().equals(preorderComplex.getPreorderMenuDetails()))
                {
                    good = false;
                }
            }
            if (good)
            {
                preorderComplexs1.add(preorderComplex);
            }
        }
        return preorderComplexs1;
    }
    private List<RegularPreorder> getUniquleResultRegular (List<RegularPreorder> regularPreorders)
    {
        List<RegularPreorder> regularPreorders1 = new ArrayList<>();
        for (RegularPreorder regularPreorder: regularPreorders)
        {
            boolean good = true;
            for (RegularPreorder regularPreorder1: regularPreorders1)
            {
                if (regularPreorder1.getClient() == regularPreorder.getClient() &&
                        regularPreorder1.getEndDate().equals(regularPreorder.getEndDate()) &&
                        regularPreorder1.getItemName().trim().equals(regularPreorder.getItemName().trim()) &&
                        regularPreorder1.getPrice().equals(regularPreorder.getPrice()) &&
                        regularPreorder1.getAmount().equals(regularPreorder.getAmount()))
                {
                    good = false;
                }
            }
            if (good)
            {
                regularPreorders1.add(regularPreorder);
            }
        }
        return regularPreorders1;
    }
}
