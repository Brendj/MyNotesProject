/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianNotificationSetting;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 30.01.2018.
 */
@Component
public class RegularPaymentNotificationService {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Autowired
    EventNotificationService notificationService;

    private static Logger logger = LoggerFactory.getLogger(RegularPaymentNotificationService.class);

    private static final String VALUE_DATE_NEW_SUBSCRIPTION = "DateNewSubscription";

    @Transactional
    public void sendNotification(BankSubscription subscription) {
        if (subscription.getNotificationSent() != null && subscription.getNotificationSent()) return;
        Session session = em.unwrap(Session.class);
        String type = EventNotificationService.NOTIFICATION_EXPIRED_REGULAR_PAYMENT;
        Client client = (Client)session.load(Client.class, subscription.getClient().getIdOfClient());
        String[] values = getValuesBySubscription(subscription);
        try {
            if (client.getNotificationSettings().contains(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_REFILLS)) {
                notificationService
                        .sendNotificationExpiredSubscription(client, null, type, values, new Date(System.currentTimeMillis()));
            }
            List<Client> guardians = ClientManager
                    .findGuardiansByClient(session, client.getIdOfClient(), null);
            if (!(guardians == null || guardians.isEmpty())) {
                for (Client destGuardian : guardians) {
                    if (DAOReadonlyService.getInstance().allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                            client.getIdOfClient(), ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_REFILLS.getValue())) {
                        notificationService
                                .sendNotificationExpiredSubscription(destGuardian, client, type, values, new Date(System.currentTimeMillis()));
                    }
                }
            }
            subscription.setNotificationSent(true);
            em.merge(subscription);
        } catch (Exception e) {
            logger.error("Error sending notification expired regular payment: ", e);
        }
    }

    private String[] getValuesBySubscription(BankSubscription bs) {
        String[] result = new String[2];
        result[0] = VALUE_DATE_NEW_SUBSCRIPTION;
        result[1] = CalendarUtils.dateToString(CalendarUtils.addDays(bs.getValidToDate(), 1));
        return result;
    }
}
