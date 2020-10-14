/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentOrder;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 07.07.16
 * Time: 10:37
 */
public class ClientPaymentOrderProcessorImpl implements ClientPaymentOrderProcessor {
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private final SessionFactory persistenceSessionFactory;
    private final EventNotificator eventNotificator;

    public ClientPaymentOrderProcessorImpl(SessionFactory persistenceSessionFactory, EventNotificator eventNotificator) {
        this.persistenceSessionFactory = persistenceSessionFactory;
        this.eventNotificator = eventNotificator;
    }

    @Override
    public Long createPaymentOrder(Long idOfClient, Long idOfContragent, int paymentMethod, Long sum,
            Long contragentSum) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Contragent contragent = findContragent(persistenceSession, idOfContragent);
            Client client = getClientReference(persistenceSession, idOfClient);
            ClientPaymentOrder clientPaymentOrder = new ClientPaymentOrder(contragent, client, paymentMethod, sum,
                    contragentSum, new Date());
            persistenceSession.save(clientPaymentOrder);
            Long idOfClientPaymentOrder = clientPaymentOrder.getIdOfClientPaymentOrder();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfClientPaymentOrder;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Override
    public void changePaymentOrderStatus(Long idOfClient, Long idOfClientPaymentOrder, int orderStatus)
            throws Exception {
        if (ClientPaymentOrder.ORDER_STATUS_TRANSFER_ACCEPTED == orderStatus
                || ClientPaymentOrder.ORDER_STATUS_TRANSFER_COMPLETED == orderStatus) {
            throw new IllegalArgumentException("Unacceptable orderStatus");
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            ClientPaymentOrder clientPaymentOrder = getClientPaymentOrderReference(persistenceSession,
                    idOfClientPaymentOrder);
            Client client = getClientReference(persistenceSession, idOfClient);
            if (!client.getIdOfClient().equals(clientPaymentOrder.getClient().getIdOfClient())) {
                throw new IllegalArgumentException("Client doesn't own this order");
            }
            if (clientPaymentOrder.canApplyOrderStatus(orderStatus)) {
                clientPaymentOrder.setOrderStatus(orderStatus);
                persistenceSession.update(clientPaymentOrder);
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Override
    public void changePaymentOrderStatus(Long idOfContragent, Long idOfClientPaymentOrder, int orderStatus,
            Long contragentSum, String idOfPayment, String addIdOfPayment) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "IdOfContragent: %d, IdOfClientPaymentOrder: %d, OrderStatus: %d, ContragentSum: %d, IdOfPayment: %s",
                    idOfContragent, idOfClientPaymentOrder, orderStatus, contragentSum, idOfPayment));
        }
        if (!(ClientPaymentOrder.ORDER_STATUS_TRANSFER_ACCEPTED == orderStatus
                || ClientPaymentOrder.ORDER_STATUS_TRANSFER_COMPLETED == orderStatus)) {
            throw new IllegalArgumentException(String.format("Unacceptable OrderStatus: %d", orderStatus));
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            ClientPaymentOrder clientPaymentOrder = getClientPaymentOrderReference(persistenceSession,
                    idOfClientPaymentOrder);
            if (!idOfContragent.equals(clientPaymentOrder.getContragent().getIdOfContragent())) {
                throw new IllegalArgumentException(String.format(
                        "Contragent doesn't own this order, IdOfCOntragnet: %d, ClientPaymentOrder is: %s",
                        idOfContragent, clientPaymentOrder));
            }
            if (!contragentSum.equals(clientPaymentOrder.getContragentSum())) {
                logger.warn(
                        String.format("Invalid sum: %d, ClientPaymentOrder: %s", contragentSum, clientPaymentOrder));
            }
            ClientPayment clientPayment = null;
            if (clientPaymentOrder.canApplyOrderStatus(orderStatus)) {
                clientPaymentOrder.setOrderStatus(orderStatus);
                clientPaymentOrder.setIdOfPayment(idOfPayment);
                persistenceSession.update(clientPaymentOrder);
                if (ClientPaymentOrder.ORDER_STATUS_TRANSFER_COMPLETED == orderStatus) {
                    Client client = clientPaymentOrder.getClient();
                    clientPayment = RuntimeContext.getFinancialOpsManager()
                            .createClientPaymentWithOrder(persistenceSession, clientPaymentOrder, client,
                                    addIdOfPayment);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (clientPayment != null) {
                RuntimeContext.getAppContext().getBean(PaymentNotificator.class)
                        .sendNotification(clientPayment, clientPaymentOrder.getClient(), null);
            }
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
}
