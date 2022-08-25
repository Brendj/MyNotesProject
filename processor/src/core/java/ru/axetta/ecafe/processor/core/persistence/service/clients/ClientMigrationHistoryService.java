/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.clients;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientMigrationHistoryRepository;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ClientBalanceHoldService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
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
    private static final String NODE_PLAN_ORDERS_RESTRICTIONS_CHANGE_ORG = "ecafe.processor.planOrdersRestrictions.serviceNode";
    private static final String NODE_TRANSFER_GUARDIANS = "ecafe.processor.clientMigrationHistory.transferGuardians.serviceNode";

    public List<ClientMigration> findAll(Org org, Client client) {
        return repository.findAll(org, client);
    }

    public void processOrgChange() {
        if (!isOnBalanceOrDiscount()) { return; }
        logger.info("Start process Org change service");
        Date lastProcess = repository.getDateLastOrgChangeProcess();
        Date nextDate = new Date();
        List<ClientMigration> list = repository.findAllSinceDate(lastProcess);
        int counter = 0;
        if (isOn(NODE_BALANCE_CHANGE_ORG)) {
            RuntimeContext.getAppContext().getBean(ClientMigrationHistoryService.class).balanceChange(list);
        }

        if (isOn(NODE_DISCOUNT_CHANGE_ORG)) {
            discountChange(list);
            //deleteDOUDiscounts();
        }

        if (isOn(NODE_PLAN_ORDERS_RESTRICTIONS_CHANGE_ORG)) {
            restrictionsChange(list);
        }

        if (isOn(NODE_TRANSFER_GUARDIANS)) {
            transferGuardians(list);
        }

        DAOService.getInstance().updateLastProcessOrgChange(nextDate);
        logger.info(String.format("End process Org change service. Processed %s client migration records",
                list.size()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public void balanceChange(List<ClientMigration> list) {
        int counter = 0;
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
                    logger.error("Error balanceChange in processOrgChange service: ", e);
                }
            }
        }
        if (counter > 0) logger.info(String.format("Processed %s balanceChange records", counter));
    }

    /*private void deleteDOUDiscounts(List<ClientMigration> list) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            for (ClientMigration clientMigration : list) {
                if (!(clientMigration.getOldOrg().getType().equals(OrganizationType.KINDERGARTEN) && clientMigration.getOrg().getType().equals(OrganizationType.SCHOOL))) {
                    continue;
                }
                try {
                    Client client = DAOUtils.findClient(session, clientMigration.getClient().getIdOfClient());
                    DiscountManager.deleteDOUDiscounts(session, client);
                } catch (Exception e) {
                    logger.error("Can not delete DOU discounts for client id = " + clientMigration.getClient().getIdOfClient(), e);
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in deleteDOUDiscounts", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }*/

    private void deleteDOUDiscounts() {
        logger.info("Start delete DOU discounts");
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Query query = session.createSQLQuery("create temp table temp_clients (idOfClient bigint, idoforg bigint, categoriesdiscounts character varying(60)) on commit drop");
            query.executeUpdate();
            logger.info("Создана временная таблица");

            query = session.createSQLQuery("INSERT INTO temp_clients(idofclient, idoforg, categoriesdiscounts) \n"
                    + "SELECT DISTINCT c.idofclient, c.idoforg, "
                    + "(SELECT array_to_string(array(SELECT idofcategorydiscount FROM cf_clients_categorydiscounts WHERE idofclient = c.idofclient), ',')) \n"
                    + "FROM cf_clients_categorydiscounts cg JOIN cf_clients c ON cg.idofclient = c.idofclient "
                    + "WHERE cg.idOfClient IN (select ccd.idofclient from cf_clients_categorydiscounts ccd \n"
                    + "join cf_clients c on ccd.idofclient = c.idofclient \n"
                    + "where idofcategorydiscount in (select idofcategorydiscount from cf_categorydiscounts where organizationtype = 1 and categorytype = 0 and deletedstate = 0) \n"
                    + "and c.agetypegroup is not null and c.agetypegroup <> '' and lower(c.agetypegroup) not like '%" + Client.DOU_STRING + "%')");
            int count = query.executeUpdate();
            if (count == 0) {
                logger.info("Льготы ДОУ, подлежащие удалению, не найдены");
                transaction.commit();
                transaction = null;
                return;
            }
            logger.info(String.format("Во временную таблицу добавлено %s записей", count));

            query = session.createSQLQuery("update cf_registry set clientregistryversion = clientregistryversion + 1");
            query.executeUpdate();
            logger.info("Обновили каталог версии клиентов");

            query = session.createSQLQuery("DELETE FROM cf_clients_categorydiscounts "
                    + "where idofclient in (select idofclient from temp_clients) "
                    + "and idofcategorydiscount in (select idofcategorydiscount from cf_categorydiscounts where organizationtype = :orgType and categorytype = :catType and deletedstate = 0)");
            query.setParameter("orgType", OrganizationType.KINDERGARTEN.getCode());
            query.setParameter("catType", CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT.getValue());
            count = query.executeUpdate();
            logger.info(String.format("Удалено %s льгот", count));

            query = session.createSQLQuery("UPDATE cf_clients c SET clientregistryversion=(SELECT clientregistryversion FROM cf_registry),"
                    + "discountmode = (select case when cnt=0 then 0 else 3 end from (select coalesce(count(*), 0) as cnt from cf_clients_categorydiscounts cg where cg.idofclient = c.idofclient) sub) "
                    + "WHERE idofclient IN (SELECT idofclient FROM temp_clients)");
            count = query.executeUpdate();
            logger.info(String.format("Обновлена версия у %s клиентов", count));

            query = session.createSQLQuery("INSERT INTO cf_discountchangehistory(idofclient, registrationdate, discountmode, olddiscountmode, "
                    + "categoriesdiscounts, oldcategoriesdiscounts, idoforg, comment) "
                    + "SELECT tc.idofclient, trunc(EXTRACT(EPOCH FROM now()) * 1000), "
                    + "CASE WHEN coalesce((SELECT array_to_string(array(SELECT idofcategorydiscount FROM cf_clients_categorydiscounts WHERE idofclient = c.idofclient), ',')), '') = '' THEN 0 ELSE 3 END, 3, coalesce((SELECT array_to_string(array(SELECT idofcategorydiscount FROM cf_clients_categorydiscounts WHERE idofclient = c.idofclient), ',')), ''), tc.categoriesdiscounts, tc.idoforg, :comment "
                    + "FROM temp_clients tc JOIN cf_clients c ON c.idofclient = tc.idofclient");
            query.setParameter("comment", DiscountChangeHistory.MODIFY_BY_TRANSITION);
            count = query.executeUpdate();
            logger.info(String.format("Добавлено %s записей в таблицу истории льгот", count));

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in deleteDOUDiscounts", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        logger.info("End delete DOU discounts");
    }

    private void discountChange(List<ClientMigration> list) {
        int counter = 0;
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
                boolean skipClient = false;
                for (Org o : friendlyOrgs) {
                    if (o.getIdOfOrg().equals(clientMigration.getOrg().getIdOfOrg())) {
                        skipClient = true;
                        break;
                    }
                }
                if (skipClient) continue;
                Client client = DAOUtils.findClient(session, clientMigration.getClient().getIdOfClient());
                if (DiscountManager.atLeastOneDiscountEligibleToDelete(client)) {
                    DiscountManager.deleteDiscount(client, session);
                    counter++;
                }
                ClientManager.archiveApplicationForFoodWithoutDiscount(client, session);

                transaction.commit();
                transaction = null;
            } catch (Exception e) {
                logger.error("Error in deleteDiscount", e);
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        }
        if (counter > 0) logger.info(String.format("Deleted discounts from %s clients", counter));
    }

    private void restrictionsChange(List<ClientMigration> list) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Long nextVersion = DAOUtils.nextVersionByTableWithoutLock(session, "cf_plan_orders_restrictions");
            for (ClientMigration clientMigration : list) {
                if (clientMigration.getOrg().equals(clientMigration.getOldOrg())) continue;
                List<PlanOrdersRestriction> planOrdersRestrictions = DAOUtils.getPlanOrdersRestrictionByClient(session, clientMigration.getClient().getIdOfClient());
                for (PlanOrdersRestriction planOrdersRestriction : planOrdersRestrictions) {
                    planOrdersRestriction.setVersion(nextVersion);
                    planOrdersRestriction.setLastUpdate(new Date());
                    session.update(planOrdersRestriction);
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in change org for plan orders restrictions: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private void transferGuardians(List<ClientMigration> list) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            for (ClientMigration clientMigration : list) {
                if (clientMigration.getOldOrg() == null || clientMigration.getOrg().equals(clientMigration.getOldOrg())) continue;
                if (DAOUtils.isFriendlyOrganizations(session, clientMigration.getOrg(), clientMigration.getOldOrg())) continue;
                List<Client> guardians = ClientManager.findGuardiansByClient(session, clientMigration.getClient().getIdOfClient(), true);
                for (Client guardian : guardians) {
                    boolean hasChildrenInOtherOrg = false;
                    List<Client> children = ClientManager.findChildsByClient(session, guardian.getIdOfClient(), true, false);
                    for (Client child : children) {
                        if (!child.equals(clientMigration.getClient()) && !DAOUtils.isFriendlyOrganizations(session, child.getOrg(), clientMigration.getOrg())) {
                            hasChildrenInOtherOrg = true;
                            break;
                        }
                    }
                    if (!hasChildrenInOtherOrg && guardian.isParent()) {
                        //нет детей в других организациях. Можно перевести родителя в ОО ребенка
                        guardian.setOrg(clientMigration.getOrg());
                        guardian.setUpdateTime(new Date());
                        long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
                        guardian.setClientRegistryVersion(clientRegistryVersion);
                        session.update(guardian);

                        ClientManager.addClientMigrationEntry(session, clientMigration.getOldOrg(), null, clientMigration.getOrg(),
                                guardian, ClientGroupMigrationHistory.MODIFY_AUTO_MODE, guardian.getClientGroup().getGroupName());
                    }
                    if (guardian.isSotrudnikMsk() || hasChildrenInOtherOrg) {
                        //есть дети в других ОО, создаем заявку на посещение
                        ClientManager.createMigrationForGuardianWithConfirm(session, guardian, new Date(), clientMigration.getOrg(),
                                MigrantInitiatorEnum.INITIATOR_PROCESSING, VisitReqResolutionHistInitiatorEnum.INITIATOR_ISPP, 12);
                    }
                }
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in change org for transfer guardians: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
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
        return (isOn(NODE_BALANCE_CHANGE_ORG) || isOn(NODE_DISCOUNT_CHANGE_ORG)
                || isOn(NODE_PLAN_ORDERS_RESTRICTIONS_CHANGE_ORG) || isOn(NODE_TRANSFER_GUARDIANS));
    }
}
