/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.items.ClientDiscountItem;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
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
    public static String TEST = "TEST";
    public static String SHORTNAMEINFOSERVICE = "shortnameinfoservice";
    private String cultureShortName;
    private Date START_DATE;
    private Date END_DATE;

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
        if (event.getEvtType().equals(ExternalEventType.SPECIAL)) {
            if (event.getEvtStatus().equals(ExternalEventStatus.START_SICK)) {
                type = EventNotificationService.NOTIFICATION_START_SICK;
                logger.info("Тип сообщения - рекомендация об освобождении");
            } else if (event.getEvtStatus().equals(ExternalEventStatus.CANCEL_START_SICK)) {
                type = EventNotificationService.NOTIFICATION_CANCEL_START_SICK;
                logger.info("Тип сообщения - отмена рекомендации об освобождении");
            } else if (event.getEvtStatus().equals(ExternalEventStatus.END_SICK)) {
                type = EventNotificationService.NOTIFICATION_END_SICK;
                logger.info("Тип сообщения - рекомендация о посещении ОО");
            } else if (event.getEvtStatus().equals(ExternalEventStatus.CANCEL_END_SICK)) {
                type = EventNotificationService.NOTIFICATION_CANCEL_END_SICK;
                logger.info("Тип сообщения - отмена рекомендации о посещении ОО");
            }

        }
        if (type == null) {
            return;
        }
        Session persistenceSession = null;
        Transaction transaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = persistenceSession.beginTransaction();
            String[] values = generateNotificationParams(client, event);
            values = EventNotificationService.attachGenderToValues(client.getGender(), values);
            final EventNotificationService notificationService = RuntimeContext.getAppContext()
                    .getBean(EventNotificationService.class);
            List<Client> guardians = ClientManager
                    .findGuardiansByClient(persistenceSession, client.getIdOfClient(), null);
            Integer clas;
            try {
                clas = extractDigits(client.getClientGroup().getGroupName());
            } catch (NumberFormatException e) //т.е. в названии группы нет чисел
            {
                clas = 0;
            }
            //отправка представителям
            if (!(guardians == null || guardians.isEmpty())) {
                for (Client destGuardian : guardians) {
                    //Если произошел проход в здание культуры или в здание музея...
                    if (ClientManager.allowedGuardianshipNotification(persistenceSession, destGuardian.getIdOfClient(),
                            client.getIdOfClient(),
                            ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_MUSEUM.getValue()) && event
                            .getEvtType().equals(ExternalEventType.MUSEUM)) {
                        notificationService
                                .sendNotificationAsync(destGuardian, client, type, values, event.getEvtDateTime());
                    }
                    if (ClientManager.allowedGuardianshipNotification(persistenceSession, destGuardian.getIdOfClient(),
                            client.getIdOfClient(),
                            ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_CULTURE.getValue()) && event
                            .getEvtType().equals(ExternalEventType.CULTURE)) {
                        notificationService
                                .sendNotificationAsync(destGuardian, client, type, values, event.getEvtDateTime());
                    }
                    if (ClientManager.allowedGuardianshipNotification(persistenceSession, destGuardian.getIdOfClient(),
                            client.getIdOfClient(),
                            ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_SPECIAL.getValue()) && event
                            .getEvtType().equals(ExternalEventType.SPECIAL)) {
                        logger.info("Отправка кведомления представителю л/с" + destGuardian.getContractId());
                        if (clas > 0 && clas < 5)//1-4
                        {
                            //Если учащийся с 1-4 класс
                            logger.info("Учащийся в 1-4 классе");
                            notificationService
                                    .sendNotificationAsync(destGuardian, client, type, values, event.getEvtDateTime());
                            logger.info("Отправка уведомления прошла успешно");
                        } else {
                            logger.info("Учащийся не в 1-4 классе");
                            //Если есть социальная льгота
                            if (ClientHaveDiscount(persistenceSession, client)) {
                                logger.info("У учашегося есть льгота");
                                notificationService.sendNotificationAsync(destGuardian, client, type, values,
                                        event.getEvtDateTime());
                                logger.info("Отправка уведомления прошла успешно");
                            } else {
                                logger.info("У учашегося нет льготы");
                            }
                        }
                    }
                }
            }
            //отправка клиенту
            if (event.getEvtType().equals(ExternalEventType.SPECIAL)) { //Если тип = Служебные сообщения, то ....
                logger.info("Отправка уведомления клиенту");
                if (clas > 0 && clas < 5)//1-4
                {
                    //Если учащийся с 1-4 класс
                    logger.info("Учащийся в 1-4 классе");
                    notificationService.sendNotificationAsync(client, null, type, values, event.getEvtDateTime());
                    logger.info("Отправка уведомления прошла успешно");
                } else {
                    logger.info("Учащийся не в 1-4 классе");
                    //Только для НЕ предопределенной группы
                    ClientGroup.Predefined predefined = ClientGroup.Predefined
                            .parse(client.getClientGroup().getGroupName());
                    if (predefined == null) {
                        logger.info("Учашийся в непредопределенной группе");
                        //Если есть социальная льгота
                        if (ClientHaveDiscount(persistenceSession, client)) {
                            logger.info("У учашегося есть льгота");
                            notificationService
                                    .sendNotificationAsync(client, null, type, values, event.getEvtDateTime());
                            logger.info("Отправка уведомления прошла успешно");
                        } else {
                            logger.info("У учашегося нет льготы");
                        }
                    } else {
                        logger.info("Учашийся в предопределенной группе");
                    }
                }

            } else {
                //для всех других типов отправляем без условий
                notificationService.sendNotificationAsync(client, null, type, values, event.getEvtDateTime());
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error sendNotification ExternalEvent:", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public Integer extractDigits(String src) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            } else {
                return Integer.valueOf(builder.toString());
            }
        }
        return Integer.valueOf(builder.toString());
    }

    public boolean ClientHaveDiscount(Session session, Client client) {
        List<Long> categoriesDiscountsIds = new LinkedList<Long>();
        for (String cd : client.getCategoriesDiscounts().split(",")) {
            if (StringUtils.isNotEmpty(cd)) {
                categoriesDiscountsIds.add(Long.valueOf(cd));
            }
        }

        List<CategoryDiscount> clientDiscountsList = Collections.emptyList();
        if (!categoriesDiscountsIds.isEmpty()) {
            Criteria clientDiscountsCriteria = session.createCriteria(CategoryDiscount.class);
            clientDiscountsCriteria.add(Restrictions.in("idOfCategoryDiscount", categoriesDiscountsIds));
            clientDiscountsList = clientDiscountsCriteria.list();
        }
        boolean discount = false;
        for (CategoryDiscount categoryDiscount : clientDiscountsList) {
            if (categoryDiscount.getCategoryType().getDescription().equals("Льгота")) {
                discount = true;
                break;
            }
        }
        return discount;
    }

    private String[] generateNotificationParams(Client client, ExternalEvent event) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(event.getEvtDateTime());
        if (event.getEvtType().equals(ExternalEventType.MUSEUM)) {
            return new String[]{
                    EMP_TIME, empTime, PLACE_NAME, event.getOrgName(), PLACE_CODE, event.getOrgCode(), SURNAME,
                    client.getPerson().getSurname(), NAME, client.getPerson().getFirstName(), ACCOUNT,
                    client.getContractId().toString()};
        }
        if (event.getEvtType().equals(ExternalEventType.CULTURE)) {
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("dd.MM.YYYY");
            String empDate = dateFormat.format(event.getEvtDateTime());
            dateFormat = new SimpleDateFormat("HH:mm");
            String empTimeH = dateFormat.format(event.getEvtDateTime());
            String shortName = null;
            if (cultureShortName == null) {
                shortName = event.getOrgName();
            } else {
                shortName = cultureShortName;
            }
            return new String[]{
                    SURNAME, client.getPerson().getSurname(), PLACE_NAME, event.getOrgName(), EMP_DATE, empDate,
                    BALANCE, String.valueOf(client.getBalance()), EMP_TIME, empTime, EMP_TIME_H, empTimeH, ADDRESS,
                    event.getAddress(), SHORTNAMEINFOSERVICE, shortName, NAME, client.getPerson().getFirstName(),
                    ACCOUNT, client.getContractId().toString()};
        }
        if (event.getEvtType().equals(ExternalEventType.SPECIAL)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
            String empDate = dateFormat.format(getSTART_DATE());
            if (event.getForTest() != null && event.getForTest()) {
                return new String[]{
                        SURNAME, client.getPerson().getSurname(),
                        NAME, client.getPerson().getFirstName(),
                        PLACE_NAME, event.getOrgName(),
                        ACCOUNT, client.getContractId().toString(),
                        EMP_DATE, empDate,
                        PLACE_CODE, event.getOrgCode(),
                        "TEST", "true"
                };
            } else {
                return new String[]{
                        SURNAME, client.getPerson().getSurname(),
                        NAME, client.getPerson().getFirstName(),
                        PLACE_NAME, event.getOrgName(),
                        ACCOUNT, client.getContractId().toString(),
                        EMP_DATE, empDate,
                        PLACE_CODE, event.getOrgCode()
                        };
            }
        }
        return null;
    }

    public String getCultureShortName() {
        return cultureShortName;
    }

    public void setCultureShortName(String cultureShortName) {
        this.cultureShortName = cultureShortName;
    }

    public Date getSTART_DATE() {
        return START_DATE;
    }

    public void setSTART_DATE(Date START_DATE) {
        this.START_DATE = START_DATE;
    }

    public Date getEND_DATE() {
        return END_DATE;
    }

    public void setEND_DATE(Date END_DATE) {
        this.END_DATE = END_DATE;
    }
}
