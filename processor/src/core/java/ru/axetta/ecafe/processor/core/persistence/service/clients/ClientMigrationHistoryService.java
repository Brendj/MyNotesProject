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
    private static final String NODE_BALANCE_CHANGE_ORG = "ecafe.processor.client.balance.serviceNode";
    private static final String NODE_DISCOUNT_CHANGE_ORG = "ecafe.processor.clientMigrationHistory.discountDelete";

    public List<ClientMigration> findAll(Org org, Client client) {
        return repository.findAll(org, client);
    }

    public void processOrgChange() {
        if (!isOnBalanceOrDiscount()) { return; }
        logger.info("Start process Org change service");
        Date lastProcess = repository.getDateLastOrgChangeProcess();
        List<ClientMigration> list = repository.findAllSinceDate(lastProcess);
        Date nextDate = new Date();
        int counter = 0;
        if (isOn(NODE_BALANCE_CHANGE_ORG)) {
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

        if (isOn(NODE_DISCOUNT_CHANGE_ORG)) {
            for (ClientMigration clientMigration : list) {
                if (clientMigration.getOldOrg() == null) {
                    continue;
                }
                Session session = null;
                Transaction transaction = null;

                try {
                    session = RuntimeContext.getInstance().createPersistenceSession();
                    transaction = session.beginTransaction();
                    List<Org> friendlyOrgs = DAOUtils
                            .findFriendlyOrgs(session, clientMigration.getOldOrg().getIdOfOrg());
                    for (Org o : friendlyOrgs) {
                        if (o.getIdOfOrg().equals(clientMigration.getOrg().getIdOfOrg())) {
                            continue;
                        }
                    }
                    Client client = DAOUtils.findClient(session, clientMigration.getClient().getIdOfClient());
                    if (ClientManager.atLeastOneDiscountEligibleToDelete(client)) {
                        ClientManager.deleteDiscount(client, session);
                    }
                    ClientManager.archiveApplicationForFoodWithoutDiscount(client, session);

                    transaction.commit();
                    transaction = null;
                    counter++;
                } catch (Exception e) {
                    logger.error("Error in deleteDiscount", e);
                } finally {
                    HibernateUtils.rollback(transaction, logger);
                    HibernateUtils.close(session, logger);
                }
            }
        }

        DAOService.getInstance().updateLastProcessOrgChange(nextDate);
        logger.info(String.format("End process Org change service. Processed %s client migration records records",
                counter));
    }

    private boolean isOn(String nodeName) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getPropertiesValue(nodeName, "");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }

    private boolean isOnBalanceOrDiscount() {
        return (isOn(NODE_BALANCE_CHANGE_ORG) || isOn(NODE_DISCOUNT_CHANGE_ORG));
    }
}
