/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientMigrationHistoryRepository;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ClientBalanceHoldService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User: shamil
 * Date: 04.12.14
 * Time: 11:15
 */
@Service
public class ClientMigrationHistoryService {

    @Autowired
    ClientMigrationHistoryRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(ClientMigrationHistoryService.class);
    private static final String NODE_CHANGE_ORG = "ecafe.processor.client.balance.serviceNode";

    public List<ClientMigration> findAll(Org org, Client client){
        return repository.findAll(org,client);
    }

    public void processOrgChange() {
        if(!isOnBalanceOrDiscount()) return;
        logger.info("Start process Org change service");
        Date lastProcess = repository.getDateLastOrgChangeProcess();
        List<ClientMigration> list = repository.findAllSinceDate(lastProcess);
        Date nextDate = new Date();
        int counter = 0;
        if(isOnBalanceHold()) {
            for (ClientMigration clientMigration : list) {
                if (clientMigration.getOldContragent() != null && clientMigration.getNewContragent() != null
                        && !clientMigration.getOldContragent().equals(clientMigration.getNewContragent())) {
                    //меняется контрагент, нужно заблокировать баланс
                    Client client = clientMigration.getClient();
                    if (client.getBalance() <= 0) {
                        continue;
                    }
                    try {
                        long holdSum = RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class)
                                .getClientBalanceHoldSum(client);
                        RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class)
                                .holdClientBalance(UUID.randomUUID().toString(), clientMigration.getClient(), holdSum,
                                        null, clientMigration.getOldOrg(), clientMigration.getOrg(),
                                        clientMigration.getOldContragent(), clientMigration.getNewContragent(),
                                        ClientBalanceHoldCreateStatus.CHANGE_SUPPLIER,
                                        ClientBalanceHoldRequestStatus.CREATED, null, null, null, null, null, null,
                                        null, null, ClientBalanceHoldLastChangeStatus.PROCESSING);
                        counter++;
                    } catch (Exception e) {
                        logger.error("Error in processOrgChange service: ", e);
                    }
                }
            }
        }

        if (isOnDiscountDelete()) {
            for (ClientMigration clientMigration : list) {
                if (!clientMigration.getOldOrg().equals(clientMigration.getOrg())) {
                    Session session = null;
                    Transaction transaction = null;

                    try {
                        session = RuntimeContext.getInstance().createPersistenceSession();
                        transaction = session.beginTransaction();
                        Client client = DAOUtils.findClient(session, clientMigration.getClient().getIdOfClient());
                        ClientManager.deleteDiscount(client, session);
                        transaction.commit();
                        transaction = null;
                        counter++;
                    } catch (Exception e) {
                        logger.error("Error in deleteDiscount");
                    } finally {
                        HibernateUtils.rollback(transaction, logger);
                        HibernateUtils.close(session, logger);
                    }
                }
            }
        }

        DAOService.getInstance().updateLastProcessOrgChange(nextDate);
        logger.info(String.format("End process Org change service. Processed %s client migration records records",
                counter));
    }

    private boolean isOnBalanceHold() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getPropertiesValue(NODE_CHANGE_ORG, "");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }

    private boolean isOnDiscountDelete() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext
                .getConfigProperties().getProperty("ecafe.processor.clientmigrationhistory.discountdelete", "false");
        return Boolean.parseBoolean(reqInstance);
    }

    private boolean isOnBalanceOrDiscount() {
        if(isOnBalanceHold()||isOnDiscountDelete()) {
            return true;
        }
        return false;
    }
}
