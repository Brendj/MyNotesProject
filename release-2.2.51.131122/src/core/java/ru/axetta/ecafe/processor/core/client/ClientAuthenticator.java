/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.09.2009
 * Time: 16:04:58
 * To change this template use File | Settings | File Templates.
 */
public class ClientAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(ClientAuthenticator.class);
    private final SessionFactory sessionFactory;

    public ClientAuthenticator(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean checkClientCredentials(Long contractId, String plainPassword) throws Exception {
        boolean succeeded;
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Criteria clientWithSameContractIdCriteria = session.createCriteria(Client.class);
            clientWithSameContractIdCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientWithSameContractIdCriteria.uniqueResult();
            succeeded = null != client && client.hasPassword(plainPassword);
            if (succeeded) {
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            String.format("Client with contractId: %s - password validation successfull", contractId));
                }
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return succeeded;
    }

    public boolean checkSSOCredentials(Long contractId, String plainPassword) throws Exception {
        boolean succeeded = false;
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Criteria clientWithSameContractIdCriteria = session.createCriteria(Client.class);
            clientWithSameContractIdCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientWithSameContractIdCriteria.uniqueResult();
            succeeded = null != client;
            if (succeeded) {
                Org org = client.getOrg();
                succeeded = org.hasSsoPassword(plainPassword);
                if (succeeded) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(
                                String.format("Client with contractId: %s - SSO validation successfull", contractId));
                    }
                }
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return succeeded;
    }

    /*
    обновляем список функций в базе
     */
    public void initUserFunctions() throws Exception {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            User adminUser = DAOUtils.findUser(session, "admin");
            adminUser.getFunctions().clear();
            List<Function> funcList=Function.getFuncList();
            for (Function f : funcList) {
                Function fStored=(Function)session.get(Function.class, f.getIdOfFunction());
                if (fStored==null) {
                    session.save(f);
                } else {
                    f=(Function)session.merge(f);
                }           
                if (!adminUser.getFunctions().contains(f)) adminUser.getFunctions().add(f);
            }
            session.saveOrUpdate(adminUser);
            /////
            //session.createQuery("DELETE FROM Function WHERE idOfFunction>15").executeUpdate();

            Criteria allFunctionsCriteria = session.createCriteria(Function.class);
            List allFunctions = allFunctionsCriteria.list();
            allFunctions.size();
            /////
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}
