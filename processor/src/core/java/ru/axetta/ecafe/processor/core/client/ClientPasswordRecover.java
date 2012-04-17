/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.mail.File;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.12
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 * Класс для организации востановления пароля пользователя.
 */
public class ClientPasswordRecover {

    public final static int CONTRACT_SEND_RECOVER_PASSWORD=0;
    public final static int NOT_FOUND_CONTRACT_BY_ID=1;
    public final static int CONTRACT_HAS_NOT_EMAIL=2;

    private static final Logger logger = LoggerFactory.getLogger(ClientPasswordRecover.class);

    private final SessionFactory sessionFactory;

    public ClientPasswordRecover(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public int sendPasswordRecoverURLFromEmail(Long contractId,String emailText) throws Exception{
        int succeeded = -1;
        Transaction transaction = null;
        List<File> files = new LinkedList<File>();
        Session session = sessionFactory.openSession();
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            transaction = session.beginTransaction();
            Criteria clientWithSameContractIdCriteria = session.createCriteria(Client.class);
            clientWithSameContractIdCriteria.add(Restrictions.eq("contractId", contractId));
            Client currClient = (Client) clientWithSameContractIdCriteria.uniqueResult();
            succeeded = (null != currClient?0:-1);
            if (null != currClient) {
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            String.format("Client with contractId: %s is exist", contractId));
                }
                String clientEmail = currClient.getEmail();
                if(clientEmail == null || clientEmail.isEmpty() || clientEmail.equalsIgnoreCase("")){
                    succeeded = CONTRACT_HAS_NOT_EMAIL;
                } else {
                    runtimeContext.getSupportEmailSender().postSupportEmail(clientEmail, "Востановление пароля", emailText, files);
                    succeeded = CONTRACT_SEND_RECOVER_PASSWORD;
                    String sDate = (new Date()).toString();
                    logger.info("Send recover password URL send from '"+clientEmail+"' by "+sDate);
                }
            } else {
                succeeded = NOT_FOUND_CONTRACT_BY_ID;
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
       return succeeded;
    }
}
