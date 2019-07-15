/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by i.semenov on 27.06.2017.
 */
@Component
@Scope("singleton")
public class ExternalEventNotificationService {

    Logger logger = LoggerFactory.getLogger(ExternalEventNotificationService.class);
    //@PersistenceContext(unitName = "reportsPU")
    //private EntityManager entityManager;

    public static String EMP_TIME = "empTime";
    public static String EMP_TIME_H = "empTimeH";
    public static String EMP_DATE = "empDate";
    public static String PLACE_NAME = "event_place";
    public static String PLACE_CODE = "event_place_code";
    public static String SURNAME = "surname";
    public static String NAME = "name";
    public static String ACCOUNT = "account";
    public static String BALANCE = "balance";
    public static String ADDRESS = "address";
    public static String SHORTNAMEINFOSERVICE = "shortnameinfoservice";
    private String cultureShortName;

    public void sendNotification(Client client, ExternalEvent event) throws Exception {
        String type = null;
        if (event.getEvtType().equals(ExternalEventType.MUSEUM)) {
            if (event.getEvtStatus().equals(ExternalEventStatus.TICKET_GIVEN)) {
                type = EventNotificationService.NOTIFICATION_ENTER_MUSEUM;
            } else if (event.getEvtStatus().equals(ExternalEventStatus.TICKET_BACK)) {
                type = EventNotificationService.NOTIFICATION_NOENTER_MUSEUM;
            }
        }
        if (event.getEvtType().equals(ExternalEventType.CULTURE)) {
            if (event.getEvtStatus().equals(ExternalEventStatus.TICKET_GIVEN)) {
                type = EventNotificationService.NOTIFICATION_ENTER_CULTURE;
            } else if (event.getEvtStatus().equals(ExternalEventStatus.TICKET_BACK)) {
                type = EventNotificationService.NOTIFICATION_EXIT_CULTURE;
            }
        }
        if (type == null) return;
        Session persistenceSession = null;
        Transaction transaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = persistenceSession.beginTransaction();
            String[] values = generateNotificationParams(client, event);
            values = EventNotificationService.attachGenderToValues(client.getGender(), values);
            final EventNotificationService notificationService = RuntimeContext.getAppContext().getBean(
                    EventNotificationService.class);
            List<Client> guardians = ClientManager.findGuardiansByClient(persistenceSession, client.getIdOfClient(), null);

            //отправка представителям
            if (!(guardians == null || guardians.isEmpty())) {
                for (Client destGuardian : guardians) {
                    //Если произошел проход в здание культуры или в здание музея...
                    if (ClientManager.allowedGuardianshipNotification(persistenceSession, destGuardian.getIdOfClient(),
                            client.getIdOfClient(), ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_MUSEUM.getValue())
                            || ClientManager.allowedGuardianshipNotification(persistenceSession, destGuardian.getIdOfClient(),
                            client.getIdOfClient(), ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_EVENTS.getValue())) {
                        notificationService
                                .sendNotificationAsync(destGuardian, client, type, values, event.getEvtDateTime());
                    }
                }
            }
            //отправка клиенту
            notificationService.sendNotificationAsync(client, null, type, values, event.getEvtDateTime());

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error sendNotification ExternalEvent:", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
    private String[] generateNotificationParams(Client client, ExternalEvent event) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(event.getEvtDateTime());
        if (event.getEvtType().equals(ExternalEventType.MUSEUM)) {
            return new String[] {
                    EMP_TIME, empTime,
                    PLACE_NAME, event.getOrgName(),
                    PLACE_CODE, event.getOrgCode(),
                    SURNAME, client.getPerson().getSurname(),
                    NAME, client.getPerson().getFirstName(),
                    ACCOUNT, client.getContractId().toString()
            };
        }
        if (event.getEvtType().equals(ExternalEventType.CULTURE)) {
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("dd.MM.YYYY");
            String empDate = dateFormat.format(event.getEvtDateTime());
            dateFormat = new SimpleDateFormat("HH:mm");
            String empTimeH = dateFormat.format(event.getEvtDateTime());
            String shortName = null;
            if (cultureShortName == null)
                shortName = event.getOrgName();
            else
                shortName = cultureShortName;
            return new String[] {
                    SURNAME, client.getPerson().getSurname(),
                    PLACE_NAME, event.getOrgName(),
                    EMP_DATE, empDate,
                    BALANCE, String.valueOf(client.getBalance()),
                    EMP_TIME, empTime,
                    EMP_TIME_H, empTimeH,
                    ADDRESS, event.getAddress(),
                    SHORTNAMEINFOSERVICE, shortName,
                    NAME, client.getPerson().getFirstName(),
                    ACCOUNT, client.getContractId().toString()
            };
        }
        return null;
    }

    public String getCultureShortName() {
        return cultureShortName;
    }

    public void setCultureShortName(String cultureShortName) {
        this.cultureShortName = cultureShortName;
    }
}
