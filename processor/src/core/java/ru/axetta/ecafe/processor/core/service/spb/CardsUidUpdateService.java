/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.spb;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Component("CardsUidUpdateService")
@Scope("singleton")
public class CardsUidUpdateService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CardsUidUpdateService.class);
    public static final String BALANCE_TRANSFER = "Объединение учетных записей";

    public void updateCards(List<Long> orgs) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            CardManager cardManager = RuntimeContext.getInstance().getCardManager();
            for (Long idOfOrg : orgs) {
                List<Client> clients = DAOUtils.findClientsByOrg(session, idOfOrg);
                logger.info(String.format("CardsUidUpdateService: idOfOrg = %d clients count = %d", idOfOrg, clients.size()));
                for (Client client : clients) {
                    try {
                        Long clientGuid = Long.parseLong(client.getClientGUID());
                        for (Card card : DAOUtils.getAllCardByClientAndCardState(session, client, CardState.ISSUED)) {
                            if (card.getCardNo().equals(clientGuid)) {
                                Long newCardNo = Long.parseLong(Long.toString(card.getCardNo() * 1000 + 1), 16);
                                Long newCardPrintedNo = card.getCardNo() * 1000 + 1;
                                cardManager.updateCard(card.getClient().getIdOfClient(), card.getIdOfCard(), card.getCardType(),
                                        CardState.BLOCKED.getValue(), card.getValidTime(), card.getLifeState(), card.getLockReason(),
                                        card.getIssueTime(), card.getExternalId());
                                cardManager.createCard(client.getIdOfClient(), newCardNo, card.getCardType(),
                                        CardState.ISSUED.getValue(), CalendarUtils.addYear(new Date(), 10),
                                        Card.ISSUED_LIFE_STATE, null, new Date(), newCardPrintedNo);
                            }
                        }
                    } catch (NumberFormatException e) {
                        logger.warn(String.format("Client with clientId = %d have unexpected clientGuid = %s",
                                client.getIdOfClient(), client.getClientGUID()));
                    }
                }
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void processClientDoubles(Long idOfOrg, ClientGuardianHistory clientGuardianHistory) throws Exception {
        logger.info("Start processClientDoubles");
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Query query = session.createSQLQuery("SELECT c1.idofclient as clientTo, c2.idofclient as clientFrom "
                    + "FROM cf_clients c1 JOIN cf_persons p1 ON c1.idofperson = p1.idofperson "
                    + "JOIN cf_persons p2 ON p1.surname = p2.surname AND p1.firstname = p2.firstname AND p1.secondname = p2.secondname "
                    + "JOIN cf_clients c2 ON c2.idofperson = p2.idofperson "
                    + "WHERE c1.idofclient <> c2.idofclient AND c1.contractdate > c2.contractdate AND c1.idofclientgroup < :group AND c2.idofclientgroup < :group "
                    + "AND c1.idoforg = :idOfOrg AND c2.idoforg = :idOfOrg");
            query.setParameter("group", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("idOfOrg", idOfOrg);
            List list = query.list();
            logger.info(String.format("Found %s client doubles", list.size()));
            User user = DAOReadonlyService.getInstance().getUserFromSession();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                Long idOfClientBalanceTo = ((BigInteger)row[0]).longValue();
                Client toClient = (Client)session.load(Client.class, idOfClientBalanceTo);
                Long idOfClientBalanceFrom = ((BigInteger)row[1]).longValue();
                Client fromClient = (Client)session.load(Client.class, idOfClientBalanceFrom);
                if (toClient.getBalance() > 0) {
                    logger.info(String.format("Баланс клиента ид=%s = %s. Пропускаем", idOfClientBalanceTo, toClient.getBalance()));
                    continue;
                }
                if (fromClient.getBalance() > 0) {
                    logger.info(String.format("Перевод баланса клиента ид=%s клиенту ид=%s, сумма %s",
                            idOfClientBalanceFrom, idOfClientBalanceTo, fromClient.getBalance()));
                    RuntimeContext.getFinancialOpsManager()
                            .createAccountTransfer(session, fromClient, toClient, fromClient.getBalance(), BALANCE_TRANSFER, user);
                }
                ClientManager.createClientGroupMigrationHistory(session, fromClient, fromClient.getOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                        ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup(), BALANCE_TRANSFER + " Пользователь: "
                                + user.getUserName(), clientGuardianHistory);
                fromClient.setIdOfClientGroup(ClientGroup.Predefined.CLIENT_LEAVING.getValue());
                long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
                fromClient.setClientRegistryVersion(clientRegistryVersion);
                session.update(fromClient);
                for (Card card : fromClient.getCards()) {
                    if (card.isActive()) {
                        RuntimeContext.getInstance().getCardManager()
                                .updateCardInSession(session, idOfClientBalanceFrom, card.getIdOfCard(), card.getCardType(),
                                        CardState.BLOCKED.getValue(), card.getValidTime(), card.getLifeState(),
                                        BALANCE_TRANSFER,
                                        card.getIssueTime(), card.getExternalId(),
                                        user, fromClient.getOrg().getIdOfOrg(), "", false);
                    }
                }

            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
            logger.info("End processClientDoubles");
        }
    }
}
