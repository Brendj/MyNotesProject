/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.payment;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.atol.AtolPaymentNotificator;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 26.08.2019.
 */
@Component
@Scope("singleton")
@DependsOn("runtimeContext")
public class PaymentAdditionalTasksProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PaymentAdditionalTasksProcessor.class);
    List<IPaymentNotificator> listTasks;
    public static final String NOTIFICATORS_CONFIG_PROPERTY = "ecafe.processor.payment.notificators";

    public PaymentAdditionalTasksProcessor() {
        listTasks = new ArrayList<IPaymentNotificator>();
        String notificators = RuntimeContext.getInstance().getConfigProperties().getProperty(NOTIFICATORS_CONFIG_PROPERTY, "");
        String[] arr = notificators.split(",");
        for (String notificator : arr) {
            switch (notificator) {
                case "ATOL" : listTasks.add(new AtolPaymentNotificator());
                    break;
            }
        }
    }

    public void savePayment(Session session, ClientPayment clientPayment) {
        if (!isToSavePayment(clientPayment)) return;
        ClientPaymentAddon clientPaymentAddon = new ClientPaymentAddon(clientPayment);
        for (IPaymentNotificator notificator : listTasks) {
            notificator.addNotificatorValues(clientPaymentAddon, clientPayment);
        }
        session.save(clientPaymentAddon);
        //saveClientPaymentAddon(clientPaymentAddon);
    }

    private boolean isToSavePayment(ClientPayment clientPayment) {
        for (IPaymentNotificator notificator : listTasks) {
            if (notificator.isToSave(clientPayment)) return true;
        }
        return false;
    }

    public void runNotifications() {
        for (IPaymentNotificator notificator : listTasks) {
            notificator.sendNotifications();
        }
    }

    /*private void saveClientPaymentAddon(ClientPaymentAddon clientPaymentAddon) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            session.save(clientPaymentAddon);
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in save clientPaymentAddon object: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }*/
}
