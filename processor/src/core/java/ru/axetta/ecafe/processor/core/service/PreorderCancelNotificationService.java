/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BlockUnblockItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.03.16
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("singleton")
public class PreorderCancelNotificationService {


    final static String CANCEL_PREORDER_NOTIFICATION = "CancelPreorderNotification";
    final EventNotificationService notificationService = RuntimeContext.getAppContext()
            .getBean(EventNotificationService.class);
    public static class sendNotification implements Job {

        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                RuntimeContext.getAppContext().getBean(PreorderCancelNotificationService.class)
                        .start(persistenceSession);
                persistenceTransaction.commit();
            } catch (Exception e) {
            }
        }

        public static void test() throws JobExecutionException {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                RuntimeContext.getAppContext().getBean(PreorderCancelNotificationService.class)
                        .start(persistenceSession);
                persistenceTransaction.commit();
            } catch (Exception e) {
                System.out.println("test fail");
            }
        }
    }

    //public static boolean isOn() {
    //    RuntimeContext runtimeContext = RuntimeContext.getInstance();
    //    String instance = runtimeContext.getNodeName();
    //    String reqInstance = runtimeContext.getConfigProperties().getProperty(PreorderCancelNotificationService.NODE_PROPERTY, "18");
    //    String[] nodes = reqInstance.split(",");
    //    for (String node : nodes) {
    //        if (!StringUtils.isBlank(instance) && !StringUtils.isBlank(reqInstance)
    //                && instance.trim().equals(node.trim())) {
    //            return true;
    //        }
    //    }
    //    return false;
    //}

    public void scheduleSync() throws Exception {
        //if (!isOn())
        //    return;
        String syncScheduleSync = RuntimeContext.getInstance().getConfigProperties().
                getProperty("ecafe.processor.card.blocked.cron", "0 10 3 ? * 2/2");
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


    public void start(Session session) throws Exception {
        Map<Client, Object> oldMessage = convertResultFromDB(session);
        //Map<Client, Object> newMessage = getNewNotification(session);
        //convertResultToDB(session, newMessage);



        Iterator<Map.Entry<Client, Object>> clients = oldMessage.entrySet().iterator();
        int counttype = 1;
        String value = "<br>";
        while (clients.hasNext()) {
            Map.Entry<Client, Object> client = clients.next();
            String[] values = new String[]{
                    "name", client.getKey().getPerson().getFirstName(),
                    "surname",client.getKey().getPerson().getSurname(),
                    "account", String.valueOf(client.getKey().getContractId()),
                    "balance", String.valueOf(client.getKey().getBalance())};
            values = EventNotificationService.attachGenderToValues(client.getKey().getGender(), values);
            List<Client> guardians = ClientManager
                    .findGuardiansByClient(session, client.getKey().getIdOfClient(), null);
            Iterator<Map.Entry<String, Object>> types =
                    ((Map<String, Object>)client.getValue()).entrySet().iterator();
            while (types.hasNext()) {
                Map.Entry<String, Object> type = types.next();
                counttype = 1;
                Iterator<Map.Entry<Date, String>> datemessages =
                        ((Map<Date, String>)type.getValue()).entrySet().iterator();
                while (datemessages.hasNext()) {
                    Map.Entry<Date, String> datemessage = datemessages.next();
                    value = "<br>";
                    value += new SimpleDateFormat("yyyy-MM-dd").format(datemessage.getKey());
                    value += datemessage.getValue();
                    values = EventNotificationService.attachToValues
                            (type.getKey() + "_day_" + counttype, value, values);
                    counttype++;
                }
            }

            ////отправка представителям
            if (!(guardians == null || guardians.isEmpty())) {
                for (Client destGuardian : guardians) {
                    if (ClientManager.allowedGuardianshipNotification(session, destGuardian.getIdOfClient(),
                            client.getKey().getIdOfClient(),
                            ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SPECIAL.getValue())) {
                        notificationService
                                .sendNotificationAsync(destGuardian, client.getKey(), EventNotificationService.NOTIFICATION_CANCEL_PREORDER, values, new Date());
                    }
                }
            }
            //отправка клиенту
            notificationService.sendNotificationAsync(client.getKey(), null, EventNotificationService.NOTIFICATION_CANCEL_PREORDER, values, new Date());
        }
    }


    public Map<Client, Object> getNewNotification(Session session) {

        Map<Client, Object> result =
                new HashMap<Client, Object>();
        List<PreorderComplex> preorderComplexs = DAOUtils.getContentDeletedPreorderDishOtherOO(session, new Date());
        preorderComplexs = getUniquleResult(preorderComplexs);
        createData(preorderComplexs, "contentDeletedPreorderDishOtherOO", 1, result);
        preorderComplexs = DAOUtils.getContentDeletedPreorderDishOtherPP(session, new Date());
        preorderComplexs = getUniquleResult(preorderComplexs);
        createData(preorderComplexs, "contentDeletedPreorderDishOtherPP", 2, result);
        preorderComplexs = DAOUtils.getContentDeletedPreorderOtherOO(session, new Date());
        preorderComplexs = getUniquleResult(preorderComplexs);
        createData(preorderComplexs, "contentDeletedPreorderOtherOO", 3, result);
        preorderComplexs = DAOUtils.getContentDeletedPreorderOtherPP(session, new Date());
        preorderComplexs = getUniquleResult(preorderComplexs);
        createData(preorderComplexs, "contentDeletedPreorderOtherPP", 4, result);
        List<RegularPreorder> regularPreorders = DAOUtils.getContentDeletedPreorderOtherRegularOO(session, new Date());
        regularPreorders = getUniquleResultRegular(regularPreorders);
        createDataRegularPreorder(session, regularPreorders, "contentDeletedPreorderOtherRegularOO", 1, result);
        regularPreorders = DAOUtils.getContentDeletedPreorderDishOtherRegularOO(session, new Date());
        regularPreorders = getUniquleResultRegular(regularPreorders);
        createDataRegularPreorder(session, regularPreorders, "contentDeletedPreorderDishOtherRegularOO", 2, result);
        return result;
    }

    private Map<Client, Object> convertResultFromDB (Session session) throws Exception {
        Map<Client, Object> result = new HashMap<>();
        List<CancelPreorderNotification> cancelPreorderNotifications = DAOUtils.getCancelPreorderNotification(session);
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
                    createmassageregular(session, mess, regularPreorder, mode, dateMessage);
                    infoType.put(type, dateMessage);
                    result.put(client,infoType);
                }
                else
                {
                    infoType = (Map<String, Object>) result.get(client);
                    if (infoType.get(type) == null)
                    {
                        String mess = "";
                        createmassageregular(session, mess, regularPreorder, mode, dateMessage);
                        infoType.put(type, dateMessage);
                    }
                    else
                    {
                        dateMessage = (Map<Date, String>) infoType.get(type);
                        if (dateMessage.get(regularPreorder.getEndDate()) == null)
                        {
                            String mess = "";
                            createmassageregular(session, mess, regularPreorder, mode, dateMessage);
                        }
                        else
                        {
                            String mess = dateMessage.get(regularPreorder.getEndDate());
                            createmassageregular(session, mess, regularPreorder, mode, dateMessage);
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
                    createmassage(mess, preorderComplex, mode, dateMessage);
                    infoType.put(type, dateMessage);
                    result.put(client,infoType);
                }
                else
                {
                    infoType = (Map<String, Object>) result.get(client);
                    if (infoType.get(type) == null)
                    {
                        String mess = "";
                        createmassage(mess, preorderComplex, mode, dateMessage);
                        infoType.put(type, dateMessage);
                    }
                    else
                    {
                        dateMessage = (Map<Date, String>) infoType.get(type);
                        if (dateMessage.get(preorderComplex.getPreorderDate()) == null)
                        {
                            String mess = "";
                            createmassage(mess, preorderComplex, mode, dateMessage);
                        }
                        else
                        {
                            String mess = dateMessage.get(preorderComplex.getPreorderDate());
                            createmassage(mess, preorderComplex, mode, dateMessage);
                        }
                    }
                }
            }
        }
    }

    private void createmassage (String mess, PreorderComplex preorderComplex, Integer mode, Map<Date, String> dateMessage)
    {
        if (mess.isEmpty())
            mess += "<br>";
        if (mode == 3 || mode == 4)
        {
            mess += "Комплексный рацион «" + preorderComplex.getComplexName().trim() + "»";
        }
        if (mode == 1 || mode == 2) {
            mess += preorderComplex.getComplexName().trim() + ":";
            for (PreorderMenuDetail preorderMenuDetail : preorderComplex.getPreorderMenuDetails()) {
                mess += "«" + preorderMenuDetail.getMenuDetailName().trim() + "»,";
            }
            if (mess.length() > 4)
                mess = mess.substring(0, mess.length() - 1);
        }
        mess += "<br>";
        dateMessage.put(preorderComplex.getPreorderDate(), mess);
    }

    private void createmassageregular (Session session, String mess, RegularPreorder regularPreorder, Integer mode,
            Map<Date, String> dateMessage)
    {
        if (mess.isEmpty())
            mess += "<br>";
        if (mode == 1) {
            mess += "Комплексный рацион «" + regularPreorder.getItemName().trim() + "»";

        }
        if (mode == 2) {
            ComplexInfo complexInfo = DAOUtils.getComplexInfoForRegular(session, regularPreorder);
            if (complexInfo != null) {
                mess += complexInfo.getIdOfComplex();
            }
            mess += ": «" + regularPreorder.getItemName().trim() + "» ";
        }
        mess += "(стоимость " + regularPreorder.getPrice() / 100 + " руб., ";
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
