/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.spb;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component("CardsUidUpdateService")
@Scope("singleton")
public class CardsUidUpdateService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CardsUidUpdateService.class);

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
}
