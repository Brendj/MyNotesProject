/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.payment;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.atol.AtolPaymentNotificator;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 26.08.2019.
 */
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

    public void savePayment(ClientPayment clientPayment) {
        ClientPaymentAddon clientPaymentAddon = new ClientPaymentAddon(clientPayment);
        for (IPaymentNotificator notificator : listTasks) {
            notificator.addInitialValue(clientPaymentAddon);
        }
        saveClientPaymentAddon(clientPaymentAddon);
    }

    private void saveClientPaymentAddon(ClientPaymentAddon clientPaymentAddon) {
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
    }
}
